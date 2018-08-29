package com.example.administrator.wifitest.biz.Map.model.parkingLot.parking;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.example.administrator.wifitest.biz.Map.model.Info;
import com.example.administrator.wifitest.biz.Map.view.MapView;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot implements Info {
    // 停车场基本属性
    public static ParkingLot _instance; // 停车场实例
    public static String name = "智慧停车场";
    public static final double offsetX = 100; // 圆心偏移量
    public static final double offsetY = 100; // 圆心偏移量
    public static final double scale = 100; // 伸缩比例
    public final double width = 5000, length = 5000; // 尺寸
    private Rect rect; // 绘画停车场矩形
    private int color = Color.parseColor("#5e5e5e"); // 路面颜色
    private List<MapView.Object> roads = new ArrayList<>(); // 马路集合



    private ParkingLot() {
        rect = new Rect((int) (ParkingLot.offsetX), (int) (ParkingLot.offsetY), (int) (ParkingLot.offsetX + this.width), (int) (ParkingLot.offsetY + this.length));
        initRoad();

    }

    // 建立马路
    private void initRoad() {
        /**
         * 上方直路
         */
        StraightRoad straight = new StraightRoad((int) (ParkingLotData.TOP_OUT_MIN_X - ParkingLot.offsetX), (int) (ParkingLotData.TOP_OUT_MIN_Y - ParkingLot.offsetY), ParkingLotData.ROAD_WIDTH, ParkingLotData.ROAD_HORIZOTAL_LENGTH);
        roads.add(straight.setRoadOrientation(straight.ROAD_HORIZONTAL_ORIENTATION));
        /**
         * 右上方弯路
         */
        WindingRoad winding = new WindingRoad(ParkingLotData.TOP_OUT_MIN_X - ParkingLot.offsetX, ParkingLotData.TOP_OUT_MIN_Y - ParkingLot.offsetY, ParkingLotData.RIGHT_OUT_MAX_X - ParkingLot.offsetX, ParkingLotData.RIGHT_OUT_MAX_Y - ParkingLot.offsetY);
        roads.add(winding.setWidth(ParkingLotData.ROAD_WIDTH).setPositon(winding.POSITION_RIGHT_TOP).buildPath());

        /**
         * 左上方弯路
         */
        winding = new WindingRoad(ParkingLotData.TOP_OUT_MIN_X - ParkingLot.offsetX, ParkingLotData.TOP_OUT_MIN_Y - ParkingLot.offsetY, ParkingLotData.LEFT_OUT_MIN_X - ParkingLot.offsetX, ParkingLotData.LEFT_OUT_MIN_Y - ParkingLot.offsetY);
        roads.add(winding.setWidth(ParkingLotData.ROAD_WIDTH).setPositon(winding.POSITION_lEFT_TOP).buildPath());

        /**
         * 右方直路
         */
        straight = new StraightRoad((int) (ParkingLotData.RIGHT_IN_MIN_X - ParkingLot.offsetX), (int) (ParkingLotData.RIGHT_IN_MIN_Y - ParkingLot.offsetY), ParkingLotData.ROAD_WIDTH, ParkingLotData.ROAD_VERTICAL_LENGTH);
        roads.add(straight.setRoadOrientation(straight.ROAD_VERTICAL_ORIENTATION));
        /**
         * 左方直路
         */
        straight = new StraightRoad((int) (ParkingLotData.LEFT_OUT_MIN_X - ParkingLot.offsetX), (int) (ParkingLotData.LEFT_OUT_MIN_Y - ParkingLot.offsetY), ParkingLotData.ROAD_WIDTH, ParkingLotData.ROAD_VERTICAL_LENGTH);
        roads.add(straight.setRoadOrientation(straight.ROAD_VERTICAL_ORIENTATION));

        /**
         * 下方直路
         */
        straight = new StraightRoad((int) (ParkingLotData.BOTTOM_IN_MIN_X - ParkingLot.offsetX), (int) (ParkingLotData.BOTTOM_IN_MIN_Y - ParkingLot.offsetY), ParkingLotData.ROAD_WIDTH, ParkingLotData.ROAD_HORIZOTAL_LENGTH);
        roads.add(straight.setRoadOrientation(straight.ROAD_HORIZONTAL_ORIENTATION));

        /**
         * 右下方弯路
         */
        winding = new WindingRoad((int) (ParkingLotData.RIGHT_OUT_MAX_X - ParkingLot.offsetX), (int) (ParkingLotData.RIGHT_OUT_MAX_Y - ParkingLot.offsetY),  (int) (ParkingLotData.BOTTOM_OUT_MAX_X - ParkingLot.offsetX), (int) (ParkingLotData.BOTTOM_OUT_MAX_Y - ParkingLot.offsetY));
        roads.add(winding.setWidth(ParkingLotData.ROAD_WIDTH).setPositon(winding.POSITION_RIGHT_BOTTOM).setFirst(winding.FIRST_Y).buildPath());

        /**
         * 左下方弯路
         */
        winding = new WindingRoad((int) (ParkingLotData.LEFT_OUT_MIN_X - ParkingLot.offsetX), (int) (ParkingLotData.LEFT_OUT_MIN_Y - ParkingLot.offsetY),  (int) (ParkingLotData.BOTTOM_OUT_MAX_X - ParkingLot.offsetX), (int) (ParkingLotData.BOTTOM_OUT_MAX_Y - ParkingLot.offsetY));
        roads.add(winding.setWidth(ParkingLotData.ROAD_WIDTH).setPositon(winding.POSITION_lEFT_BOTTOM).setFirst(winding.FIRST_Y).buildPath());
    }

    public static ParkingLot getInstance() {
        if (_instance == null) {
            _instance = new ParkingLot();

        }
        return _instance;
    }

    public ParkingLot setColor(int color) {
        this.color = color;
        return this;
    }

    /**
     * 绘画停车场
     *
     * @param canvas
     * @param paint
     */
    public static void drawMyself(Canvas canvas, Paint paint) {
        // 重置画笔
        paint.reset();

        if (_instance == null) getInstance();

        paint.setColor(_instance.color);
        canvas.drawRect(_instance.getRect(), paint);

        // 绘制名字文本
//        paint.setColor(Color.BLACK);
//        paint.setTextSize(50);
//        canvas.drawText(_instance.getName(), (int) (_instance.getX()), (int) (_instance.getY() - 50), paint);

        for (MapView.Object road : _instance.roads) {
            road.drawMyself(canvas, paint);
        }
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
        return offsetX;
    }

    @Override
    public double getY() {
        return offsetY;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getLength() {
        return length;
    }

    public class WindingRoad implements MapView.Object {
        public final byte FIRST_X = 1;
        public final byte FIRST_Y = 2;

        public final byte POSITION_lEFT_TOP = -1;
        public final byte POSITION_RIGHT_TOP = -2;
        public final byte POSITION_lEFT_BOTTOM = -3;
        public final byte POSITION_RIGHT_BOTTOM = -4;

        private Path inPath = new Path();
        private Path outPath = new Path();
        public double x1, y1;
        public double x2, y2;
        public double width;
        private byte first = FIRST_X;
        private boolean oneAdd = true;
        private boolean twoAdd = true;
        private byte positon = POSITION_lEFT_TOP;
        public int color = Color.parseColor("#fff674"); // 颜色


        public WindingRoad(double x1, double y1, double x2, double y2) {
            this.x1 = x1 + offsetX;
            this.y1 = y1 + offsetY;
            this.x2 = x2 + offsetX;
            this.y2 = y2 + offsetY;
        }

        public WindingRoad setWidth(double width) {
            this.width = width;
            return this;
        }

        public WindingRoad setOneAdd(boolean oneAdd) {
            this.oneAdd = oneAdd;
            return this;
        }

        public WindingRoad setTwoAdd(boolean twoAdd) {
            this.twoAdd = twoAdd;
            return this;
        }

        public WindingRoad setFirst(byte first) {
            this.first = first;
            return this;
        }

        public WindingRoad setPositon(byte positon) {
            this.positon = positon;
            return this;
        }

        public WindingRoad buildPath() {
            outPath.reset();
            inPath.reset();
            if (FIRST_X == first) {
                outPath.moveTo((float) x1, (float) y1);
                outPath.lineTo((float) (x2), (float) y1);
                outPath.lineTo((float) (x2), (float) (y2));

                if (positon == POSITION_lEFT_TOP) {
                    inPath.moveTo((float) (x1), (float) (y1 + width / 2));
                    inPath.lineTo((float) (x2 + width / 2), (float) (y1 + width / 2));
                    inPath.lineTo((float) (x2 + width / 2), (float) (y2));
                } else if (positon == POSITION_RIGHT_TOP) {
                    inPath.moveTo((float) (x1), (float) (y1 + width / 2));
                    inPath.lineTo((float) (x2 - width / 2), (float) (y1 + width / 2));
                    inPath.lineTo((float) (x2 - width / 2), (float) (y2));
                }

            }else if (FIRST_Y == first){
                outPath.moveTo((float) (x1), (float) (y1));
                outPath.lineTo((float) (x1), (float)(y2) );
                outPath.lineTo((float) (x2), (float) (y2));
                if (positon == POSITION_lEFT_BOTTOM) {
                    inPath.moveTo((float) (x1 + width / 2), (float) (y1));
                    inPath.lineTo((float) (x1 + width / 2), (float) (y2 - width / 2));
                    inPath.lineTo((float) (x2), (float) (y2 - width / 2));
                } else if (positon == POSITION_RIGHT_BOTTOM) {
                    inPath.moveTo((float) (x1 - width / 2), (float) (y1));
                    inPath.lineTo((float) (x1 - width / 2), (float) (y2 - width / 2));
                    inPath.lineTo((float) (x2), (float) (y2 - width / 2));
                }
            }
            return this;

        }

        @Override
        public void drawMyself(Canvas canvas, Paint paint) {
            paint.reset();
            paint.setStyle(Paint.Style.STROKE);


            // 绘画马路两边边线
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth((int) (width / 25));
            canvas.drawPath(outPath, paint);

            paint.setColor(color);
            paint.setStrokeWidth((int) (width / 50));
            canvas.drawPath(inPath, paint);


        }
    }

    public class StraightRoad implements MapView.Object {
        // 马路方向常量
        public final byte ROAD_HORIZONTAL_ORIENTATION = 1;
        public final byte ROAD_VERTICAL_ORIENTATION = 2;

        // 箭头方向常量
        public final byte ARROWS_LEFT_ORIENTATION = -1;
        public final byte ARROWS_TOP_ORIENTATION = -2;
        public final byte ARROWS_RIGHT_ORIENTATION = -3;
        public final byte ARROWS_BOTTOM_ORIENTATION = -4;


        public Rect rect;
        public double x, y; // 马路坐标
        public double width, length; // 马路尺寸
        public byte roadOrientation = ROAD_HORIZONTAL_ORIENTATION; // 方向
        public byte arrowsOrientation = ARROWS_LEFT_ORIENTATION; // 方向
        public int color = Color.parseColor("#fff674"); // 颜色
        public Path arrows = new Path(); // 箭头路径
        public final int arrowsLength = 150;

        {
            arrows.reset();
            arrows.moveTo(0, 0);
            arrows.lineTo(30, -30);
            arrows.lineTo(30, -10);
            arrows.lineTo(150, -10);
            arrows.lineTo(150, 10);
            arrows.lineTo(30, 10);
            arrows.lineTo(30, 30);
            arrows.close();
        }

        public StraightRoad(double x, double y, double width, double length) {
            this.x = x + offsetX;
            this.y = y + offsetY;
            this.width = width;
            this.length = length;
            rect = new Rect((int) x, (int) y, (int) (x + width), (int) (y + length));
        }

        public StraightRoad setRoadOrientation(byte roadOrientation) {
            this.roadOrientation = roadOrientation;
            return this;
        }

        @Override
        public void drawMyself(Canvas canvas, Paint paint) {
            paint.reset();
            paint.setStyle(Paint.Style.FILL);


            if (roadOrientation == ROAD_HORIZONTAL_ORIENTATION) { // 马路方向水平
                // 绘画马路两边边线
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth((int) (width / 25));
                canvas.drawLine((int) x, (int) (y), (int) (x + length), (int) (y), paint);
                canvas.drawLine((int) x, (int) (y + width), (int) (x + length), (int) (y + width), paint);
                // 绘画马路中间边线
                paint.setColor(color);
                paint.setStrokeWidth((int) (width / 50));
                canvas.drawLine((int) x, (int) (y + width / 2), (int) (x + length), (int) (y + width / 2), paint);

                // 绘画上边马路标志线
                paint.setStrokeWidth((int) (width / 6));
                setArrowsOrientation(ARROWS_LEFT_ORIENTATION);
                int begin = 50; // 初始位置
                for (int i = 0; i < (int) (length - begin) / (arrowsLength * 3); i++) {
                    arrows.offset((int) (x + i * arrowsLength * 3 + begin), (int) (y + width / 4));
                    canvas.drawPath(arrows, paint);
                    arrows.offset(-(int) (x + i * arrowsLength * 3 + begin), -(int) (y + width / 4));
                }

                // 绘画下边马路标志线
                paint.setStrokeWidth((int) (width / 6));
                setArrowsOrientation(ARROWS_RIGHT_ORIENTATION);
                begin = 50; // 初始位置
                for (int i = 0; i < (int) (length - begin) / (arrowsLength * 3); i++) {
                    if (i != 0) {
                        arrows.offset((int) (x + i * arrowsLength * 3 + begin), (int) (y + width / 4 * 3));
                        canvas.drawPath(arrows, paint);
                        arrows.offset(-(int) (x + i * arrowsLength * 3 + begin), -(int) (y + width / 4 * 3));
                    }

                }


            } else if (roadOrientation == ROAD_VERTICAL_ORIENTATION) { // 马路方向垂直
                // 绘画马路两边边线
                paint.setColor(Color.WHITE);
                paint.setStrokeWidth((int) (width / 25));
                canvas.drawLine((int) x, (int) (y), (int) (x), (int) (y + length), paint);
                canvas.drawLine((int) (x + width), (int) y, (int) (x + width), (int) (y + length), paint);
                // 绘画马路中间边线
                paint.setColor(color);
                paint.setStrokeWidth((int) (width / 50));
                canvas.drawLine((int) (x + width / 2), (int) y, (int) (x + width / 2), (int) (y + length), paint);

                // 绘画左边马路标志线
                paint.setStrokeWidth((int) (width / 6));
                setArrowsOrientation(ARROWS_BOTTOM_ORIENTATION);
                int begin = 50; // 初始位置
                for (int i = 0; i < (int) (length - begin) / (arrowsLength * 3); i++) {
                    if (i != 0) {
                        arrows.offset((int) (x + width / 4), (int) (y + i * arrowsLength * 3 + begin));
                        canvas.drawPath(arrows, paint);
                        arrows.offset(-(int) (x + width / 4), -(int) (y + i * arrowsLength * 3 + begin));
                    }

                }

                // 绘画右边马路标志线
                paint.setStrokeWidth((int) (width / 6));
                setArrowsOrientation(ARROWS_TOP_ORIENTATION);
                begin = 50; // 初始位置
                for (int i = 0; i < (int) (length - begin) / (arrowsLength * 3); i++) {

                    arrows.offset((int) (x + width / 4 * 3), (int) (y + i * arrowsLength * 3 + begin));
                    canvas.drawPath(arrows, paint);
                    arrows.offset(-(int) (x + width / 4 * 3), -(int) (y + i * arrowsLength * 3 + begin));

                }


            }
        }

        /**
         * 设置箭头方向
         *
         * @param orientation
         */
        public void setArrowsOrientation(byte orientation) {
            if (orientation == arrowsOrientation) return;
            Matrix matrix = new Matrix();


            switch (orientation) {
                case ARROWS_LEFT_ORIENTATION:
                    switch (arrowsOrientation) {
                        case ARROWS_LEFT_ORIENTATION:
                            break;
                        case ARROWS_TOP_ORIENTATION:
                            matrix.setRotate(90); //旋转顺时针90度
                            break;
                        case ARROWS_RIGHT_ORIENTATION:
                            matrix.setRotate(180); //旋转顺时针180度
                            break;
                        case ARROWS_BOTTOM_ORIENTATION:
                            matrix.setRotate(270); //旋转顺时针270度
                            break;
                    }
                    break;
                case ARROWS_TOP_ORIENTATION:
                    switch (arrowsOrientation) {
                        case ARROWS_LEFT_ORIENTATION:
                            matrix.setRotate(90); //旋转顺时针90度
                            break;
                        case ARROWS_TOP_ORIENTATION:

                            break;
                        case ARROWS_RIGHT_ORIENTATION:
                            matrix.setRotate(270); //旋转顺时针270度
                            break;
                        case ARROWS_BOTTOM_ORIENTATION:
                            matrix.setRotate(180); //旋转顺时针180度
                            break;
                    }
                    break;
                case ARROWS_RIGHT_ORIENTATION:
                    switch (arrowsOrientation) {
                        case ARROWS_LEFT_ORIENTATION:
                            matrix.setRotate(180); //旋转顺时针180度
                            break;
                        case ARROWS_TOP_ORIENTATION:
                            matrix.setRotate(270); //旋转顺时针270度
                            break;
                        case ARROWS_RIGHT_ORIENTATION:
                            break;
                        case ARROWS_BOTTOM_ORIENTATION:
                            matrix.setRotate(90); //旋转顺时针90度
                            break;
                    }
                    break;
                case ARROWS_BOTTOM_ORIENTATION:
                    switch (arrowsOrientation) {
                        case ARROWS_LEFT_ORIENTATION:
                            matrix.setRotate(270); //旋转顺时针270度
                            break;
                        case ARROWS_TOP_ORIENTATION:
                            matrix.setRotate(180); //旋转顺时针180度
                            break;
                        case ARROWS_RIGHT_ORIENTATION:
                            matrix.setRotate(90); //旋转顺时针90度
                            break;
                        case ARROWS_BOTTOM_ORIENTATION:

                            break;

                    }
                    break;
            }
            arrows.transform(matrix);//应用变形
            arrowsOrientation = orientation;
        }
    }


}
