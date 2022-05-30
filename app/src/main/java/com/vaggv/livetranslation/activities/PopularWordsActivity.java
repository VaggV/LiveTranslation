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

public class PopularWordsActivity extends AppCompatActivity {
    private TextView popularWords;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_words);

        popularWords = findViewById(R.id.popularWords);
        back = findViewById(R.id.backButtonPW);

        back.setOnClickListener(v -> finish());

        getPopularWords();
    }

    private void getPopularWords(){
        RequestQueue requestQueue = Volley.newRequestQueue(PopularWordsActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Utils.url + "/api/popularwords",
                response -> {
                    popularWords.setText("");
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            popularWords.setText(popularWords.getText() + obj.getString("value") + " - " + obj.getString("counter") + " translations\n\n\n" );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    popularWords.setText(error.toString());
                    Log.e("PopularTextActivity", "ERROR ON VOLLEY", error);
                });

        requestQueue.add(stringRequest);
    }
}