package com.example.administrator.wifitest.mvp.view;

import java.util.List;

public  interface MyView {
    void showLoading();
    // 显示数据(使用回调的方式返回数据)
    void showInfos(List infos);
}
