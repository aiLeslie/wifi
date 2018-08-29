package com.example.administrator.wifitest.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.administrator.wifitest.R;
import com.example.administrator.wifitest.biz.Map.controlor.ParkingLot.Controlor;
import com.example.administrator.wifitest.biz.Map.model.AbstractDatabase;
import com.example.administrator.wifitest.biz.Map.view.MapView;
import com.example.administrator.wifitest.biz.handle.MyHandler;
import com.example.administrator.wifitest.view.MySurfaceView;

import java.util.List;

import static com.example.administrator.wifitest.ApplicationUtil.MODE;
import static com.example.administrator.wifitest.ApplicationUtil.client;
import static com.example.administrator.wifitest.ApplicationUtil.server;

public class MapActivity extends AppCompatActivity implements MySurfaceView.Drawable {

    private MapView mapView;
    private List<MapView.Object> data;
    public Controlor controlor;



    private void initMode(){
        if ("STA".equals(MODE)){
            client.getMsgCounter().setHandler(new MyHandler(this));
        }else if ("AccessPoint".equals(MODE)){
            server.getMsgCounter().setHandler(new MyHandler(this));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMode();
//        mapView = new MapView(this);
//        setContentView(mapView);
        setContentView(R.layout.activity_map);
        setTitle("智慧停车场");
        mapView = (MapView) findViewById(R.id.mapView);

        // 隐藏行动栏
//        getSupportActionBar().hide();

        // 设置手机方向垂直
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

//        mapView = (MapView) findViewById(R.id.mapView);
        // 从模型层获取数据在显示层显示
//        presenter.fetch();

        controlor = new Controlor(this);
        controlor.loadData(new AbstractDatabase.OnFetchListener() {
            @Override
            public void onFetch(List data) {
                MapActivity.this.data =  data;
            }
        });
        controlor.initCars();


    }

//    @Override
//    public void showLoading() {
//
//    }
//
//    @Override
//    public void showInfos(List infos) {
//        // 获取数据
//        data = infos;
//        // 开始更新surfaceView
////        mapView.startUpdate();
//
//    }

    @Override
    public void ondraw() {
        // 平移画布
        mapView.translateCanvas();

        // 伸缩画布
        mapView.telescopicCanvas();

        // 绘画动画
//        mapView.startAnimation();

        // 绘画布局
        mapView.drawAll(data);

        // 检查边界



        // 停止更新surfaceView
//        mapView.stopUpdate();

    }
}
