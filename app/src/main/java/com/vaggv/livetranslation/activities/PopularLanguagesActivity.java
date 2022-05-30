package com.vaggv.livetranslation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.vaggv.livetranslation.R;
import com.vaggv.livetranslation.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PopularLanguagesActivity extends AppCompatActivity {
    private TextView popularLanguages;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_languages);

        popularLanguages = findViewById(R.id.popularLanguages);
        back = findViewById(R.id.backButtonPL);

        back.setOnClickListener(v -> finish());

        getPopularLanguages();
    }

    private void getPopularLanguages(){
        RequestQueue requestQueue = Volley.newRequestQueue(PopularLanguagesActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Utils.url + "/api/popularlanguages?translatedLanguage=true",
                response -> {
                    popularLanguages.setText("");
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            popularLanguages.setText(popularLanguages.getText() + obj.getString("value") + " - " + obj.getString("counter") + " translations\n\n\n" );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    popularLanguages.setText(error.toString());
                    Log.e("PopularTextActivity", "ERROR ON VOLLEY", error);
                });

        requestQueue.add(stringRequest);
    }
}