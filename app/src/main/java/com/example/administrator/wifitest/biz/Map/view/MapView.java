package com.example.administrator.wifitest.biz.Map.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import com.example.administrator.wifitest.biz.Map.model.Info;
import com.example.administrator.wifitest.biz.Map.model.parkingLot.Car;
import com.example.administrator.wifitest.biz.Map.model.parkingLot.parking.ParkingLot;
import com.example.administrator.wifitest.view.FloatWater;
import com.example.administrator.wifitest.view.MySurfaceView;

import java.util.List;

public class MapView extends MySurfaceView {
    private float ox = 0; // 原点x坐标
    private float oy = 0; // 原点y坐标
    private float tempX = -1; // 用户点击屏幕x坐标
    private float tempY = -1; // 用户点击屏幕y坐标
    private float oxLBound = -1000;
    private float oxRBound = 0;
    private float oyTBound = -500;
    private float oyBBound = 500;
    private boolean firstClick = false;
    private float scale = 0.5f;
    public final int DISPLAY_WIDTH;
    public final int DISPLAY_HEIGHT;
    private FloatWater water;

    // 非静态初始化语句块
    {
        // 获得屏幕宽和长
        WindowManager manager = activityWR.get().getWindowManager();
        DisplayMetrics INMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(INMetrics);
        DISPLAY_WIDTH = INMetrics.widthPixels;
        DISPLAY_HEIGHT = INMetrics.heightPixels;

    }

    {

        water = new FloatWater(-200, 500, DISPLAY_WIDTH + 200);
    }

    public MapView(Context context) {
        super(context);

        // 开启绘画功能
        setDrawable((Drawable) activityWR.get());


    }

    public MapView(Context context, AttributeSet attrs) {

        super(context, attrs);
        // 开启绘画功能
        setDrawable((Drawable) activityWR.get());
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    /**
     * 检测边界
     */
    public void checkBound() {
        if (ox < oxLBound) {
            ox = oxLBound;
        } else if (ox > oxRBound) {
            ox = oxRBound;
        }
        if (oy < oyTBound) {
            oy = oyTBound;
        } else if (oy > oyBBound) {
            oy = oyBBound;
        }
    }


    /**
     * 回归原点(0,0)
     */
    private void resetOrigin() {
        ox = 0;
        oy = 0;
    }

    /**
     * 检测是否双击屏幕
     *
     * @return
     */
    private boolean isDoubleClick() {
        if (!firstClick) {
            // 点击一次
            firstClick = true;
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        sleep(1000);
                        firstClick = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            return false;
        } else {
            // 快速点击两次
            firstClick = false;
            return true;

        }
    }

    /**
     * 翻转scale
     */
    private void reverseScale() {
        if (scale == 1) {
            scale = 2;
        } else if (scale == 2) {
            scale = 1;
        } else {
            scale = 1;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                startUpdate();

                // 放大缩小视图
//                if (isDoubleClick()) {
//                    reverseScale();
//                }

                // 获取按下的坐标
                tempX = event.getX();
                tempY = event.getY();



                break;
            case MotionEvent.ACTION_MOVE:
                // 检查边界
//                checkBound();
                // 获得当前按下的坐标增加量加上原先的原点坐标(更新原点坐标)
                ox += event.getX() - tempX;
                oy += event.getY() - tempY;
                // 更新当前按下的坐标
                tempX = event.getX();
                tempY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
//                stopUpdate();
//                Toast.makeText(activitySR.get(), Toast.LENGTH_SHORT).show();

                break;
        }

        return true;
    }

    /**
     * 平移画布
     */
    public void translateCanvas() {
        canvas.translate(ox, oy);
    }

    /**
     * 伸缩画布
     */
    public void telescopicCanvas() {
        canvas.scale(scale, scale);
    }

    /**
     * 绘画对象
     *
     * @param info
     */

    private void drawInfo(Info info) {

        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        // 绘制矩形
        paint.setColor(info.getRectColor());
        Rect rect = info.getRect();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // 绘画圆角矩形
            canvas.drawRoundRect(rect.left, rect.top, rect.right, rect.bottom, 10, 10, paint);
        } else {
            // 绘画一般矩形
            canvas.drawRect(rect, paint);
        }

        // 绘制名字文本
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        canvas.drawText(info.getName(), (int) (info.getX() - 20), (int) (info.getY()), paint);


    }


    public void drawAll(List<Object> data) {
        // 如果没有数据不绘画
        if (data == null) return;

        // 重置画笔
        paint.reset();

        // 绘画整个停车场
        ParkingLot.drawMyself(canvas, paint);

        // 检测马路位置正确性
//        paint.setColor(Color.RED);
//        canvas.drawRect((float) ParkingLotData.RIGHT_OUT_MIN_X, (float)ParkingLotData.RIGHT_OUT_MIN_Y,(float)ParkingLotData.RIGHT_OUT_MAX_X,(float)ParkingLotData.RIGHT_OUT_MAX_Y, paint);


        // 绘画多个对象(停车区域和车辆)
        for (Object obj : data) {
            obj.drawMyself(canvas, paint);
        }



    }



    /**
     * 绘画动画
     */
    public void startAnimation() {
        water.floatAnimation(canvas);
    }


    public interface Object {
        public void drawMyself(Canvas canvas, Paint paint);
    }

    /**
     * 显示坐标
     */
    public void drawPosition(double x, double y) {
        paint.setColor(Color.RED);
        canvas.drawPoint((float) x, (float) y, paint);
        canvas.drawText("(" + x + "," + y + ")", (float) x, (float) y, paint);
    }


}
