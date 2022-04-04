package com.vaggv.livetranslation;

import static com.vaggv.livetranslation.Utils.languages;
import static com.vaggv.livetranslation.Utils.toFullLangString;
import static com.vaggv.livetranslation.Utils.toShortLangString;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentificationOptions;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ResultActivity extends AppCompatActivity {
    private static final String TAG = "ResultActivity";

    private Spinner resultsDropdown, langDropdown;
    private TextView txtLang;
    private LanguageIdentifier languageIdentifier;
    private EditText textToTranslate, translatedText;
    private Button translateBtn;
    private TranslatorOptions options;
    private Translator translator;
    private DownloadConditions conditions;
    private RemoteModelManager modelManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultsDropdown = findViewById(R.id.resultsSpinner);
        txtLang = findViewById(R.id.txtLang);
        langDropdown = findViewById(R.id.langDropdown);
        textToTranslate = findViewById(R.id.textToTranslate);
        translatedText = findViewById(R.id.translatedText);
        translateBtn = findViewById(R.id.translateBtn);

        modelManager = RemoteModelManager.getInstance();

        languageIdentifier = LanguageIdentification.getClient(
                new LanguageIdentificationOptions.Builder().setConfidenceThreshold(0.34f).build());

        conditions = new DownloadConditions.Builder().requireWifi().build();

        ArrayList<String> results = (ArrayList<String>) getIntent().getSerializableExtra("results");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, results);

        resultsDropdown.setAdapter(adapter);
        resultsDropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                textToTranslate.setText(resultsDropdown.getSelectedItem().toString());
                languageIdentifier.identifyLanguage(resultsDropdown.getSelectedItem().toString())
                        .addOnSuccessListener(s -> {
                            if(s.equals("und")) {
                                txtLang.setText("Can't identify language.");
                            } else {
                                txtLang.setText(Utils.toFullLangString(s));
                            }
                        }).addOnFailureListener(e -> {
                            Log.e(TAG, "Error while trying to identify language" ,e);
                        });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // Empty
            }
        });

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, languages);
        langDropdown.setAdapter(adapter1);

        translateBtn.setOnClickListener(v -> {
            translate(textToTranslate.getText().toString(),
                    toShortLangString(txtLang.getText().toString()),
                    toShortLangString(langDropdown.getSelectedItem().toString()));
        });


    }

    private void translate(String text, String sourceLang, String targetLang){
        options = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLang)
                .setTargetLanguage(targetLang)
                .build();

        translator = Translation.getClient(options);

        TranslateRemoteModel model1 = new TranslateRemoteModel.Builder(targetLang).build();
        TranslateRemoteModel model2 = new TranslateRemoteModel.Builder(sourceLang).build();
        modelManager.isModelDownloaded(model1).addOnSuccessListener(isDownloaded -> {
            if (!isDownloaded) {
                Toast.makeText(ResultActivity.this, "Downloading model " + targetLang, Toast.LENGTH_SHORT).show();
            }
        });
        modelManager.isModelDownloaded(model2).addOnSuccessListener(isDownloaded -> {
            if (!isDownloaded) {
                Toast.makeText(ResultActivity.this, "Downloading model " + sourceLang, Toast.LENGTH_SHORT).show();
            }
        });

        translator.downloadModelIfNeeded(conditions).addOnSuccessListener(unused -> {
            translator.translate(text).addOnSuccessListener(s -> {
                translatedText.setText(s);
                try {
                    System.out.println("SAVING EVENT");
                    saveEvent(text, toFullLangString(sourceLang), s, toFullLangString(targetLang));
                } catch (Exception e) {
                    System.out.println("COULDNT SAVE EVENT");
                    System.out.println("EVENT: " + e);
                    e.printStackTrace();
                }
            }).addOnFailureListener(e -> {
                Toast.makeText(ResultActivity.this, "Failed to translate.", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Toast.makeText(ResultActivity.this, "Couldn't download translation model.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Error while trying to download translation model.", e);
        });


    }

    private void saveEvent(String originaltext, String textlang, String translatedtext, String translatedtextlang){
        try {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "http://192.168.1.18:8080/api/translations";
            JSONObject jsonBody = new JSONObject();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            System.out.println("TIMESTAMP IS: " + timestamp);
            jsonBody.put("timestamp", timestamp);
            jsonBody.put("location", "Greece");
            jsonBody.put("userid", "v@v.gr");
            jsonBody.put("originaltext", originaltext);
            jsonBody.put("textlang", textlang);
            jsonBody.put("translatedtext", translatedtext);
            jsonBody.put("translatedtextlang", translatedtextlang);
            final String mRequestBody = jsonBody.toString();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> Toast.makeText(ResultActivity.this, "Success", Toast.LENGTH_LONG).show(),
                    error -> Toast.makeText(ResultActivity.this, "Error", Toast.LENGTH_LONG).show()) {
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