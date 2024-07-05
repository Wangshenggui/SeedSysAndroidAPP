package com.example.seedingsystemandroidapp;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化扫码组件
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false); // 设置扫码界面随设备方向旋转
        integrator.setPrompt("将二维码放入框内，即可自动扫描"); // 设置扫码时的提示信息
        integrator.initiateScan(); // 启动扫码
    }

    private boolean isWeChatInstalled() {
        try {
            PackageManager pm = getPackageManager();
            PackageInfo info = pm.getPackageInfo("com.tencent.mm", PackageManager.GET_ACTIVITIES);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    // 处理扫码结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                // 扫码取消
                Toast.makeText(this, "Scan canceled", Toast.LENGTH_LONG).show();
            } else {
                // 处理扫码结果
                String scanResult = result.getContents();

                // 判断是否为URL
                if (scanResult.startsWith("http://") || scanResult.startsWith("https://")) {
                    if (isWeChatInstalled()) {
                        // 构建微信分享URL Intent
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("weixin://dl/business/?ticket=" + scanResult));
                        startActivity(intent);
                    } else {
//                        String url="weixin://";
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

                        // 调用方法打开微信扫码
                        toWeChatScanDirect(ScanQRCodeActivity.this);
                    }
                } else {
                    // 其他处理逻辑
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @SuppressLint("WrongConstant")
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

}
