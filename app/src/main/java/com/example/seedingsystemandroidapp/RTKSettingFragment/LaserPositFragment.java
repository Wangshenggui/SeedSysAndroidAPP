package com.example.seedingsystemandroidapp.RTKSettingFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seedingsystemandroidapp.R;

import org.json.JSONException;
import org.json.JSONObject;


public class LaserPositFragment extends Fragment {

    private Handler handler;
    private LaserPositFragment.WebSocketServiceReceiver webSocketReceiver;

    public LaserPositFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
        webSocketReceiver = new LaserPositFragment.WebSocketServiceReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_laser_posit, container, false);
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

                    } else if (jsonObject.has("S1")) {
                        // 解析包含"S1"信息的消息
                        // 这里放置解析"S1"信息的代码，根据具体需求进行处理

                    } else {
                        // 处理其他情况或报错
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}