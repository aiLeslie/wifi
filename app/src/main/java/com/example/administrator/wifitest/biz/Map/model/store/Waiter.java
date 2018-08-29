package com.example.administrator.wifitest.biz.Map.model.store;

import android.graphics.Color;
import android.graphics.Rect;

import com.example.administrator.wifitest.biz.Map.model.Info;

import static com.example.administrator.wifitest.biz.Map.model.store.Store.scale;

public class Waiter implements Info {
    private String name; // 服务员名称
    private String desc; // 服务员描述
    private double x, y; // 服务员真实坐标
    private double width, length; // 服务员身材
    private int color = Color.parseColor("#a2c5ff"); // 代表颜色

    private Rect rect;

    public Waiter(String name, double x, double y, double width, double length) {
        this.name = name;
        this.x = x / scale;
        this.y = y / scale;
        this.width = width / scale;
        this.length = length / scale;

        rect = new Rect((int) (Store.offsetX + this.x - this.width / 2), (int) (Store.offsetY + this.y - this.length / 2), (int) (Store.offsetX + this.x + this.width / 2), (int) (Store.offsetY + this.y + this.length / 2));

    }
    /**
     * 设置描述
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
     * 设置位置坐标
     * @param x
     * @param y
     */
    public void setPosition(double x, double y) {
        this.x = x / scale;
        this.y = y / scale;
    }

    public void setPositionIncrement(double xIcm, double yIcm) {
        this.x += xIcm / scale;
        this.y += yIcm / scale;
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public int getRectColor() {
        return color;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getLength() {
        return length;
    }
}
