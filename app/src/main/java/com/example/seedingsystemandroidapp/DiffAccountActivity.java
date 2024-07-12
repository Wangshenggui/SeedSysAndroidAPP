package com.example.seedingsystemandroidapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class DiffAccountActivity extends AppCompatActivity {

    //基站号
    int eNodeB_n = 0;
    int Port = 0;
    String MountPoint = null;
    int MountPoint_n=0;
    private Spinner spinner;
    RadioGroup radioGroup1;
    RadioGroup radioGroup2;
    CheckBox showPasswordCheckBox;
    EditText AccounteditText;
    EditText PasswordeditText;
    Button SendDiffAccountButton;

    // 定义一个静态的Toast对象
    private static Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diff_account);

        radioGroup1 = findViewById(R.id.radioGroup1);
        radioGroup2 = findViewById(R.id.radioGroup2);
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox);
        PasswordeditText = findViewById(R.id.PasswordeditText);
        spinner = findViewById(R.id.spinner);
        SendDiffAccountButton = findViewById(R.id.SendDiffAccountButton);
        AccounteditText = findViewById(R.id.AccounteditText);

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.RadioYiDongeNodeBButton) {
                    // Handle 移动 (Yi Dong) selection
                    eNodeB_n = 1;
                } else if (checkedId == R.id.RadioQianXuneNodeBButton) {
                    // Handle 千寻 (Qian Xun) selection
                    eNodeB_n = 2;
                }
                // 根据 Port 的值更新 Spinner 的选项内容
                updateSpinnerOptions();
            }
        });

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton selected
                if (checkedId == R.id.Radio8001eNodeBButton) {
                    Port = 3;
                } else if (checkedId == R.id.Radio8002eNodeBButton) {
                    Port = 4;
                }
            }
        });

        // 显示密码
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
//        categories.add("选项 1");
//        categories.add("选项 2");
//        categories.add("选项 3");

        // 创建一个适配器（Adapter），用于将数据与 Spinner 关联起来
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
        // 将适配器设置到 Spinner
        spinner.setAdapter(dataAdapter);
        // 设置 Spinner 的选择监听器
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // 当用户选择某个选项时触发
                String item = parent.getItemAtPosition(position).toString();
                MountPoint = item;
                //Toast.makeText(DiffAccountActivity.this, "选择了：" + item, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 当没有选项被选择时触发
            }
        });

        // 设置发送按钮点击事件
        SendDiffAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eNodeB_n==0){
                    showToast(DiffAccountActivity.this,"请选择基站");
                    return;
                }
                if(Port==0){
                    showToast(DiffAccountActivity.this,"请选择端口号");
                    return;
                }
                if(AccounteditText.getText().toString().trim().isEmpty()){
                    showToast(DiffAccountActivity.this,"请输入账号");
                    return;
                }
                if(PasswordeditText.getText().toString().trim().isEmpty()){
                    showToast(DiffAccountActivity.this,"请输入密码");
                    return;
                }

                switch(MountPoint){
                    case("RTCM33_GRCEpro"):
                        MountPoint_n=9;
                        break;
                    case("RTCM33_GRCEJ"):
                        MountPoint_n=5;
                        break;
                    case("RTCM33_GRCE"):
                        MountPoint_n=6;
                        break;
                    case("RTCM33_GRC"):
                        MountPoint_n=7;
                        break;
                    case("RTCM30_GR"):
                        MountPoint_n=8;
                        break;
                    case("RTCM32_GGB"):
                        MountPoint_n=10;
                        break;
                    case("RTCM30_GG"):
                        MountPoint_n=11;
                        break;
                    default:
                        MountPoint_n=0;
                        break;
                }


                String result = "{\"lte\":\"$CORS," + eNodeB_n + "," + Port + "," + MountPoint_n + "," + AccounteditText.getText().toString() + "," + PasswordeditText.getText().toString() + "\"}";

                //{"lte":"$CORS,a,b,c,string1,string2"}
                showToast(DiffAccountActivity.this,result);

                // 发送广播或执行其他操作
                Intent intent = new Intent("SendWebSocketMessage");
                intent.putExtra("message", result);
                sendBroadcast(intent);
            }
        });
    }

    // 更新 Spinner 的选项内容方法
    private void updateSpinnerOptions() {
        // 根据 Port 的值选择合适的数据源
        List<String> currentCategories = new ArrayList<>();
        //移动
        if (eNodeB_n == 1) {
            currentCategories.add("RTCM33_GRCEpro");//9
            currentCategories.add("RTCM33_GRCEJ");//5
            currentCategories.add("RTCM33_GRCE");//6
            currentCategories.add("RTCM33_GRC");//7
            currentCategories.add("RTCM30_GR");//8
            MountPoint = "RTCM33_GRCEpro";
        }
        //千寻
        else {
            currentCategories.add("RTCM32_GGB");//10
            currentCategories.add("RTCM30_GG");//11
            MountPoint = "RTCM32_GGB";
        }

        // 更新适配器的数据源，并通知适配器数据已改变
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        adapter.clear();
        adapter.addAll(currentCategories);
        adapter.notifyDataSetChanged();

        // 选择数据源之后默认选中第一个
        spinner.setSelection(0);
    }

    // 在需要显示Toast消息的地方调用这个方法
    public void showToast(Context context, String message) {
        // 如果toast不为null，则取消当前Toast
        if (toast != null) {
            toast.cancel();
        }

        // 创建新的Toast实例
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}
