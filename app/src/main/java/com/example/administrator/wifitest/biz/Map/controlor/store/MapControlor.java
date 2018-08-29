package com.example.administrator.wifitest.biz.Map.controlor.store;

import com.example.administrator.wifitest.biz.Map.model.store.Database;
import com.example.administrator.wifitest.mvp.model.MyModel;
import com.example.administrator.wifitest.mvp.presenter.Presenter;
import com.example.administrator.wifitest.mvp.view.MyView;

import java.util.List;

public class MapControlor extends Presenter<MyView> {

    public MapControlor() {
        // 初始化模型层
        modelLayer = Database.getInstance(null);
    }



    @Override
    public void fetch() {
        if (mViewWf.get() != null){
            // 显示加载进度
//            mViewWf.get().showLoading();

            if (modelLayer != null){
                // 获取模型层数据(添加回调)
                modelLayer.loadData(new MyModel.OnLoadListener() {
                    @Override
                    public void onComplete(List datas) {
                        mViewWf.get().showInfos(datas);
                    }
                });
            }
        }
    }

}
