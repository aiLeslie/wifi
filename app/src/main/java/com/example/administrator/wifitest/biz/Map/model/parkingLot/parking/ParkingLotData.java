package com.example.administrator.wifitest.biz.Map.model.parkingLot.parking;

public class ParkingLotData {
    /**
     * 描述在马路里面位置
     */
    public static final byte POSITION_LEFT_OUT = 0;
    public static final byte POSITION_TOP_OUT = 1;
    public static final byte POSITION_RIGHT_OUT = 2;
    public static final byte POSITION_BOTTOM_OUT = 3;
    public static final byte POSITION_LEFT_IN = 4;
    public static final byte POSITION_TOP_IN = 5;
    public static final byte POSITION_RIGHT_IN = 6;
    public static final byte POSITION_BOTTOM_IN = 7;
    /**
     * 描述在马路被包围位置
     */
    public static final byte POSITION_SURROUNDED_ROAD = 8;
    /**
     * 描述在马路外面位置
     */
    public static final byte POSITION_OUTSIDE_ROAD_LEFT = 9;
    public static final byte POSITION_OUTSIDE_ROAD_TOP = 10;
    public static final byte POSITION_OUTSIDE_ROAD_BOTTOM = 11;
    public static final byte POSITION_OUTSIDE_ROAD_RIGHT = 12;
    public static final byte POSITION_OUTSIDE_ROAD_LEFT_TOP = 13;
    public static final byte POSITION_OUTSIDE_ROAD_RIGHT_TOP = 13;
    public static final byte POSITION_OUTSIDE_ROAD_LEFT_BOTTOM = 14;
    public static final byte POSITION_OUTSIDE_ROAD_RIGHT_BOTTOM = 15;
    /**
     * 路面尺寸
     */
    public static final double ROAD_WIDTH = 400;
    public static final double ROAD_HORIZOTAL_LENGTH = 3000;
    public static final double ROAD_VERTICAL_LENGTH = 2000;
    /**
     * 上方马路
     */
    public static final double TOP_OUT_MIN_X = 700 + ParkingLot.offsetX;
    public static final double TOP_OUT_MAX_X = TOP_OUT_MIN_X + ROAD_HORIZOTAL_LENGTH;
    public static final double TOP_OUT_MIN_Y = 100 + ParkingLot.offsetY;
    public static final double TOP_OUT_MAX_Y = TOP_OUT_MIN_Y + ROAD_WIDTH / 2;

    public static final double TOP_IN_MIN_X = TOP_OUT_MIN_X;
    public static final double TOP_IN_MAX_X = TOP_OUT_MAX_X;
    public static final double TOP_IN_MIN_Y = TOP_OUT_MAX_Y;
    public static final double TOP_IN_MAX_Y = TOP_OUT_MIN_Y + ROAD_WIDTH;
    /**
     * 下方马路
     */
    public static final double BOTTOM_IN_MIN_X = TOP_IN_MIN_X;
    public static final double BOTTOM_IN_MAX_X = TOP_IN_MAX_X;
    public static final double BOTTOM_IN_MIN_Y = TOP_IN_MAX_Y + ROAD_VERTICAL_LENGTH;
    public static final double BOTTOM_IN_MAX_Y = BOTTOM_IN_MIN_Y + ROAD_WIDTH / 2;

    public static final double BOTTOM_OUT_MIN_X = BOTTOM_IN_MIN_X;
    public static final double BOTTOM_OUT_MAX_X = BOTTOM_IN_MAX_X;
    public static final double BOTTOM_OUT_MIN_Y = BOTTOM_IN_MAX_Y;
    public static final double BOTTOM_OUT_MAX_Y = BOTTOM_IN_MIN_Y + ROAD_WIDTH;
    /**
     * 左方马路
     */
    public static final double LEFT_IN_MIN_X = TOP_OUT_MIN_X - ROAD_WIDTH / 2;
    public static final double LEFT_IN_MAX_X = TOP_OUT_MIN_X;
    public static final double LEFT_IN_MIN_Y = TOP_IN_MAX_Y;
    public static final double LEFT_IN_MAX_Y = LEFT_IN_MIN_Y + ROAD_VERTICAL_LENGTH;

    public static final double LEFT_OUT_MIN_X = LEFT_IN_MAX_X - ROAD_WIDTH;
    public static final double LEFT_OUT_MAX_X = LEFT_OUT_MIN_X + ROAD_WIDTH / 2;
    public static final double LEFT_OUT_MIN_Y = LEFT_IN_MIN_Y;
    public static final double LEFT_OUT_MAX_Y = LEFT_IN_MAX_Y;
    /**
     * 右方马路
     */
    public static final double RIGHT_IN_MIN_X = LEFT_IN_MAX_X + ROAD_HORIZOTAL_LENGTH;
    public static final double RIGHT_IN_MAX_X = RIGHT_IN_MIN_X + ROAD_WIDTH / 2;
    public static final double RIGHT_IN_MIN_Y = LEFT_IN_MIN_Y;
    public static final double RIGHT_IN_MAX_Y = LEFT_IN_MAX_Y;

    public static final double RIGHT_OUT_MIN_X = RIGHT_IN_MAX_X;
    public static final double RIGHT_OUT_MAX_X = RIGHT_OUT_MIN_X + ROAD_WIDTH / 2;
    public static final double RIGHT_OUT_MIN_Y = LEFT_IN_MIN_Y;
    public static final double RIGHT_OUT_MAX_Y = LEFT_IN_MAX_Y;


    public static byte checkBoundForPosition(double x, double y) {
        /**
         * 上方马路
         */
        if (y <= TOP_IN_MAX_Y) {
            if (x < TOP_OUT_MIN_X) {
                // 左上方马路外面
                return POSITION_OUTSIDE_ROAD_LEFT_TOP;
            } else if (x > TOP_OUT_MAX_X) {
                // 右上方马路外面
                return POSITION_OUTSIDE_ROAD_RIGHT_TOP;
            } else if (y < TOP_OUT_MIN_Y) {
                // 上方马路外面
                return POSITION_OUTSIDE_ROAD_TOP;
            } else if (y < TOP_OUT_MAX_Y) {
                // 上方外面马路
                return POSITION_TOP_OUT;
            } else {
                // 上方里面马路
                return POSITION_TOP_IN;
            }
        }


        /**
         * 下方马路
         */
        if (y >= BOTTOM_IN_MIN_Y) {

            if (x < TOP_OUT_MIN_X) {
                // 左下方马路外面
                return POSITION_OUTSIDE_ROAD_LEFT_BOTTOM;
            } else if (x > TOP_OUT_MAX_X) {
                // 右下方马路外面
                return POSITION_OUTSIDE_ROAD_RIGHT_BOTTOM;
            } else if (y > BOTTOM_OUT_MAX_Y) {
                // 下方马路外面
                return POSITION_OUTSIDE_ROAD_BOTTOM;
            } else if (y <= BOTTOM_IN_MAX_X) {
                // 下方里面马路
                return POSITION_BOTTOM_IN;
            } else {
                // 下方外面马路
                return POSITION_BOTTOM_OUT;
            }
        }

        /**
         * 左方马路
         */
        if (x <= LEFT_IN_MAX_X) {

            if (x < LEFT_OUT_MIN_X) {
                // 左方马路外面
                return POSITION_OUTSIDE_ROAD_LEFT;
            } else if (x >= LEFT_IN_MIN_X) {
                // 左方里面马路
                return POSITION_LEFT_IN;
            } else {
                // 左方外面马路
                return POSITION_LEFT_OUT;
            }
        }
        /**
         * 右方马路
         */
        if (x >= RIGHT_IN_MIN_X) {
            if (x > RIGHT_OUT_MAX_X) {
                // 右方马路外面
                return POSITION_OUTSIDE_ROAD_RIGHT;
            } else if (x <= RIGHT_IN_MAX_X) {
                // 右方里面马路
                return POSITION_RIGHT_IN;
            } else {
                // 右方外面马路
                return POSITION_RIGHT_OUT;
            }
        }

        return POSITION_SURROUNDED_ROAD;


    }

    public static boolean isOutside(double x, double y) {
        if (checkBoundForPosition(x, y) >= 8) return true;
        else return false;
    }

}
