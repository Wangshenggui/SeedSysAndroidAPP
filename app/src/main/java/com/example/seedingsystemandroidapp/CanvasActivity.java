package com.example.seedingsystemandroidapp;

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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CanvasActivity extends AppCompatActivity {

    private ImageView iv1;
    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;

    private Button zoomInButton;
    private Button zoomOutButton;
    private TextView testTextView;
    private TextView mapTextView;

    private float zoom = 1;
    private int translationX = 0;
    private int translationY = 0;
    private double minXValue;
    private double minYValue;

//    106.613138, 26.379819
//    106.613273, 26.379863
//    106.61329, 26.379694
//    106.613234, 26.379672
//    106.613219, 26.379702
//    106.613186, 26.379697

    private double[][] rawDataPoints;
    private double[][] plottedDataPoints;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canvas);

        // 初始化视图
        initializeViews();


//        StringBuilder csvData = new StringBuilder();
//        csvData.append(116.433606).append(",").append(39.829946).append("\n");
//        csvData.append(116.438603).append(",").append(39.830069).append("\n");
//
//        csvData.append(116.441437).append(",").append(39.827215).append("\n");
//        csvData.append(116.442767).append(",").append(39.826711).append("\n");
//
//        csvData.append(116.442671).append(",").append(39.82494).append("\n");
//        csvData.append(116.433397).append(",").append(39.825174).append("\n");
//
//        csvData.append(116.432388).append(",").append(39.827572).append("\n");
//        // 添加更多行...
//
        String csvFileName = "mydata.csv";
//
//        FileOutputStream fos = null;
//        try {
//            fos = openFileOutput(csvFileName, Context.MODE_PRIVATE);
//            fos.write(csvData.toString().getBytes());
//            Toast.makeText(this, "CSV file saved successfully", Toast.LENGTH_SHORT).show();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (fos != null) {
//                    fos.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
        // 写入成功后立即读取文件内容
        readCSVFile(csvFileName);


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


        String directoryPath = getFilesDir().getAbsolutePath();; // 要查找的目录路径
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.out.println("指定路径不是一个目录。");
            return;
        }

        listFiles(directory);

    }
    @SuppressLint("SetTextI18n")
    private void listFiles(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果要打印子目录，可以使用 file.getName() 或 file.getPath()
                    System.out.println("目录: " + file.getName());
                    listFiles(file); // 递归调用，处理子目录
                    mapTextView.setText(mapTextView.getText() + file.getPath() + "\n");
                } else {
                    // 如果要打印文件名，可以使用 file.getName() 或 file.getPath()
                    System.out.println("文件: " + file.getName());
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
        drawFilledPolygon(points, Color.BLUE, 5, 128);
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
