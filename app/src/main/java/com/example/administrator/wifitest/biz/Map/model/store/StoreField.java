package com.example.administrator.wifitest.biz.Map.model.store;

import android.graphics.Color;
import android.graphics.Rect;

import com.example.administrator.wifitest.biz.Map.model.Info;

public  class StoreField implements Info {

    private String[] fields = {"家具", "化妆品", "副食", "精品", "日用百货", "文具", "果蔬", "大米食用油"};

    private String name; // 分区名称
    private String desc; // 分区描述
    private double x, y; // 分区真实坐标
    private double width, length; // 分区长和宽
    private int color = Color.parseColor("#a2c5ff"); // 分区颜色

    private Rect rect;




    public StoreField(String name, double x, double y, double width, double length) {
        this.name = name;
        this.x = x / Store.scale;
        this.y = y / Store.scale;
        this.width = width / Store.scale;
        this.length = length / Store.scale;

        rect = new Rect((int) (Store.offsetX + this.x - this.width / 2), (int) (Store.offsetY + this.y - this.length / 2), (int) (Store.offsetX + this.x + this.width / 2), (int) (Store.offsetY + this.y + this.length / 2));

    }

    /**
     * 设置颜色
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
}
