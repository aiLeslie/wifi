package com.example.administrator.wifitest;

import android.app.Application;

import com.example.administrator.wifitest.socket.EchoClient;
import com.example.administrator.wifitest.socket.EchoServer;

public class ApplicationUtil extends Application {

    public static String MODE; // 用户选择的模式
    public static EchoServer server; // 服务端
    public static EchoClient client; // 客户端
}
