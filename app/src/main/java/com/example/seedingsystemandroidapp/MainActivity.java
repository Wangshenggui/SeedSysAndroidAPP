package com.example.seedingsystemandroidapp;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    Button goRTKSettingActivityButton;
    Button goScanActivityButton;
    Button goCanvasActivityButton;
    Button goBluetoothButton;

    public static OutputStream outputStream=null;//获取输出数据
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
        goRTKSettingActivityButton = (Button) findViewById(R.id.goRTKSettingActivityButton);
        goScanActivityButton = (Button) findViewById(R.id.goScanActivityButton);
        goCanvasActivityButton = (Button) findViewById(R.id.goCanvasActivityButton);
        goBluetoothButton=(Button) findViewById(R.id.goBluetoothButton);




        goBluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,BluetoothFunActivity.class);
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
        goScanActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toWeChatScanDirect(MainActivity.this);
            }
        });
        goCanvasActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CanvasActivity.class);
                startActivity(intent);
            }
        });

    }
    public static void toWeChatScanDirect(Context context) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI"));
            intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
            intent.setFlags(335544320);
            intent.setAction("android.intent.action.VIEW");
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //软件退出后清空，断开蓝牙操作
        BluetoothFunActivity.connectThread.cancel();
        BluetoothFunActivity.connectedThread.cancel();
    }
}