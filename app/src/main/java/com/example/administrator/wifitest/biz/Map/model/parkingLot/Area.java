package com.example.administrator.wifitest.biz.Map.model.parkingLot;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.example.administrator.wifitest.biz.Map.model.Info;
import com.example.administrator.wifitest.biz.Map.model.parkingLot.parking.ParkingLot;
import com.example.administrator.wifitest.biz.Map.view.MapView;

public class Area implements Info, MapView.Object {
    private String name; // 分区名称
    private String desc; // 分区描述
    private double x, y; // 分区真实坐标
    private double width, length; // 分区长和宽
    private int color = Color.parseColor("#a6ffa5"); // 分区颜色
    public static final int GREEN = Color.parseColor("#a6ffa5");
    public static final int RED = Color.parseColor("#ff9193");

    private Rect rect;

    public Area(String name, double x, double y, double width, double length) {
        this.name = name;

        this.x = x / ParkingLot.scale + ParkingLot.offsetX;
        this.y = y / ParkingLot.scale + ParkingLot.offsetY;
        this.width = width / ParkingLot.scale;
        this.length = length / ParkingLot.scale;

        rect = new Rect((int) (this.x - this.width / 2), (int) (this.y - this.length / 2), (int) (this.x + this.width / 2), (int) (this.y + this.length / 2));

    }


    /**
     * 设置颜色
     *
     * @param color
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * 设置描述
     *
     * @param desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 获取描述
     */
    public String getDesc() {
        return desc;
    }

    /**
     * 得到描述分区矩形
     */
    @Override
    public Rect getRect() {
        return rect;
    }

    /**
     * 得到描述分区颜色
     */
    @Override
    public int getRectColor() {
        return color;
    }

    /**
     * 得到分区名字
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 得到分区X坐标
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * 得到分区Y坐标
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * 得到分区宽度
     */
    @Override
    public double getWidth() {
        return width;
    }

    /**
     * 得到分区长度
     */
    @Override
    public double getLength() {
        return length;
    }

    @Override
    public void drawMyself(Canvas canvas, Paint paint) {
        paint.reset();
        paint.setStyle(Paint.Style.FILL);
        // 绘制矩形
        paint.setColor(getRectColor());
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            // 绘画圆角矩形
//            canvas.drawRoundRect(rect.left, rect.top, rect.right, rect.bottom, 10, 10, paint);
//        } else {
//            // 绘画一般矩形
//            canvas.drawRect(rect, paint);
//        }

        canvas.drawCircle((float) x, (float)y, (float) length / 5, paint);

        // 绘制名字文本
        paint.setColor(Color.BLACK);
        paint.setTextSize(60);
        canvas.drawText(name, (int) (getX() - 20), (int) (getY() + 10), paint);

        // 绘制边线
        paint.setColor(Color.YELLOW);
        paint.setStrokeWidth(20);
        double lineLen = length / 6;
        // 左上角
        canvas.drawLine((float) (x - width / 2), (float) (y - length / 2 + lineLen), (float) (x - width / 2), (float) (y - length / 2), paint);
        canvas.drawLine((float) (x - width / 2), (float) (y - length / 2), (float) (x - width / 2 + lineLen), (float) (y - length / 2), paint);

        // 左下角
        canvas.drawLine((float) (x - width / 2), (float) (y + length / 2 - lineLen), (float) (x - width / 2), (float) (y + length / 2), paint);
        canvas.drawLine((float) (x - width / 2), (float) (y + length / 2), (float) (x - width / 2 + lineLen), (float) (y + length / 2), paint);

        // 右下角
        canvas.drawLine((float) (x + width / 2 - lineLen), (float) (y - length / 2), (float) (x + width / 2), (float) (y - length / 2), paint);
        canvas.drawLine((float) (x + width / 2), (float) (y - length / 2), (float) (x + width / 2), (float) (y - length / 2 + lineLen), paint);

        // 右上角
        canvas.drawLine((float) (x + width / 2 - lineLen), (float) (y + length / 2), (float) (x + width / 2), (float) (y + length / 2), paint);
        canvas.drawLine((float) (x + width / 2), (float) (y + length / 2), (float) (x + width / 2), (float) (y + length / 2 - lineLen), paint);
    }
}
