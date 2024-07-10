package com.example.seedingsystemandroidapp.RTKSettingFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.seedingsystemandroidapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MotorControlFragment extends Fragment {

    private int SetSpeed;
    private TextView SpeedTextView;
    private TextView altiTextView;
    private TextView RTKStatusTextView;
    private TextView RTKHCSDSTextView;
    private TextView lonTextView;
    private TextView latTextView;
    private TextView leftInfo;
    private Button SendDataButton;
    private Button SendData0Button;
    private TextView SetSpeedText;
    private SeekBar SetSpeedSeekBar;
    private View leftColorBlock;
    private View rightColorBlock;
    private View leftBottomColorBlock;
    private View rightBottomColorBlock;
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
        SetSpeedText = view.findViewById(R.id.SetSpeedText);
        SetSpeedSeekBar = view.findViewById(R.id.SetSpeedSeekBar);
        leftColorBlock = view.findViewById(R.id.leftColorBlock);
        rightColorBlock = view.findViewById(R.id.rightColorBlock);
        leftBottomColorBlock = view.findViewById(R.id.leftBottomColorBlock);
        rightBottomColorBlock = view.findViewById(R.id.rightBottomColorBlock);


        leftColorBlock.setOnClickListener(new View.OnClickListener() {
            boolean isOn = false;
            @Override
            public void onClick(View v) {
                isOn = !isOn;
                if (isOn) {
                    leftColorBlock.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    // 执行开启时的逻辑
                } else {
                    leftColorBlock.setBackgroundColor(Color.parseColor("#80000000"));
                    // 执行关闭时的逻辑
                }
            }
        });
        rightColorBlock.setOnClickListener(new View.OnClickListener() {
            boolean isOn = false;
            @Override
            public void onClick(View v) {
                isOn = !isOn;
                if (isOn) {
                    rightColorBlock.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    // 执行开启时的逻辑
                } else {
                    rightColorBlock.setBackgroundColor(Color.parseColor("#80000000"));
                    // 执行关闭时的逻辑
                }
            }
        });
        leftBottomColorBlock.setOnClickListener(new View.OnClickListener() {
            boolean isOn = false;
            @Override
            public void onClick(View v) {
                isOn = !isOn;
                if (isOn) {
                    leftBottomColorBlock.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    // 执行开启时的逻辑
                } else {
                    leftBottomColorBlock.setBackgroundColor(Color.parseColor("#80000000"));
                    // 执行关闭时的逻辑
                }
            }
        });
        rightBottomColorBlock.setOnClickListener(new View.OnClickListener() {
            boolean isOn = false;
            @Override
            public void onClick(View v) {
                isOn = !isOn;
                if (isOn) {
                    rightBottomColorBlock.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
                    // 执行开启时的逻辑
                } else {
                    rightBottomColorBlock.setBackgroundColor(Color.parseColor("#80000000"));
                    // 执行关闭时的逻辑
                }
            }
        });



        setupSendDataButton();
        setupSendData0Button();

        SetSpeedSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override  //当滑块进度改变时，会执行该方法下的代码
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//            mImageView.setAlpha(i);//设置当前的透明度
//            mTextView.setText("当前透明度： " +i+"/255");
                SetSpeedText.setText("速度 " +i);
                SetSpeed = i;
            }

            @Override  //当开始滑动滑块时，会执行该方法下的代码
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override   //当结束滑动滑块时，会执行该方法下的代码
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
                JSONObject data = new JSONObject();
                String[] variables = {"n1", "n2", "n3", "n4", "z1", "z2", "z3", "z4", "s1", "s2", "s3", "s4"};

                try {
                    // 给 n1 赋值
                    data.put(variables[0], 1); // 替换 "your_value_for_n1" 为你实际的值
                    data.put(variables[1], 1);
                    data.put(variables[2], 1);
                    data.put(variables[3], 1);
                    data.put(variables[4], 1);
                    data.put(variables[5], 1);
                    data.put(variables[6], 1);
                    data.put(variables[7], 1);

                    data.put(variables[8], SetSpeed);
                    data.put(variables[9], SetSpeed);
                    data.put(variables[10], SetSpeed);
                    data.put(variables[11], SetSpeed);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String jsonMessage = data.toString();

                Intent intent = new Intent("SendWebSocketMessage");
                intent.putExtra("message", jsonMessage);
                requireContext().sendBroadcast(intent);
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

    private String getRTKStatusMessage(String data) {
        switch (data) {
            case "0":
                return "无定位";
            case "1":
                return "单点定位";
            case "2":
                return "差分定位D";
            case "3":
                return "精密定位";
            case "4":
                return "RTK固定解";
            case "5":
                return "RTK浮动解";
            case "6":
                return "位置估算";
            case "7":
                return "手动输入模式";
            case "8":
                return "模拟模式";
            default:
                return "未知状态";
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
                        String rtkStatusMessage = getRTKStatusMessage(data);
                        RTKStatusTextView.setText("RTK状态: " + rtkStatusMessage);


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
                        leftInfo.setText("\n状态:" + S + "-转速:" + r1);
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
