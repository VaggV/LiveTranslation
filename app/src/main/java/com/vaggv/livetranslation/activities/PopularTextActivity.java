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

public class PopularTextActivity extends AppCompatActivity {
    private TextView popularText;
    private Button back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_text);

        popularText = findViewById(R.id.popularText);
        back = findViewById(R.id.backButtonPT);

        back.setOnClickListener(v -> finish());

        getPopularTranslations();

    }

    private void getPopularTranslations(){
        RequestQueue requestQueue = Volley.newRequestQueue(PopularTextActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Utils.url + "/api/populartranslations",
                response -> {
                    popularText.setText("");
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            popularText.setText(popularText.getText() + obj.getString("value") + " - " + obj.getString("counter") + " times\n\n\n" );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    popularText.setText(error.toString());
            Log.e("PopularTextActivity", "ERROR ON VOLLEY", error);
        });

        requestQueue.add(stringRequest);
    }
}