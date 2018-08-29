package com.example.administrator.wifitest.biz.Map.model.parkingLot;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.example.administrator.wifitest.biz.Map.model.parkingLot.parking.ParkingLot;
import com.example.administrator.wifitest.biz.Map.model.parkingLot.parking.ParkingLotData;
import com.example.administrator.wifitest.biz.Map.view.MapView;

public class Car implements MapView.Object{
    private String name; // 车辆名称
    private String desc; // 车辆描述
    private double x, y; // 车辆坐标
    private double width, length;
    private Bitmap bitmap;
    public byte position = -1;
    private int angle = 0;



    public Car(String name, Resources res, int resId) {
        this.name = name;
        bitmap = BitmapFactory.decodeResource(res, resId);

    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public synchronized Car setWidthAndLength(float newWidth, float newHeight) {
        width = newWidth;
        length = newHeight;
        bitmap = resizeBitmap(bitmap, newWidth, newHeight);
        return this;
    }

    /**
     * 设置绝对角度
     * @param angle
     */
    public void setAngle(int angle) {
        if (angle <= 0 || angle > 360)
        this.angle = angle;
    }

    /**
     * 相对顺时针旋转一个角度
     * @param orientationDegree
     * @return
     */
    public synchronized Car setOrientation(final int orientationDegree) {
        angle += orientationDegree;
        bitmap = adjustRotation(bitmap, orientationDegree);
        return this;
    }

    public synchronized Car setPosition(double x, double y) {
        this.x = x / ParkingLot.scale + ParkingLot.offsetX;
        this.y = y / ParkingLot.scale + ParkingLot.offsetY;
        return this;
    }


    /**
     * 设置描述
     *
     * @param desc
     */
    public synchronized void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 获取描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 得到名字
     */
    public String getName() {
        return name;
    }

    /**
     * 得到X坐标
     */
    public double getX() {
        return x;
    }
    /**
     * 得到Y坐标
     */
    public double getY() {
        return y;
    }
    /**
     * 设置X坐标
     */
    public synchronized Car setX(double x) {
        this.x = x;
        return this;
    }
    /**
     * 设置Y坐标
     */
    public synchronized Car setY(double y) {
        this.y = y;
        return this;
    }

    /**
     * 重新调整bitmap的大小
     *
     * @param newWidth
     * @param newHeight
     * @param bitmap
     * @return
     */
    private synchronized Bitmap resizeBitmap(Bitmap bitmap, float newWidth, float newHeight) {
        // 实例化
        Matrix matrix = new Matrix();
        // 计算新bitmap与旧bitmap的大小比例
        matrix.postScale(newWidth / bitmap.getWidth(), newHeight / bitmap.getHeight());
        // 创建新的bitmap对象
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        // 返回bitmap对象
        return newBitmap;
    }

    private synchronized Bitmap adjustRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }

        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);

        return bm1;
    }


    @Override
    public synchronized void drawMyself(Canvas canvas, Paint paint) {
        canvas.drawBitmap(bitmap, (float) x, (float) y, paint);
        position = ParkingLotData.checkBoundForPosition(x, y);
        switch (position) {
//            case ParkingLotData.
        }




    }

}
