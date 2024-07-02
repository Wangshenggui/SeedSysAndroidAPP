package com.example.seedingsystemandroidapp.RTKSettingFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.seedingsystemandroidapp.R;

public class GameFragment extends Fragment {

    private TextView textView;
    private int count = 0;
    private Handler handler;

    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        textView = view.findViewById(R.id.text_view); // 假设布局文件中有一个TextView，ID为text_view
        startCounting();
        return view;
    }

    private void startCounting() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                count++;
                if (textView != null) {
                    textView.setText(String.valueOf(count));
                }
                handler.postDelayed(this, 100);
            }
        }, 100);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // 停止计数
    }

    public void updateText(String text) {
        if (textView != null) {
            textView.setText(text);
        }
    }
}
