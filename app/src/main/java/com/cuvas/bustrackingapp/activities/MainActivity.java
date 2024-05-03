package com.cuvas.bustrackingapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.cuvas.bustrackingapp.R;

public class MainActivity extends AppCompatActivity {

    CardView adminButton, driverButton, studentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);

        adminButton = findViewById(R.id.adminButton);
        driverButton = findViewById(R.id.driverButton);
        studentButton = findViewById(R.id.studentButton);

        adminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });

        driverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, DriverActivity.class);
                startActivity(intent);
            }
        });


        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =  new Intent(MainActivity.this, StudentActivity.class);
                startActivity(intent);
            }
        });
    }
}