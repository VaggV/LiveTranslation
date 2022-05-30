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

public class UserLeaderboardActivity extends AppCompatActivity {
    private TextView usersResultTxt;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_leaderboard);

        usersResultTxt = findViewById(R.id.userLeaderboard);
        back = findViewById(R.id.backButtonUL);

        back.setOnClickListener(v -> finish());

        getUsers();
    }

    private void getUsers(){
        RequestQueue requestQueue = Volley.newRequestQueue(UserLeaderboardActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                Utils.url + "/api/userstotal",
                response -> {
                    usersResultTxt.setText("");
                    /*String[] popularTranslations = response.substring(1).replace("]", "").split(",");
                    for (String x : popularTranslations){
                        usersResultTxt.setText(usersResultTxt.getText() + x.replace("\"", "").split("\\*")[0] + "\n\n");
                    }*/
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            usersResultTxt.setText(usersResultTxt.getText() + obj.getString("value") + " - " + obj.getString("counter") + " translations\n\n\n" );
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> {
                    usersResultTxt.setText("There was an error");
                    Log.e("PopularTextActivity", "ERROR ON VOLLEY", error);
                });

        requestQueue.add(stringRequest);
    }
}