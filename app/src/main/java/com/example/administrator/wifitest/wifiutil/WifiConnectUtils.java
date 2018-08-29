package com.example.administrator.wifitest.wifiutil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Xiho on 2016/2/1.
 */
public class WifiConnectUtils {

    private WifiManager mWifiManager;
    private AppCompatActivity activity;
    private boolean isScanned = false;
    private List<ScanResult> scanResultsList = new ArrayList<>();
    private WifiInfo connectionInfo = null;
    private BroadcastReceiver receiver;

    // 构造函数
    public WifiConnectUtils(AppCompatActivity activity, WifiManager mWifiManager) {
        this.mWifiManager = mWifiManager;
        this.activity = activity;
    }

    // 打开wifi功能
    private boolean openWifi() {
        boolean enable = true;
        if (!mWifiManager.isWifiEnabled()) {
            enable = mWifiManager.setWifiEnabled(true);
        }
        return enable;
    }

    // 关闭wifi功能
    private boolean closeWifi() {
        boolean enable = true;
        if (mWifiManager.isWifiEnabled()) {
            enable = mWifiManager.setWifiEnabled(false);
        }
        return enable;
    }


    /**
     * 提供一个外部接口，传入要连接的无线网
     *
     * @param result
     * @param Password
     * @return
     */
    public boolean connect(final ScanResult result, final String Password) {
        if (!this.openWifi()) {
            return false;
        }
        // 开启wifi功能需要一段时间(我在手机上测试一般需要1-3秒左右)，所以要等到wifi
        // 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 为了避免程序一直while循环，让它睡个100毫秒在检测……
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }

        WifiConfiguration wifiConfig = WifiConfigBuilder.createConfig(result.SSID, Password, WifiConfigBuilder.getWifiCipherTypeByScanResult(result));
        if (wifiConfig == null) {
            return false;
        }
        // 加入配置
        int netID = mWifiManager.addNetwork(wifiConfig);
        //是否去连接wifi
        boolean isConnect = mWifiManager.enableNetwork(netID, false);
        //如果不能连接就删除当前网络配置
        if (!isConnect) mWifiManager.removeNetwork(netID);
        return isConnect;
    }

    // 查看以前是否也配置过这个网络
    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    /****************************************************************************************
     * 检查WiFi状态线程
     * 根据WiFi状态改变UI控件的属性
     * **************************************************************************************
     */
    class WifiStateThread extends Thread {//如果WiFi状态不与aswich状态一致,发送消息

        @Override
        public void run() {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        while (!activity.isFinishing()) {
                            /**
                             * 每隔十秒扫描一次wifi设备
                             */
                            if (mWifiManager.isWifiEnabled()) mWifiManager.startScan();

                            sleep(10 * 1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();


            while (!activity.isFinishing()) {
                /**
                 * 显示wifi连接状态
                 */
                connectionInfo = mWifiManager.getConnectionInfo();

                /**
                 * 如果扫描成功
                 * 比较两个新旧list
                 * 不相同 - 旧的list被赋值新的list
                 * 相同 - 保持原样
                 * 更新listView
                 */
                if (isScanned) {
                    List<ScanResult> newList = mWifiManager.getScanResults();
                    if (scanResultsList.size() != newList.size() || !scanResultsList.containsAll(newList)) {
                        scanResultsList.clear();
                        scanResultsList.addAll(newList);
                        Collections.sort(scanResultsList, new Comparator<ScanResult>() {
                            @Override
                            public int compare(ScanResult result, ScanResult t1) {
                                if (result.level > t1.level) {
                                    return -1;
                                } else if (result.level < t1.level) {
                                    return 1;
                                } else {
                                    return 0;
                                }
                            }
                        });


                    }
                    isScanned = false;
                }


                try {
                    Thread.sleep(1000 / 24);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 初始化网络连接接收器
     */
    private void initWiFiReceiver() {
        receiver = new WifiReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //WifiManager.WIFI_STATE_CHANGED_ACTION ——wifi开关变化广播
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);

        //WifiManager.SCAN_RESULTS_AVAILABLE_ACTION——热点扫描结果通知广播
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        //WifiManager.SUPPLICANT_STATE_CHANGED_ACTION——热点连接结果通知广播
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);

        // WifiManager.NETWORK_STATE_CHANGED_ACTION——网络状态变化广播（与上一广播协同完成连接过程通知）
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

        activity.registerReceiver(receiver, intentFilter);

    }

    /**
     * 广播接收器
     */
    class WifiReceiver extends BroadcastReceiver {
        /**
         * 涉及到的广播
         * <p>
         * WifiManager.WIFI_STATE_CHANGED_ACTION ——wifi开关变化广播
         * WifiManager.SCAN_RESULTS_AVAILABLE_ACTION——热点扫描结果通知广播
         * WifiManager.SUPPLICANT_STATE_CHANGED_ACTION——热点连接结果通知广播
         * WifiManager.NETWORK_STATE_CHANGED_ACTION——网络状态变化广播（与上一广播协同完成连接过程通知）
         */
        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
//                    Toast.makeText(context, "WIFI_STATE_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
                    int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                    switch (wifiState) {
                        case WifiManager.WIFI_STATE_DISABLED:
                            //wifi已关闭
                            Toast.makeText(context, "wifi已关闭", Toast.LENGTH_SHORT).show();
                            break;
                        case WifiManager.WIFI_STATE_ENABLED:
                            //wifi已打开
                            Toast.makeText(context, "wifi已打开", Toast.LENGTH_SHORT).show();

                            break;
                        case WifiManager.WIFI_STATE_ENABLING:
                            //wifi正在打开
                            Toast.makeText(context, "wifi正在打开", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            break;
                    }
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    if (info == null) return;

                    if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                        /**
                         * 连接已断开
                         */
                        Toast.makeText(context, "连接已断开", Toast.LENGTH_SHORT).show();
                    } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                        final WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        /**
                         * "已连接到网络:" + wifiInfo.getSSID()
                         */
                        Toast.makeText(context, "已连接到网络:" + wifiInfo.getSSID(), Toast.LENGTH_SHORT).show();


                    } else {
                        NetworkInfo.DetailedState state = info.getDetailedState();
                        if (state == state.CONNECTING) {
                            Toast.makeText(context, "连接中...", Toast.LENGTH_SHORT).show();

                        } else if (state == state.AUTHENTICATING) {
                            Toast.makeText(context, "正在验证身份信息...", Toast.LENGTH_SHORT).show();

                        } else if (state == state.OBTAINING_IPADDR) {
                            Toast.makeText(context, "正在获取IP地址...", Toast.LENGTH_SHORT).show();

                        } else if (state == state.FAILED) {
                            Toast.makeText(context, "连接失败...", Toast.LENGTH_SHORT).show();

                        }
                    }


                    break;
                case WifiManager.SCAN_RESULTS_AVAILABLE_ACTION:
                    isScanned = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);

//                    if (!intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
//                        Toast.makeText(context, "WiFi扫描失败", Toast.LENGTH_SHORT).show();
//
//                    }
//                    if (intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)) {
//                        Toast.makeText(context, "WiFi扫描更新成功", Toast.LENGTH_SHORT).show();
//
//                    }
//
                    break;
                case WifiManager.SUPPLICANT_STATE_CHANGED_ACTION:
//                    Toast.makeText(context, "SUPPLICANT_STATE_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
                    int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
                    if (WifiManager.ERROR_AUTHENTICATING == error) {
                        //密码错误,认证失败
                        Toast.makeText(context, "密码错误,请重新输入", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
//                    Toast.makeText(context, "NETWORK_STATE_CHANGED_ACTION", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

    }
}
