package com.example.mybooks;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private MaterialButton signUpButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_login);

            // Initialize Firebase Auth
            auth = FirebaseAuth.getInstance();
            Log.d(TAG, "Firebase Auth initialized");

            // Check if user is already logged in
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null) {
                Log.d(TAG, "User already logged in, redirecting to MainActivity");
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                return;
            }

            // Initialize views
            emailEditText = findViewById(R.id.emailEditText);
            passwordEditText = findViewById(R.id.passwordEditText);
            loginButton = findViewById(R.id.loginButton);
            signUpButton = findViewById(R.id.signUpButton);

            // Setup login button
            loginButton.setOnClickListener(v -> {
                String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
                String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";

                Log.d(TAG, "Login attempt - Email: " + email + ", Password length: " + password.length());

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                loginButton.setEnabled(false);
                signUpButton.setEnabled(false);

                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Login successful");
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Log.e(TAG, "Login failed", task.getException());
                                Toast.makeText(LoginActivity.this, "Authentication failed: " + 
                                        task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                                loginButton.setEnabled(true);
                                signUpButton.setEnabled(true);
                            }
                        });
            });

            // Setup sign up button to navigate to SignUpActivity
            signUpButton.setOnClickListener(v -> {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            });

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error initializing app: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
} 