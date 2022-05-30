package com.vaggv.livetranslation.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.vaggv.livetranslation.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText emailInput, passwordInput;
    private ProgressBar progressBar;
    private Button registerBtn, backBtn;

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();

        // Get controls from layout
        emailInput = findViewById(R.id.email2);
        passwordInput = findViewById(R.id.passwd);
        registerBtn = findViewById(R.id.btnregister);
        progressBar = findViewById(R.id.progressBar2);
        backBtn = findViewById(R.id.backtologin);

        // Set on click listener methods to register and back buttons
        registerBtn.setOnClickListener(view -> registerNewUser());
        backBtn.setOnClickListener(view -> {
            // Go back to login activity
            final Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void registerNewUser(){
        String email, password;
        email = emailInput.getText().toString();
        password = passwordInput.getText().toString();

        // If email or password inputs are empty then the app doesn't try to login
        if (TextUtils.isEmpty(email)){
            Toast.makeText(getApplicationContext(), "Please enter your email", Toast.LENGTH_LONG).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please enter your password", Toast.LENGTH_LONG).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        // Register user to firebase, if successful then go back to login activity
        // else show an error message
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(getApplicationContext(), "Registration was successful", Toast.LENGTH_LONG).show();
                final Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);

                startActivity(intent);
            } else {
                // Different catch clause for each register error
                try {
                    throw Objects.requireNonNull(task.getException());
                } catch (FirebaseAuthWeakPasswordException e) {
                    passwordInput.setError("Weak password");
                    passwordInput.requestFocus();
                } catch (FirebaseAuthInvalidCredentialsException e) {
                    emailInput.setError("Invalid email");
                    emailInput.requestFocus();
                } catch (FirebaseAuthUserCollisionException e){
                    emailInput.setError("User already exists");
                    emailInput.requestFocus();
                } catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Uknown error, register failed", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, e.getMessage());
                }
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}