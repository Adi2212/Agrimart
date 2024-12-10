package com.example.agrimart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText emailEditText,passwordEditText;

private AppCompatButton signInBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_in);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(this);

        // Initialize Views
        emailEditText = findViewById(R.id.signInEmail);
        passwordEditText = findViewById(R.id.signInPassword);

        findViewById(R.id.signInBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("IsSignedIn", true);
                        editor.apply();
                        Toast.makeText(SignInActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                       Intent intent = new Intent(SignInActivity.this, MainActivity.class);

                        startActivity(intent);

                        finish();
                    } else {
                        Toast.makeText(SignInActivity.this, "Login Failed: Invalid Credentials. ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void signup(View view){
        startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
        finish();
    }
}