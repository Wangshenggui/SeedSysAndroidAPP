package com.example.seedingsystemandroidapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class DiffAccountActivity extends AppCompatActivity {

    private Spinner spinner;
    RadioGroup radioGroup1;
    RadioGroup radioGroup2;
    CheckBox showPasswordCheckBox;
    EditText PasswordeditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diff_account);

        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        PasswordeditText = findViewById(R.id.PasswordeditText);
        spinner = findViewById(R.id.spinner);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.RadioYiDongeNodeBButton) {
                    // Handle 移动 (Yi Dong) selection
                } else if (checkedId == R.id.RadioQianXuneNodeBButton) {
                    // Handle 千寻 (Qian Xun) selection
                }
            }
        });
        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.Radio8001eNodeBButton) {
                    // Handle 移动 (Yi Dong) selection
                } else if (checkedId == R.id.Radio8002eNodeBButton) {
                    // Handle 千寻 (Qian Xun) selection
                }
            }
        });
        //显示密码
        showPasswordCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                PasswordeditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                PasswordeditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            PasswordeditText.setSelection(PasswordeditText.length()); // 保持光标在文本末尾
        });

        // 创建一个选项列表（数据源）
        List<String> categories = new ArrayList<>();
        categories.add("选项 1");
        categories.add("选项 2");
        categories.add("选项 3");
        // 创建一个适配器（Adapter），用于将数据与Spinner关联起来
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // 获取默认视图
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                // 设置字体大小
                textView.setTextSize(20); // 根据需要调整字体大小
                return view;
            }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                // 获取默认下拉视图
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                // 设置字体大小
                textView.setTextSize(18); // 根据需要调整字体大小
                return view;
            }
        };
        // 设置下拉列表框的样式
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 将适配器设置到Spinner
        spinner.setAdapter(dataAdapter);
        // 设置Spinner的选择监听器
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 当用户选择某个选项时触发
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(DiffAccountActivity.this, "选择了：" + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选项被选择时触发
            }
        });

        Intent intent = new Intent("SendWebSocketMessage");
        intent.putExtra("message", "这是消息");
        sendBroadcast(intent);
    }
}
