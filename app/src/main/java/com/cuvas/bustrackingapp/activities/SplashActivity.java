package com.cuvas.bustrackingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.cuvas.bustrackingapp.R;
import com.cuvas.bustrackingapp.SharedPref;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_splash);

        SharedPref sharedPref = new SharedPref(SplashActivity.this);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                String type = sharedPref.getString("type");
                if (type.equals("Admin")) {
                    intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
                } else if (type.equals("Driver")) {
                    intent = new Intent(SplashActivity.this, DriverMapActivity.class);
                }else if (type.equals("Student")){
                    intent = new Intent(SplashActivity.this, StudentDashboardActivity.class);
                }else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }

                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}