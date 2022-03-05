package com.vaggv.livetranslation;

import android.media.Image;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;


public class TextReaderAnalyzer implements ImageAnalysis.Analyzer{
    private static final String TAG = "TextReaderAnalyzer";

    @OptIn(markerClass = androidx.camera.core.ExperimentalGetImage.class)
    @Override
    public void analyze(@NonNull ImageProxy imageProxy) {
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

    private void processTextFromImage(Text text){
        for (Text.TextBlock block : text.getTextBlocks()){
            System.out.println("Text is: " + block.getText());
        }
    }
}
