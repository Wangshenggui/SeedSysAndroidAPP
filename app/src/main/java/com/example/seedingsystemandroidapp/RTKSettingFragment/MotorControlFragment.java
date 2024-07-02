package com.example.seedingsystemandroidapp.RTKSettingFragment;

import android.content.Intent;
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
    private Button SendDataButton;
    private Button SendData0Button;
    private Handler handler;

    public MotorControlFragment() {
        // 必需的空公共构造函数
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_motor_control, container, false);

        SendDataButton = view.findViewById(R.id.SendDataButton);
        SendData0Button = view.findViewById(R.id.SendData0Button);
        SpeedTextView = view.findViewById(R.id.SpeedTextView);

        setupSendDataButton();
        setupSendData0Button();

        startCounting(); // 示例占位符，应根据实际使用调整

        return view;
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
            throw new RuntimeException("无法将键 '" + key + "' 放入 JSONObject", e);
        }
    }

    // 占位符方法，用于周期性任务
    private void startCounting() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 如果需要，执行周期性任务在这里
                handler.postDelayed(this, 1000); // 每1秒重复一次
            }
        }, 1000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // 停止周期性任务
    }
}
