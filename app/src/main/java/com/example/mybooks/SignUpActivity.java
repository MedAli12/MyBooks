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

public class SignUpActivity extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private MaterialButton signUpButton;
    private MaterialButton backToLoginButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        signUpButton = findViewById(R.id.signUpButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        // Setup sign up button
        signUpButton.setOnClickListener(v -> {
            String email = emailEditText.getText() != null ? emailEditText.getText().toString().trim() : "";
            String password = passwordEditText.getText() != null ? passwordEditText.getText().toString().trim() : "";
            String confirmPassword = confirmPasswordEditText.getText() != null ? confirmPasswordEditText.getText().toString().trim() : "";

            Log.d(TAG, "Signup attempt - Email: " + email + ", Password length: " + password.length());

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(SignUpActivity.this, "Passwords do not match",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            signUpButton.setEnabled(false);
            backToLoginButton.setEnabled(false);

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Sign up successful");
                            Toast.makeText(SignUpActivity.this, "Account created successfully",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Log.e(TAG, "Sign up failed", task.getException());
                            String errorMessage = task.getException().getMessage();
                            if (errorMessage != null && errorMessage.contains("email address is already in use")) {
                                Toast.makeText(SignUpActivity.this, "This email is already registered. Please login instead.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "Registration failed: " + errorMessage,
                                        Toast.LENGTH_SHORT).show();
                            }
                            signUpButton.setEnabled(true);
                            backToLoginButton.setEnabled(true);
                        }
                    });
        });

        // Setup back to login button
        backToLoginButton.setOnClickListener(v -> {
            finish(); // This will return to the previous activity (LoginActivity)
        });
    }
} 