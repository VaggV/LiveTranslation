package com.vaggv.livetranslation;

import static com.vaggv.livetranslation.Utils.languages;
import static com.vaggv.livetranslation.Utils.toFullLangString;
import static com.vaggv.livetranslation.Utils.toShortLangString;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.common.internal.ImageUtils;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.vaggv.livetranslation.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
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
    private FloatingActionButton flashlightButton, settingsButton;
    private ArrayList<String> results;
    private TextView srcText, srcLang, translatedText, progressText;
    private LanguageIdentifier languageIdentifier;
    private ProgressBar progressBar;
    private RemoteModelManager modelManager;
    private TranslatorOptions options;
    private Translator translator;
    private DownloadConditions conditions;
    private Spinner targetLangSelector;
    private FirebaseAuth firebaseAuth;
    private Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initializations start
        previewView = findViewById(R.id.previewView);
        getTextBtn = findViewById(R.id.getTextBtn);
        srcText = findViewById(R.id.srcText);
        srcLang = findViewById(R.id.srcLang);
        translatedText = findViewById(R.id.translatedText);
        targetLangSelector = findViewById(R.id.targetLangSelector);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        flashlightButton = findViewById(R.id.flashlightButton);
        settingsButton = findViewById(R.id.settingsButton);

        firebaseAuth = FirebaseAuth.getInstance();
        modelManager = RemoteModelManager.getInstance();
        conditions = new DownloadConditions.Builder().requireWifi().build();
        languageIdentifier = LanguageIdentification.getClient(
                new LanguageIdentificationOptions.Builder().setConfidenceThreshold(0.34f).build());
        cameraExecutor = Executors.newSingleThreadExecutor();
        imageAnalyzer = getImageAnalyzer();
        // Initializations end

        // Check for permissions
        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions();
        }

        results = new ArrayList<>();

        // Open ResultActivity on click
        getTextBtn.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("results", results);

            startActivity(intent);
        });

        // Set the languages list to the dropdown
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, languages);
        targetLangSelector.setAdapter(adapter);

        // Flashlight toggle
        final boolean[] isTorchOn = {false};
        flashlightButton.setOnClickListener(view -> {
            if ( camera.getCameraInfo().hasFlashUnit() ) {
                camera.getCameraControl().enableTorch(!isTorchOn[0]);
                isTorchOn[0] = !isTorchOn[0];

                if (isTorchOn[0]) flashlightButton.setImageResource(R.drawable.flash_off_icon);
                else flashlightButton.setImageResource(R.drawable.flash_icon);
            }
        });

        settingsButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
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
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3).build();
            imageAnalyzer.setAnalyzer(cameraExecutor, new TextReaderAnalyzer());
        }
        return imageAnalyzer;
    }

    private void bind(@NonNull ProcessCameraProvider cameraProvider, ImageAnalysis imageAnalyzer, Preview preview){
        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer);
        System.out.println("CAMERA INFO: " + cameraProvider.getAvailableCameraInfos());
    }


    public class TextReaderAnalyzer implements ImageAnalysis.Analyzer{
        @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            Image image = imageProxy.getImage();
            if (image == null) return;

            InputImage inputImage = InputImage.fromMediaImage(image, 90);

            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                    .process(inputImage)
                    .addOnSuccessListener(text -> processTextFromImage(text, imageProxy));

            try {
                Thread.sleep(2000); // Analyze every 2 seconds
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        private void processTextFromImage(Text text, ImageProxy imageProxy) {
            String txt = text.getText();
            srcText.setText(txt);

            languageIdentifier.identifyLanguage(txt)
                    .addOnSuccessListener(result -> {
                        if (result.equals("und")) srcLang.setText("Can't identify language.");
                        else {
                            System.out.println("RESULT IS: " + result);
                            srcLang.setText(Utils.toFullLangString(result));
                            System.out.println("Target lang: " + targetLangSelector.getSelectedItem().toString());
                            translate(txt, result.replace("-Latn", ""), toShortLangString(targetLangSelector.getSelectedItem().toString()), targetLangSelector.getSelectedItem().toString());
                        }
                    });

            imageProxy.close();
        }

        private void translate(String text, String sourceLang, String targetLang, String temp){
            System.out.println("++++++++");
            System.out.println("SOURCE LANG: " + sourceLang);
            System.out.println("TARGET LANG: " + targetLang);
            if (sourceLang.equals("")){
                Toast.makeText(MainActivity.this, "Source lang string is empty: " + temp, Toast.LENGTH_LONG).show();
                return;
            }

            if (targetLang.equals("")){
                Toast.makeText(MainActivity.this, "Target lang string is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            options = new TranslatorOptions.Builder()
                    .setSourceLanguage(sourceLang)
                    .setTargetLanguage(targetLang)
                    .build();

            translator = Translation.getClient(options);

            TranslateRemoteModel model1 = new TranslateRemoteModel.Builder(targetLang).build();
            TranslateRemoteModel model2 = new TranslateRemoteModel.Builder(sourceLang).build();
            modelManager.isModelDownloaded(model1).addOnSuccessListener(isDownloaded -> {
                if (!isDownloaded) {
                    Toast.makeText(MainActivity.this, "Downloading model " + targetLang, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                    progressText.setVisibility(View.VISIBLE);
                }
            });
            modelManager.isModelDownloaded(model2).addOnSuccessListener(isDownloaded -> {
                if (!isDownloaded) {
                    Toast.makeText(MainActivity.this, "Downloading model " + sourceLang, Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.VISIBLE);
                    progressText.setVisibility(View.VISIBLE);
                }
            });

            translator.downloadModelIfNeeded(conditions).addOnSuccessListener(unused -> {
                progressBar.setVisibility(View.INVISIBLE);
                progressText.setVisibility(View.INVISIBLE);
                translator.translate(text).addOnSuccessListener(s -> {
                    translatedText.setText(s);
                    System.out.println("SAVING EVENT");
                    saveEvent(text, toFullLangString(sourceLang), s, toFullLangString(targetLang));
                }).addOnFailureListener(e -> {
                    Toast.makeText(MainActivity.this, "Failed to translate.", Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                Toast.makeText(MainActivity.this, "Couldn't download translation model.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Error while trying to download translation model.", e);
                progressBar.setVisibility(View.INVISIBLE);
                progressText.setVisibility(View.INVISIBLE);
            });


        }

        private void saveEvent(String originaltext, String textlang, String translatedtext, String translatedtextlang){
            try {
                String userid;
                if (firebaseAuth.getCurrentUser() != null) userid = firebaseAuth.getCurrentUser().getEmail();
                else userid = "null";

                RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                String url = "http://192.168.1.18:8080/api/translations";
                JSONObject jsonBody = new JSONObject();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());

                System.out.println("TIMESTAMP IS: " + timestamp);

                jsonBody.put("timestamp", timestamp);
                jsonBody.put("location", "Greece");
                jsonBody.put("userid", userid);
                jsonBody.put("originaltext", originaltext);
                jsonBody.put("textlang", textlang);
                jsonBody.put("translatedtext", translatedtext);
                jsonBody.put("translatedtextlang", translatedtextlang);

                final String mRequestBody = jsonBody.toString();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> Toast.makeText(MainActivity.this, "Successfully saved event", Toast.LENGTH_LONG).show(),
                        error -> Toast.makeText(MainActivity.this, "Error while saving event", Toast.LENGTH_LONG).show()) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() throws AuthFailureError {
                        return mRequestBody.getBytes(StandardCharsets.UTF_8);
                    }
                };

                requestQueue.add(stringRequest);
            } catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    // Permissions handling methods
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

}

