package com.example.administrator.wifitest.biz.Map.model.parkingLot;


import com.example.administrator.wifitest.biz.Map.model.AbstractDatabase;
import com.example.administrator.wifitest.biz.Map.model.parkingLot.parking.ParkingLot;
import com.example.administrator.wifitest.biz.Map.model.parkingLot.parking.ParkingLotData;
import com.example.administrator.wifitest.biz.Map.view.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database extends AbstractDatabase<MapView.Object> {
    private static Database _instance;
    private List<Car> cars = new ArrayList<>();
    private HashMap<Area, Car> areaMap = new HashMap<>();



    public static Database getInstance() {
        if (_instance == null) {
            _instance = new Database();
        }
        return _instance;
    }

    /**
     * 初始化数据库
     */
    @Override
    public void initBase() {
        areaMap = new HashMap<>();

        Area area = new Area("1", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("2", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6 * 2) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("3", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6 * 3) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("4", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6 * 4) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("5", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6 * 5) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("6", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3 * 2) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("7", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6 * 2) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3 * 2) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("8", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6 * 3) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3 * 2) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("9", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6 * 4) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3 * 2) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        area = new Area("10", (ParkingLotData.TOP_IN_MIN_X - ParkingLot.offsetX + ParkingLotData.ROAD_HORIZOTAL_LENGTH / 6 * 5) * ParkingLot.scale, (ParkingLotData.LEFT_IN_MIN_Y - ParkingLot.offsetY + ParkingLotData.ROAD_VERTICAL_LENGTH / 3 * 2) * ParkingLot.scale, 250 * ParkingLot.scale, 400 * ParkingLot.scale);
        updataList(area, ADD_DATA);

        // 将停车区域加入areaMap作为键
        for (MapView.Object obj : dataList) {
            areaMap.put((Area) obj, null);
        }


    }

    /**
     * 获得数据库中的数据
     *
     * @param listener
     */
    @Override
    public void fetchData(OnFetchListener listener) {
        if (listener != null) {
            listener.onFetch(dataList);
        }
    }

    /******************车辆操作******************/
    public void addCar(Car car) {
        cars.add(car);
        updataList(car, ADD_DATA);
    }

    public void removeCar(Car car) {
        cars.remove(car);
        updataList(car, REMOVE_DATA);
    }

    public List<Car> getCars() {
        return cars;
    }

    public Car findCarByName(String name) {
        for (Car c : getCars()) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }

    /******************停车区域停排车辆******************/

    public boolean parking(Area area, Car car) {
        if (areaMap.containsKey(area)) {
            if (areaMap.get(area) == null) {
                areaMap.put(area, car);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public Area closestArea(Car car) {
        Area closest = null;
        double distance = -1;
        double temp = -1;
        for (Map.Entry<Area, Car> entry : areaMap.entrySet()) {
            if (entry.getValue() == null) {
                if (closest == null) {
                    closest = entry.getKey();
                    distance = calcDistance(closest.getX(), closest.getY(), car.getX(),car.getY());
                } else {
                    temp = calcDistance(entry.getKey().getX(), entry.getKey().getY(), car.getX(),car.getY());
                    if (temp < distance) {
                        closest = entry.getKey();
                        distance = temp;
                    }
                }
            }
        }
        return closest;
    }

    public double calcDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * 停车区域是否有空位
     *
     * @return
     */
    public boolean isVacancy() {
        for (Map.Entry<Area, Car> entry : areaMap.entrySet()) {
            if (entry.getValue() == null) {
                return true;
            }
        }
        return false;
    }


}
