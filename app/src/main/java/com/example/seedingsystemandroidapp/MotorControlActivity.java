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

    private TextView SpeedTextView;
    private TextView altiTextView;
    private TextView RTKStatusTextView;
    private TextView RTKHCSDSTextView;
    private TextView lonTextView;
    private TextView latTextView;
    private TextView leftInfo;
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

        SendDataButton = findViewById(R.id.SendDataButton);
        SpeedTextView = (TextView) findViewById(R.id.SpeedTextView);
        altiTextView = (TextView) findViewById(R.id.altiTextView);
        RTKStatusTextView = (TextView) findViewById(R.id.RTKStatusTextView);
        RTKHCSDSTextView = (TextView) findViewById(R.id.RTKHCSDSTextView);
        lonTextView = (TextView) findViewById(R.id.lonTextView);
        latTextView = (TextView) findViewById(R.id.latTextView);
        leftInfo = (TextView)findViewById(R.id.leftInfo);

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

                    if (jsonObject.has("lon")) {
                        // 解析包含"lon"信息的消息
                        String data = jsonObject.getString("speed");
                        double speed = Double.parseDouble(data);
                        String formattedData = String.format("%.3f", speed);
                        SpeedTextView.setText("速度(km/h):" + formattedData);

                        data = jsonObject.getString("alti");
                        double alti = Double.parseDouble(data);
                        formattedData = String.format("%.2f", alti);
                        altiTextView.setText("海拔(m):" + formattedData);

                        data = jsonObject.getString("rtksta");
                        RTKStatusTextView.setText("RTK状态:" + data);

                        data = jsonObject.getString("HCSDS");
                        RTKHCSDSTextView.setText("卫星数量:" + data);

                        data = jsonObject.getString("lon");
                        double lon = Double.parseDouble(data);
                        formattedData = String.format("%.10f", lon);
                        String dms = convertToDMS(lon);
                        lonTextView.setText("E:" + dms);

                        data = jsonObject.getString("lat");
                        double lat = Double.parseDouble(data);
                        formattedData = String.format("%.10f", lat);
                        dms = convertToDMS(lat);
                        latTextView.setText("N:" + dms);
                    } else if (jsonObject.has("S1")) {
                        // 解析包含"S1"信息的消息
                        // 这里放置解析"S1"信息的代码，根据具体需求进行处理
                        String S = jsonObject.getString("S1");
                        String r1 = jsonObject.getString("r1");
                        leftInfo.setText("状态:" + S + "-转速:" + r1);
                    } else {
                        // 处理其他情况或报错
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //转换度分秒
    public static String convertToDMS(double coord) {
        int degrees = (int) coord;
        double minutesDouble = (coord - degrees) * 60;
        int minutes = (int) minutesDouble;
        double secondsDouble = (minutesDouble - minutes) * 60;
        // Format seconds to show up to 4 decimal places
        String secondsFormatted = String.format("%.4f", secondsDouble);

        // Construct the DMS string without extra spaces
        return String.format("%d°%d'%s″", degrees, minutes, secondsFormatted);
    }
}
