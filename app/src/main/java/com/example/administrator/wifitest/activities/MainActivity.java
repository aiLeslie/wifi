package com.example.administrator.wifitest.activities;

import android.Manifest;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.wifitest.R;
import com.example.administrator.wifitest.picture.PictureResource;
import com.example.administrator.wifitest.wifiutil.ScanResultAdapter;
import com.example.administrator.wifitest.wifiutil.WifiConfigBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.administrator.wifitest.wifiutil.WifiConfigBuilder.getWifiCipherTypeByScanResult;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<ScanResult> scanResultsList = new ArrayList<>();
    private WifiManager wifiManager;
    private ListView listView;
    private EditText editPassword;
    private BroadcastReceiver receiver;
    private WifiInfo connectionInfo = null;
    public static boolean isRun = false;
    private boolean isScanned = false;
    private boolean isOnLongClick = false;
    private boolean isActionUp = false;
    private Handler handler;
    private Vibrator vibrator;

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
                        while (isRun) {
                            /**
                             * 每隔十秒扫描一次wifi设备
                             */
                            if (wifiManager.isWifiEnabled()) wifiManager.startScan();

                            sleep(10 * 1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }.start();
            CompoundButton aswitch = (Switch) findViewById(R.id.aswitch);
            TextView textView = (TextView) findViewById(R.id.textViewConnectionWifi);

            while (isRun) {
                switch (wifiManager.getWifiState()) {
                    case WifiManager.WIFI_STATE_ENABLED:
                        if (aswitch.isChecked() == false) {
                            Message message = handler.obtainMessage(WifiManager.WIFI_STATE_ENABLED);
                            handler.sendMessage(message);

                        }
                        break;
                    case WifiManager.WIFI_STATE_DISABLED:
                        if (aswitch.isChecked() == true) {
                            Message message = handler.obtainMessage(WifiManager.WIFI_STATE_DISABLED);
                            handler.sendMessage(message);
                        }
                }
                /**
                 * 显示wifi连接状态
                 */
                connectionInfo = wifiManager.getConnectionInfo();
                /**如果Wifi已连接但是显示的不一致,发送消息*/
                if (((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED && !("连接的WLAN: " + connectionInfo.getSSID()).equals(textView.getText().toString())) {
                    handler.sendEmptyMessage(6);
                }
                /**如果Wifi已断开但是显示的不一致,发送消息*/
                if (((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED && !("你没有连接WiFi").equals(textView.getText().toString())) {
                    connectionInfo = null;
                    handler.sendEmptyMessage(5);

                }
                /**
                 * 如果扫描成功
                 * 比较两个新旧list
                 * 不相同 - 旧的list被赋值新的list
                 * 相同 - 保持原样
                 * 更新listView
                 */
                if (isScanned) {
                    List<ScanResult> newList = wifiManager.getScanResults();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                listView.setAdapter(new ScanResultAdapter(MainActivity.this, R.layout.wifi_item, scanResultsList));
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

    /****************************************************************************************
     * activity生命周期函数
     * **************************************************************************************
     */


    @Override
    protected void onStart() {
        super.onStart();
        isRun = true;
        new WifiStateThread().start();
        initWiFiReceiver();
    }


    @Override
    protected void onStop() {
        super.onStop();
        isRun = false;
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    /**
     * 处理器初始化
     */
    private void handlerInit() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {//根据消息的what改变aswitch状态
                CompoundButton aswitch = (Switch) findViewById(R.id.aswitch);
                TextView textView = (TextView) findViewById(R.id.textViewConnectionWifi);
                if (msg.what == WifiManager.WIFI_STATE_ENABLED) {
                    aswitch.setChecked(true);
                } else if (msg.what == WifiManager.WIFI_STATE_DISABLED) {
                    aswitch.setChecked(false);
                    ((ListView) findViewById(R.id.listView)).setAdapter(new ScanResultAdapter(MainActivity.this, R.layout.wifi_item, new ArrayList<ScanResult>()));
                } else if (msg.what == 5) {
                    textView.setText("你没有连接WiFi");
                } else if (msg.what == 6) {
                    WifiInfo connectionInfo = wifiManager.getConnectionInfo();
                    textView.setText("连接的WLAN: " + connectionInfo.getSSID());
                }


            }
        };
    }

    /**
     * 开关控件初始化
     */
    private void aswitchInit() {
        CompoundButton aswitch = (Switch) findViewById(R.id.aswitch);
        aswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (wifiManager.isWifiEnabled() == isChecked) return;
                wifiManager.setWifiEnabled(isChecked);
            }
        });

    }

    /**
     * 显示连接状态的文本控件初始化
     */
    private void textViewInit() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        TextView textView = (TextView) findViewById(R.id.textViewConnectionWifi);
        textView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
//                TextView textView = (TextView) view;
//                if (textView.getText().toString().equals("你没有连接WiFi")) {
////                    Toast.makeText(MainActivity.this, "你没有连接WiFi", Toast.LENGTH_SHORT).show();
//                    return true;
//                }
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        isActionUp = false;
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                isOnLongClick = false;
                                try {
                                    sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (!isActionUp) {
                                    vibrator.vibrate(50);
                                    isOnLongClick = true;
                                }


                            }
                        }.start();
                        break;
                    case MotionEvent.ACTION_UP:
                        isActionUp = true;
                        /**
                         * 判断显示wifi名称textView是否长按
                         * 如果是就断开当前网络
                         * 否则进入通讯的Activity
                         */
                        if (isOnLongClick) {

                            wifiManager.disableNetwork(connectionInfo.getNetworkId());
                            if (connectionInfo == null)
                                Toast.makeText(MainActivity.this, "断开连接", Toast.LENGTH_SHORT).show();

                        } else {

                        }
                        break;
                }
                return true;
            }
        });

    }

    /**
     * 扫描结果列表控件初始化
     */

    private void listViewInit() {

        listView = (ListView) findViewById(R.id.listView);

        listView.setAdapter(new ScanResultAdapter(MainActivity.this, R.layout.wifi_item, scanResultsList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                /**
                 * 获取保存的配置信息
                 */
                List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
                /****************************************************************************************************/
                /**
                 * 打印所有保存过配置信息
                 */
                /********************************************************************************************************************************************/
                Log.i(TAG, "/****************************************************************************************************/");
                for (WifiConfiguration existingConfig : existingConfigs) {
                    Log.i(TAG, existingConfig.toString());

                    Log.i(TAG, "/****************************************************************************************************/");

                }
                /********************************************************************************************************************************************/
                /**
                 * 判断某ssid的AP是否有保存过配置信息
                 */
                for (WifiConfiguration existingConfig : existingConfigs) {

                    if (existingConfig.SSID.equals("\"" + scanResultsList.get(position).SSID + "\"")) {
                        /**
                         * 如果现在连接着wifi,就断开当前网络
                         */
                        if (connectionInfo != null)
                            wifiManager.disableNetwork(connectionInfo.getNetworkId());

                        boolean isEnableNework = wifiManager.enableNetwork(existingConfig.networkId, false);
                        if (isEnableNework) {
                            Toast.makeText(MainActivity.this, "成功连接" + scanResultsList.get(position).SSID, Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(MainActivity.this, "删除当前网络配置,重新输入密码", Toast.LENGTH_SHORT).show();
                            wifiManager.removeNetwork(existingConfig.networkId);

                        }
                    }

                }

                showPasswordWindow(scanResultsList.get(position));
            }
        });

    }

    /**
     * 显示进入通讯Activity模式
     */
    private void showSelectModeWindow() {

        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.selectmode_window, null);
        contentView.setBackgroundColor(Color.parseColor("#eaeaea"));
        View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, 700, true);
        class TouchListener implements View.OnTouchListener {
            String mode = null;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    if (view.getId() == R.id.sat) {
                        mode = "STA";
                    } else if (view.getId() == R.id.ap) {
                        mode = "AccessPoint";
                    }
                    popupWindow.dismiss();
                    Intent intent = new Intent(MainActivity.this, CommunicationActivity.class);
                    intent.putExtra("MODE", mode);
                    intent.putExtra("IpAddress", connectionInfo.getIpAddress());

                    startActivity(intent);
                }


                return true;
            }
        }
        TouchListener touchListener = new TouchListener();

        TextView textView = contentView.findViewById(R.id.sat);
        textView.setText("STA模式");
        textView.setOnTouchListener(touchListener);

        textView = contentView.findViewById(R.id.ap);
        textView.setText("AccessPoint模式");
        textView.setOnTouchListener(touchListener);
        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);


    }
    /***********************************************************************************************************/
    /**
     * 初始化菜单并加载
     * 设置监听事件
     */
    /***********************************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_set_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /******************清空接收区******************/
            case R.id.goSocket:
//                showSelectModeWindow();
                Intent intent = new Intent(MainActivity.this, CommunicationActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        aswitchInit();
        listViewInit();
        textViewInit();
    }

    /**
     * 显示输入密码的悬浮窗口
     *
     * @param result
     */
    private void showPasswordWindow(final ScanResult result) {
        View contentView = LayoutInflater.from(MainActivity.this).inflate(R.layout.password_window, null);
        View rootView = LayoutInflater.from(MainActivity.this).inflate(R.layout.activity_main, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.MATCH_PARENT, 700, true);
        TextView name = (TextView) contentView.findViewById(R.id.name);
        editPassword = (EditText) contentView.findViewById(R.id.password);
        name.setText(result.SSID);//显示wifi名称
        Button button = (Button) contentView.findViewById(R.id.buttonOk);//设置确认按钮点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
                /**
                 * 如果现在连接着wifi,就断开当前网络
                 */
                if (connectionInfo != null)
                    wifiManager.disableNetwork(connectionInfo.getNetworkId());

                int netID = wifiManager.addNetwork(WifiConfigBuilder.createConfig(result.SSID, editPassword.getText().toString(), getWifiCipherTypeByScanResult(result)));//添加
                boolean isEnableNework = wifiManager.enableNetwork(netID, true);
                if (isEnableNework) {
                    Toast.makeText(MainActivity.this, "成功连接" + result.SSID, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "删除当前网络配置,重新输入密码", Toast.LENGTH_SHORT).show();
                    showPasswordWindow(result);
                }
            }
        });
        button = (Button) contentView.findViewById(R.id.buttonCancel);//设置取消按钮点击事件
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupWindow.dismiss();
            }
        });

        popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //申请权限
        requestPermission();

        // 开启打开动画
        PictureResource pr = new PictureResource(this, R.id.main);
        pr.openingAnimation();

        isRun = true;

        // 得到wifiManage的实例
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        /// 初始化控件
        initView();
        // 处理器初始化
        handlerInit();


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

        registerReceiver(receiver, intentFilter);

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


    /**
     * 申请权限
     */

    private void requestPermission() {

//    <!--修改网络状态的权限-->
//    <uses-permission android:name="android.permission.CHANGE_NETWORK_STATE"></uses-permission>
//    <!--修改WIFI状态的权限-->
//    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
//    <!--访问网络权限-->
//    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
//    <!--访问WIFI权限-->
//    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.CHANGE_NETWORK_STATE", "android.permission.CHANGE_WIFI_STATE", "android.permission.ACCESS_NETWORK_STATE", "android.permission.ACCESS_WIFI_STATE", Manifest.permission.ACCESS_FINE_LOCATION}, 0);
    }


}




