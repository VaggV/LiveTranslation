package com.vaggv.livetranslation;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.vaggv.livetranslation.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int PERMISSION_REQUESTS = 1;

    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private ImageAnalysis imageAnalyzer;
    private ActivityMainBinding binding;
    private Button getTextBtn;
    private ArrayList<String> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        previewView = findViewById(R.id.previewView);
        cameraExecutor = Executors.newSingleThreadExecutor();
        imageAnalyzer = getImageAnalyzer();
        getTextBtn = findViewById(R.id.getTextBtn);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions();
        }

        results = new ArrayList<>();

        getTextBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("results", results);

            startActivity(intent);

        });
    }

    private void startCamera(){
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            Preview preview = new Preview.Builder().build();

            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            try {
                bind(cameraProviderFuture.get(), imageAnalyzer, preview);
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Binding failed", e);
            }

        }, ContextCompat.getMainExecutor(this));
    }

    public synchronized ImageAnalysis getImageAnalyzer(){
        if (imageAnalyzer == null) {
            imageAnalyzer = new ImageAnalysis.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                    .build();
            imageAnalyzer.setAnalyzer(cameraExecutor, new TextReaderAnalyzer());
        }

        return imageAnalyzer;
    }

    private void bind(@NonNull ProcessCameraProvider cameraProvider, ImageAnalysis imageAnalyzer, Preview preview){
        cameraProvider.unbindAll();
        cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0){
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e){
            return new String[0];
        }
    }

    private boolean allPermissionsGranted(){
        for (String permission : getRequiredPermissions()) {
            if(!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void requestPermissions(){
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()){
            if (!isPermissionGranted(this, permission)){
                allNeededPermissions.add(permission);
            }
        }

        if(!allNeededPermissions.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private static boolean isPermissionGranted(Context context, String permission){
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUESTS){
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(this, "Permissions not granted.", Toast.LENGTH_SHORT).show();
                finish(); // Close the app if the user doesn't grant access (it's useless without it)
            }
        }
    }

    public class TextReaderAnalyzer implements ImageAnalysis.Analyzer{
        @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            Image mediaImage = imageProxy.getImage();
            if (mediaImage == null) return;

            process(imageProxy.getImage(), imageProxy);
        }

        private void process(Image image, ImageProxy imageProxy) {
            readTextFromImage(InputImage.fromMediaImage(image, 90), imageProxy);
        }

        private void readTextFromImage(InputImage image, ImageProxy imageProxy){
            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    .process(image)
                    .addOnSuccessListener(text -> {
                        processTextFromImage(text);
                        imageProxy.close();
                    });
        }

        private void processTextFromImage(Text text) {
            int count = binding.getRoot().getChildCount();

            /*// Remove the text graphics so that we can redraw them
            for (int i = 1; i <= count; i++) {
                if (binding.getRoot().getChildAt(i) instanceof TextGraphic){
                    binding.getRoot().removeViewAt(i);
                }
            }*/

            previewView.removeViews(1, previewView.getChildCount() - 1);

            results.clear();

            for (Text.TextBlock textBlock : text.getTextBlocks()) {
                results.add(textBlock.getText());
                previewView.addView(new TextGraphic(MainActivity.this, textBlock));
            }
        }
    }

}

