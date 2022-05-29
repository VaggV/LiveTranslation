package com.vaggv.livetranslation;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.nio.charset.StandardCharsets;

public class ApiHandler {
    public static void postRequest(Context context, String url, JSONObject jsonBody){
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        final String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(context, "Successfully saved event", Toast.LENGTH_LONG).show(),
                error -> Log.e("ApiHandler", "Error while saving event", error)) {
            //Toast.makeText(context, "Error while saving event", Toast.LENGTH_LONG).show()
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() {
                return requestBody.getBytes(StandardCharsets.UTF_8);
            }
        };

        requestQueue.add(stringRequest);
    }
    // "/translations" GET
    // "/usersstotal" (total translations per user)
    // "/popularwords"
    // "/popularlanguages"
}
