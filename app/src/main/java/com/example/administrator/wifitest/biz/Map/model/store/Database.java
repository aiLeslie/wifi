package com.example.administrator.wifitest.biz.Map.model.store;

import android.graphics.Color;

import com.example.administrator.wifitest.biz.Map.model.Info;
import com.example.administrator.wifitest.mvp.model.MyModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Database implements MyModel {
    private boolean isInit = false;
    private static Database me;
    private List<Info> data = new ArrayList<>();
    // 分区集合
    private HashSet<StoreField> sfs = new HashSet<>();
    // 服务员集合
    private HashSet<Waiter> ws = new HashSet<>();

    private Database() {
    }


    /**
     * 单例模式
     * 创建数据基地
     *
     * @param cst
     * @return
     */
    public static Database getInstance(Constructor cst) {
        if (me == null) {
            me = new Database();
            me.init();
            if (cst != null) cst.init(me);
            me.toList();

        }
        return me;

    }

    @Override
    public void loadData(OnLoadListener listener) {
        toList();
        listener.onComplete(data);
    }


    public interface Constructor {
        public abstract void init(Database database);
    }





    /**
     * 初始化方法
     * 为模型层加载数据
     */
    public void init() {
        StoreField field = new StoreField("家具", 100 * 150, 100 * 50, 100 * 300, 100 * 100);
        field.setColor(Color.parseColor("#8dd7ff"));
        this.addStoreField(field);

        field = new StoreField("化妆品", 100 * 385, 100 * 25, 100 * 150, 100 * 50);
        field.setColor(Color.parseColor("#ababab"));
        this.addStoreField(field);

        field = new StoreField("副食", 100 * 620, 100 * 50, 100 * 300, 100 * 100);
        field.setColor(Color.parseColor("#ff90af"));
        this.addStoreField(field);

        field = new StoreField("日用百货", 100 * 1030, 100 * 50, 100 * 500, 100 * 100);
        field.setColor(Color.parseColor("#ffeeb1"));
        this.addStoreField(field);

        field = new StoreField("果蔬", 100 * 1340, 100 * 50, 100 * 100, 100 * 100);
        field.setColor(Color.parseColor("#a7ffa5"));
        this.addStoreField(field);

        field = new StoreField("家具", 100 * 150, 100 * 170, 100 * 300, 100 * 100);
        field.setColor(Color.parseColor("#8dd7ff"));
        this.addStoreField(field);

        field = new StoreField("精品", 100 * 385, 100 * 195, 100 * 150, 100 * 50);
        field.setColor(Color.parseColor("#ffe148"));
        this.addStoreField(field);

    }

    private List<Info> toList() {
        data.clear();
        // 添加分区数据
        for (Info info : getStoreFieldSet()) {
            data.add(info);
        }
        // 添加服务员数据
        for (Info info : getWaiterSet()) {
            data.add(info);
        }
        return data;
    }


    public HashSet<StoreField> getStoreFieldSet() {
        return sfs;
    }

    public HashSet<Waiter> getWaiterSet() {
        return ws;
    }

    /**
     * 添加分区
     *
     * @param sf
     */
    public void addStoreField(StoreField sf) {
        sfs.add(sf);
    }

    /**
     * 移除分区
     *
     * @param sf
     * @return
     */
    public boolean removeStoreField(StoreField sf) {
        boolean is = sfs.remove(sf);
        return is;
    }

    /**
     * 添加服务员
     *
     * @param w
     */
    public void addWaiter(Waiter w) {
        ws.add(w);
    }


    /**
     * 移除服务员
     *
     * @param w
     * @return
     */
    public boolean removeWaiter(Waiter w) {
        boolean is = sfs.remove(w);
        return is;
    }


    public void onDestroy() {
        if (me == null) return;
        me.getWaiterSet().clear();
        me.getStoreFieldSet().clear();
        me = null;
    }


}
