package com.vaggv.livetranslation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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

        getRequest();
    }

    private void getRequest(){
        RequestQueue requestQueue = Volley.newRequestQueue(PopularTextActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://localhost:8080/api/populartranslations",
                response -> responseText.setText(response),
                error -> {
            responseText.setText(error.toString());
        });
    }
}