package com.example.agrimart;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

public class HomeActivity extends AppCompatActivity {

    private AppCompatButton SignInBtn,SignUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        SignInBtn = findViewById(R.id.signinBtn);
        SignUpBtn = findViewById(R.id.signupBtn);

        SignUpBtn.setOnClickListener((view)->{
            startActivity(new Intent(HomeActivity.this,SignUpActivity.class));
            finish();
        });

        SignInBtn.setOnClickListener((view)->{
            startActivity(new Intent(HomeActivity.this,SignInActivity.class));
            finish();
        });



    }
}