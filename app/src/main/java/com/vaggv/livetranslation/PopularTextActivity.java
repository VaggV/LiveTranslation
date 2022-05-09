package com.vaggv.livetranslation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class PopularTextActivity extends AppCompatActivity {
    private TextView responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular_text);

        responseText = findViewById(R.id.responseText);

        getPopularTranslations();
        // TODO: design
    }

    private void getPopularTranslations(){
        RequestQueue requestQueue = Volley.newRequestQueue(PopularTextActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://192.168.1.18:8080/api/populartranslations", // 192.168.1.8 for mac
                response -> {
            responseText.setText("");
            String[] popularTranslations = response.substring(1).replace("]", "").split(",");
            for (String x : popularTranslations){
                responseText.setText(responseText.getText() + x.replace("\"", "").split("\\*")[0] + "\n\n");
            }
                },
                error -> {
            responseText.setText(error.toString());
            Log.e("PopularTextActivity", "ERROR ON VOLLEY", error);
        });

        requestQueue.add(stringRequest);


    }
}