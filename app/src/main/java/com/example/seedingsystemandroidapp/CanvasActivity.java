package com.example.seedingsystemandroidapp;

import static com.example.seedingsystemandroidapp.BluetoothFunFragment.DataAcceptanceFragment.bytesToDouble;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

public class CanvasActivity extends AppCompatActivity {

    private ImageView iv1;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    private Button zoomInButton;
    private Button zoomOutButton;
    private TextView testTextView;
    private TextView mapTextView;
    private Button RecordDataButton;
    private Button SaveDataButton;

    private float zoom = 1;
    private int translationX = 0;
    private int translationY = 0;
    private double minXValue;
    private double minYValue;

    private double[][] rawDataPoints;
    private double[][] plottedDataPoints;
    private double[][] SaveNewDataPoints;
    int aaa=0;

    private static byte[] RxData;
    private static int totalBytesReceived = 0;
    static int count=0;
    private static double Longitude;
    private static double Latitude;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        // 初始化视图
        initializeViews();


        String FileName = "niganma.csv";
        // 写入成功后立即读取文件内容
        readCSVFile(FileName);


        // 初始化 Bitmap 和 Canvas
        initializeBitmap();
        // 初始化画笔
        initializePaint();

        // 寻找最小值，用于缩放和平移
        minXValue = findMinValue(rawDataPoints, 0);
        minYValue = findMinValue(rawDataPoints, 1);

        // 绘制画布
        drawCanvas();

        // 设置缩放按钮点击事件
        setupZoomButtons();
        // 设置滚动视图的触摸事件
        setupScrollView();
        setupSaveButtons();


        String directoryPath = getFilesDir().getAbsolutePath();; // 要查找的目录路径
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("指定路径不是一个目录。");
            return;
        }

        listFiles(directory);
    }
    private void writeFileLonLat(String filename, double lonlat[][]) {
        StringBuilder Data = new StringBuilder();

        for (int i = 0; i < lonlat.length; i++) {
            Data.append(lonlat[i][0]).append(",").append(lonlat[i][1]).append("\n");
        }

        FileOutputStream fos = null;
        try {
            fos = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(Data.toString().getBytes());
            //Toast.makeText(this, "CSV file saved successfully", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private void listFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果要打印子目录，可以使用 file.getName() 或 file.getPath()
                    mapTextView.setText(mapTextView.getText() + file.getPath() + "\n");
                    listFiles(file); // 递归调用，处理子目录
                } else {
                    // 如果要打印文件名，可以使用 file.getName() 或 file.getPath()
                    mapTextView.setText(mapTextView.getText() + file.getPath() + "\n");
                }
            }
        }
    }
    private void readCSVFile(String fileName) {
        FileInputStream fis = null;
        try {
            fis = openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;

            int num=0;
            double[][] temp = new double[100][2];
            // Read each line from the file
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
                // Display the current line using Toast
                // 使用逗号分割字符串
                String[] parts = line.split(",");
                double getLongitude = 0;
                double getLatitude = 0;
                try {
                    getLongitude = Double.parseDouble(parts[0]);
                    getLatitude = Double.parseDouble(parts[1]);
                    // 继续处理 a 的逻辑
                } catch (NumberFormatException e) {
                    // 处理解析失败的情况
                    e.printStackTrace(); // 或者记录日志
                }

                temp[num][0] = getLongitude;
                temp[num][1] = getLatitude;
                num++;
            }

            if(num==0){
                num = 1;
            }
            rawDataPoints = new double[num][2]; // 创建一个3行4列的二维数组
            for (int i = 0; i < num; i++) {
                rawDataPoints[i][0] = temp[i][0];
                rawDataPoints[i][1] = temp[i][1];
            }

            // Show the contents in a Toast message
            String fileContents = sb.toString();
            Toast.makeText(getApplicationContext(), fileContents, Toast.LENGTH_LONG).show();
            testTextView.setText(fileContents);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 初始化视图组件
    private void initializeViews() {
        iv1 = findViewById(R.id.iv1);
        zoomInButton = findViewById(R.id.ZoomInButton);
        zoomOutButton = findViewById(R.id.ZoomOutButton);
        testTextView = findViewById(R.id.testTextView);
        mapTextView = findViewById(R.id.mapTextView);
        RecordDataButton = findViewById(R.id.RecordDataButton);
        SaveDataButton = findViewById(R.id.SaveDataButton);
    }

    // 初始化 Bitmap 和 Canvas
    private void initializeBitmap() {
        int screenWidth = getScreenWidth();
        bitmap = Bitmap.createBitmap(screenWidth, screenWidth, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        canvas.drawColor(Color.BLACK); // 绘制背景为黑色
    }

    // 初始化画笔
    private void initializePaint() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setAlpha(128);
    }

    // 绘制画布
    private void drawCanvas() {
        plottedDataPoints = new double[rawDataPoints.length][2];
        zoomPoints(rawDataPoints, zoom);
        drawFilledMap(plottedDataPoints, Color.RED, 5, 255);
        iv1.setImageBitmap(bitmap); // 将绘制好的 Bitmap 显示在 ImageView 中
    }

    // 设置缩放按钮的点击事件
    private void setupZoomButtons() {
        zoomInButton.setOnClickListener(v -> {
            zoom *= 1.5f; // 放大倍数
            canvas.drawColor(Color.BLACK); // 清空画布，重新绘制
            zoomPoints(rawDataPoints, zoom);
            drawFilledMap(plottedDataPoints, Color.RED, 5, 255);
        });

        zoomOutButton.setOnClickListener(v -> {
            zoom /= 1.5f; // 缩小倍数
            canvas.drawColor(Color.BLACK); // 清空画布，重新绘制
            zoomPoints(rawDataPoints, zoom);
            drawFilledMap(plottedDataPoints, Color.RED, 5, 255);
        });
    }
    public static int findCharacter(byte[] data, byte target) {
        for (int i = 0; i < data.length; i++) {
            if (data[i] == target) {
                return i; // 找到目标字符，返回其索引
            }
        }
        return -1; // 如果没有找到目标字符，返回 -1
    }
    @SuppressLint("SetTextI18n")
    public static void processReceivedData(byte[] buffer, int bytes) {
        // 确保 RxData 的大小足够存储所有接收到的数据
        if (RxData == null) {
            RxData = new byte[1024 * 3]; // 假设每次接收的数据大小不超过 bytes
        }

        // 将接收到的数据存储到 RxData 中
        System.arraycopy(buffer, 0, RxData, totalBytesReceived, bytes);
        totalBytesReceived += bytes;

        // 如果接收了三次数据
        if (count++ >= 3) {
            count=0;
            // 将前两次数据拼接起来
            byte[] concatenatedData = new byte[1024 * 2];
            System.arraycopy(RxData, 0, concatenatedData, 0, 1024 * 2);

            // 显示拼接的数据在 ReceiveTextView 中
            // 这里假设 ReceiveTextView 是用来显示文本的视图
            // 你需要根据你的实际情况来更新文本视图的内容
            // 使用示例
            int index = findCharacter(RxData, (byte) 0xAB); // 在 RxData 中查找字符 'A'

            int temp=RxData[index + 77] & 0xFF;
            if(temp == 0x90){

            }
            else {
                index=-1;
            }
            if (index != -1) {
                // 找到了目标字符，进行相应的操作
                System.out.println("Found 'A' at index: " + index);
                // 获取 'A' 及其后面的数据
                byte[] dataAfterA = Arrays.copyOfRange(RxData, index, index+78);
                // 将字节数组转换为字符串
                String stringAfterA = new String(dataAfterA);
                // 在界面上显示字符串

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < 78; i++) {
                    int t = RxData[index + i] & 0xFF;
                    stringBuilder.append("0x").append(String.format("%02X", t)).append("   ");
                }
                StringBuilder TimestringBuilder = new StringBuilder();
                for (int i = 0; i < 3; i++) {
                    int t = RxData[index + i + 1] & 0xFF;
                    TimestringBuilder.append(String.format("%02d", t)).append(" ");
                }
                // 需要从RxData中某个index开始提取8个字节
                Longitude = bytesToDouble(RxData, 25+index);

                // 需要从RxData中某个index开始提取8个字节
                Latitude = bytesToDouble(RxData, 33+index);
            } else {
                // 没有找到目标字符
                System.out.println("Character 'A' not found.");
            }

            // 重置 RxData 和 totalBytesReceived
            System.arraycopy(RxData, 1024 * 2, RxData, 0, 1024);
            totalBytesReceived = 0;
            RxData=null;
        }
    }
    // 设置保存按钮的点击事件
    private void setupSaveButtons() {

        double[][] temp = new double[100][2];
        RecordDataButton.setOnClickListener(v -> {
            int min = 10;
            int max = 99;
            Random random = new Random();
            //int num = random.nextInt(max)%(max-min+1) + min;
            temp[aaa][0] = Longitude;
            //num = random.nextInt(max)%(max-min+1) + min;
            temp[aaa][1] = Latitude;
            aaa++;
        });
        SaveDataButton.setOnClickListener(v -> {
            SaveNewDataPoints = new double[aaa][2]; // 创建一个3行4列的二维数组
            for (int i = 0; i < aaa; i++) {
                SaveNewDataPoints[i][0] = temp[i][0];
                SaveNewDataPoints[i][1] = temp[i][1];
            }
            writeFileLonLat("niganma.csv",SaveNewDataPoints);
            readCSVFile("niganma.csv");
        });
    }

    // 设置滚动视图的触摸事件
    @SuppressLint("ClickableViewAccessibility")
    private void setupScrollView() {
        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private float startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if(event.getY()<getScreenWidth()) {
                            startX = event.getX();
                            startY = event.getY();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(event.getY()<getScreenWidth()){
                            float currentX = event.getX();
                            float deltaX = currentX - startX;
                            float currentY = event.getY();
                            float deltaY = currentY - startY;

                            translationX += (int) deltaX;
                            translationY -= (int) deltaY;

                            canvas.drawColor(Color.BLACK); // 清空画布，重新绘制
                            zoomPoints(rawDataPoints, zoom);
                            drawFilledMap(plottedDataPoints, Color.RED, 5, 255);

                            startX = currentX;
                            startY = currentY;
                        }
                        break;
                }
                return false;
            }
        });
    }

    // 缩放点的坐标
    private void zoomPoints(double[][] points, double magnification) {
        for (int i = 0; i < points.length; i++) {
            plottedDataPoints[i][0] = (points[i][0] - minXValue) * magnification + translationX;
            plottedDataPoints[i][1] = getScreenWidth() - translationY - (points[i][1] - minYValue) * magnification;
        }
    }

    // 绘制填充地图的方法
    private void drawFilledMap(double[][] points, @ColorInt int color, float width, int alpha) {
        drawPolygon(points, Color.WHITE, 5, 255);
        drawFilledPolygon(points, Color.BLUE, 5, 255);
        drawPoint(points, Color.RED, 20, 255);
    }

    // 绘制点的方法
    private void drawPoint(double[][] points, @ColorInt int color, float width, int alpha) {
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setAlpha(alpha);

        for (double[] point : points) {
            canvas.drawCircle((float) point[0], (float) point[1], width, paint);
        }
    }

    // 绘制多边形的方法
    private void drawPolygon(double[][] points, @ColorInt int color, float width, int alpha) {
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setAlpha(alpha);

        for (int i = 0; i < points.length; i++) {
            double[] currentPoint = points[i];
            double[] nextPoint = points[(i + 1) % points.length];

            canvas.drawLine((float) currentPoint[0], (float) currentPoint[1], (float) nextPoint[0], (float) nextPoint[1], paint);
        }
    }

    // 绘制填充多边形的方法
    private void drawFilledPolygon(double[][] points, @ColorInt int color, float width, int alpha) {
        paint.setColor(color);
        paint.setStrokeWidth(width);
        paint.setAlpha(alpha);

        Path path = new Path();

        for (int i = 0; i < points.length; i++) {
            double[] point = points[i];
            if (i == 0) {
                path.moveTo((float) point[0], (float) point[1]);
            } else {
                path.lineTo((float) point[0], (float) point[1]);
            }
        }

        path.close();
        canvas.drawPath(path, paint);
    }

    // 获取屏幕宽度的方法
    public int getScreenWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    // 寻找指定列的最小值
    private double findMinValue(double[][] points, int column) {
        double minValue = points[0][column];
        for (int i = 1; i < points.length; i++) {
            if (points[i][column] < minValue) {
                minValue = points[i][column];
            }
        }
        return minValue;
    }
}
