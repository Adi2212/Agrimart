package com.example.agrimart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class SplashAcitvity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);


        SharedPreferences sharedPreferences=getSharedPreferences("UserPrefs",MODE_PRIVATE);
        boolean IsSignedIn=sharedPreferences.getBoolean("IsSignedIn",false);


        new Handler().postDelayed(()->{
            if (IsSignedIn) {
                // Navigate to MainActivity
                Intent intent = new Intent(SplashAcitvity.this, MainActivity.class);
                startActivity(intent);
            } else {
                // Navigate to SignInActivity
                Intent intent = new Intent(SplashAcitvity.this, HomeActivity.class);
                startActivity(intent);
            }
            finish();
        },1000);


    }
}