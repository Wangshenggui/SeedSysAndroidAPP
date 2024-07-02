package com.example.seedingsystemandroidapp.RTKSettingFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seedingsystemandroidapp.R;

import org.json.JSONException;
import org.json.JSONObject;

public class MotorControlFragment extends Fragment {

    private TextView SpeedTextView;
    private TextView altiTextView;
    private TextView RTKStatusTextView;
    private TextView RTKHCSDSTextView;
    private TextView lonTextView;
    private TextView latTextView;
    private TextView leftInfo;
    private Button SendDataButton;
    private Button SendData0Button;
    private Handler handler;
    private WebSocketServiceReceiver webSocketReceiver;

    public MotorControlFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        webSocketReceiver = new WebSocketServiceReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motor_control, container, false);

        SendDataButton = view.findViewById(R.id.SendDataButton);
        SpeedTextView = view.findViewById(R.id.SpeedTextView);
        altiTextView = view.findViewById(R.id.altiTextView);
        RTKStatusTextView = view.findViewById(R.id.RTKStatusTextView);
        RTKHCSDSTextView = view.findViewById(R.id.RTKHCSDSTextView);
        lonTextView = view.findViewById(R.id.lonTextView);
        latTextView = view.findViewById(R.id.latTextView);
        leftInfo = view.findViewById(R.id.leftInfo);
        SendData0Button = view.findViewById(R.id.SendData0Button);

        setupSendDataButton();
        setupSendData0Button();

        startCounting(); // Placeholder, adjust as needed

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter("WebSocketMessage");
        requireContext().registerReceiver(webSocketReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(webSocketReceiver);
    }

    private void setupSendDataButton() {
        SendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWebSocketMessage(1);
            }
        });
    }

    private void setupSendData0Button() {
        SendData0Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendWebSocketMessage(0);
            }
        });
    }

    private void sendWebSocketMessage(int value) {
        JSONObject data = new JSONObject();
        String[] variables = {"n1", "n2", "n3", "n4", "z1", "z2", "z3", "z4", "s1", "s2", "s3", "s4"};

        for (String variable : variables) {
            putToJsonObject(data, variable, value);
        }

        String jsonMessage = data.toString();

        Intent intent = new Intent("SendWebSocketMessage");
        intent.putExtra("message", jsonMessage);
        requireContext().sendBroadcast(intent);
    }

    private void putToJsonObject(JSONObject jsonObject, String key, Object value) {
        try {
            jsonObject.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException("Could not put key '" + key + "' into JSONObject", e);
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

    public static String convertToDMS(double coord) {
        int degrees = (int) coord;
        double minutesDouble = (coord - degrees) * 60;
        int minutes = (int) minutesDouble;
        double secondsDouble = (minutesDouble - minutes) * 60;
        String secondsFormatted = String.format("%.4f", secondsDouble);

        return String.format("%d°%d'%s''", degrees, minutes, secondsFormatted);
    }

    private void startCounting() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Perform periodic task here if needed
                handler.postDelayed(this, 1000); // Repeat every 1 second
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Stop periodic task
    }
}
