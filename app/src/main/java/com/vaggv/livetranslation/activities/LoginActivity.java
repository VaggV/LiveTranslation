package com.vaggv.livetranslation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.vaggv.livetranslation.R;
import com.vaggv.livetranslation.Utils;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText emailInput, passwordInput;
    private Button loginBtn, registerBtn, instantLogin;
    private ProgressBar progressBar;

    private final static String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();

        // Get controls from layout
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);
        registerBtn = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        instantLogin = findViewById(R.id.instantLogin);
        instantLogin.setOnClickListener(view -> login("v@v.gr", "123123"));

        // Set on click listener methods to buttons
        loginBtn.setOnClickListener(view -> {
            login(emailInput.getText().toString(), passwordInput.getText().toString());
        });

        registerBtn.setOnClickListener(view -> {
            // Open register activity on click
            /*final Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);*/
            Toast.makeText(LoginActivity.this, "TODO", Toast.LENGTH_SHORT).show();
        });

    }

    private void login(String email, String password) {

        // If email or password inputs are empty then the app doesn't try to login
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // Try to login, if login successful then open main activity
        // if unsuccessful then show login failed message
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getApplicationContext(), getString(R.string.login_successful), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
            } else {
                // Different catch clauses for each error case
                // (e.g. No user with that email or password is wrong)
                try {
                    throw task.getException();
                } catch (FirebaseAuthInvalidUserException e) {
                    emailInput.setError(getString(R.string.user_not_found));
                    emailInput.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    passwordInput.setError(getString(R.string.invalid_password));
                    passwordInput.requestFocus();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                }
            }

            progressBar.setVisibility(View.GONE);
        });
    }
}