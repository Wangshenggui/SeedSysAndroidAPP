package com.example.seedingsystemandroidapp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

public class MotorControlActivity extends AppCompatActivity {

    private TextView RxTextView;
    private TextView SpeedTextView;
    private Button SendDataButton;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private WebSocketServiceReceiver receiver;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motor_control);

        // Set up the Toolbar
        Toolbar toolbar = findViewById(R.id.Toolbar);
        setSupportActionBar(toolbar);

        toolbar.setTitle("电机控制");
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleText);

        RxTextView = findViewById(R.id.RxTextView);
        SendDataButton = findViewById(R.id.SendDataButton);
        SpeedTextView = (TextView) findViewById(R.id.SpeedTextView);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> finish());

        // 设置发送按钮的点击事件监听器
        SendDataButton.setOnClickListener(new View.OnClickListener() {
            private int counter = 0;

            @Override
            public void onClick(View v) {
                counter++;
                String json = "{\"giuty\":" + counter + "}";
                Intent intent = new Intent("SendWebSocketMessage");
                intent.putExtra("message", json);
                sendBroadcast(intent);
            }
        });

        // 注册广播接收器
        receiver = new WebSocketServiceReceiver();
        IntentFilter filter = new IntentFilter("WebSocketMessage");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    private class WebSocketServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("WebSocketMessage".equals(intent.getAction())) {
                String message = intent.getStringExtra("message");

                try {
                    JSONObject jsonObject = new JSONObject(message);
                    // 假设 JSON 有一个字段叫 "data"
                    String data = jsonObject.getString("speed");
                    double speed = Double.parseDouble(data); // 将字符串转换为浮点数
                    String formattedData = String.format("%.3f", speed); // 格式化为三位小数
                    SpeedTextView.setText("速度(km/h):" + formattedData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
