package com.example.administrator.wifitest.biz.Map.controlor.ParkingLot;

import android.support.v7.app.AppCompatActivity;

import com.example.administrator.wifitest.R;
import com.example.administrator.wifitest.biz.Map.model.AbstractDatabase;
import com.example.administrator.wifitest.biz.Map.model.parkingLot.Car;
import com.example.administrator.wifitest.biz.Map.model.parkingLot.Database;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;

public class Controlor {
    private WeakReference<AppCompatActivity> activityWR;
    private Database database = Database.getInstance();

    /**
     * 构造方法
     * @param activity
     */
    public Controlor(AppCompatActivity activity) {
        this.activityWR = new WeakReference<AppCompatActivity>(activity);

    }

    /**
     * 加载数据
     * @param listener
     */
    public void loadData(AbstractDatabase.OnFetchListener listener) {
        database.fetchData(listener);
    }

    /**
     * 初始化车辆
     */
    public void initCars() {
        Random random = new Random();

        Car car = new Car("兰博基尼", activityWR.get().getResources(), R.drawable.white_car).setWidthAndLength(300, 200).setX(random.nextInt(1000)).setY(random.nextInt(1000));
//        Car car = new Car("兰博基尼", activityWR.get().getResources(), R.drawable.white_car).setWidthAndLength(200, 100).setOrientation(90).setX(ParkingLotData.RIGHT_IN_MIN_X + 600).setY(ParkingLotData.RIGHT_IN_MIN_Y + 200);
        database.addCar(car);
    }

    /**
     * 停车场进车
     * @param car
     */
    public void addCar(Car car) {
        database.addCar(car);
    }
    /**
     * 停车场出车
     * @param car
     */
    public void removeCar(Car car) {
        database.removeCar(car);
    }

    /**
     * 获得停车场的车辆集合
     * @return
     */
    public List<Car> getCars() {
        return database.getCars();
    }

    /**
     * 设置车辆位置
     * @param name
     * @param x
     * @param y
     */
    public void setCarLocation(String name, float x, float y) {
        database.findCarByName(name).setX(x).setY(y);
    }



}
