package com.example.seedingsystemandroidapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    //跳转到电机控制页面
    Button goMotConActivityButton;
    Button goRTKSettingActivityButton;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 启动WebSocket服务
        Intent serviceIntent = new Intent(this, WebSocketService.class);
        startService(serviceIntent);

        //获取控件ID
        goMotConActivityButton = (Button) findViewById(R.id.goMotConActivityButton);
        goRTKSettingActivityButton = (Button) findViewById(R.id.goRTKSettingActivityButton);
        goMotConActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MotorControlActivity.class);
                startActivity(intent);
            }
        });

        goRTKSettingActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RTKSettingActivity.class);
                startActivity(intent);
            }
        });
    }
}