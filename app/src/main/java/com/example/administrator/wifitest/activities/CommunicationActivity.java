package com.example.administrator.wifitest.activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.wifitest.R;
import com.example.administrator.wifitest.socket.DataFormat;
import com.example.administrator.wifitest.socket.EchoClient;
import com.example.administrator.wifitest.socket.EchoServer;
import com.example.administrator.wifitest.socket.SysConvert;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import static com.example.administrator.wifitest.ApplicationUtil.MODE;
import static com.example.administrator.wifitest.ApplicationUtil.client;
import static com.example.administrator.wifitest.ApplicationUtil.server;


public class CommunicationActivity extends AppCompatActivity implements View.OnClickListener {
    // 通讯类型
//    public String MODE = "STA"; // 用户选择的模式
//    private EchoServer server; // 服务器
//    private EchoClient client; // 客户端
    // 控件类型
    public TextView showStatus; // 显示连接状态
    public static TextView showReceived; // 显示接收的数据
    private EditText edit; // 发送编辑文本
    public EditText editIP; // ip编辑文本
    public EditText editPort; // 端口编辑文本
    private PopupWindow IOpopupWindow;//显示IO格式设置的popupWindow
    private PopupWindow popupWindow;//显示列表的popupWindow
    // 命令列表类型
    private int commandCount = 0; // 指令数目
    private List<String> commadList = new ArrayList<>(); // 指令列表
    private EditText key; // 指令键编辑文本
    private EditText value; //指令值编辑文本
    // IO格式类型
    public DataFormat TFormat = new DataFormat(); // 发送数据格式
    public DataFormat RFormat = new DataFormat(); // 接收数据格式
    public String encodeMode = "GBK"; // 当前编码
    // 可选编码列表
    private List<String> encodeList = Arrays.asList(new String[]{"ASCII", "GBK", "ISO-8859-1", "UTF-8", "UTF-16", "GB2312", "Big5"});


    private ServerSocket ServerSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communication);
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//                .detectDiskReads().detectDiskWrites().detectNetwork()
//                .penaltyLog().build());

        // 控件初始化
        initView();
//        // 请求悬浮窗口权限
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SYSTEM_ALERT_WINDOW) == PackageManager.PERMISSION_DENIED) {
//            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
//        }


    }

    /**
     * 初始化模式
     */
    private void initMode(String host, int port) {
        // 用户选择模式为STA 模式
        if ("STA".equals(MODE)) {
            // 如果连接着服务器 关闭客户端
            if ("STA".equals(getTitle())) {
//                Toast.makeText(this, "You disconnect the server !", Toast.LENGTH_SHORT).show();
                if (client != null) client.close();

            }
            // 如果开启服务器 关闭服务器
            else if ("AccessPoint".equals(getTitle())) {
                Toast.makeText(this, "close server", Toast.LENGTH_SHORT).show();
                server.close();
            }

            client = EchoClient.newInstance(this, host, port);
            Toast.makeText(this, "Connecting to the server", Toast.LENGTH_SHORT).show();
            client.connectServer();
        } else if ("AccessPoint".equals(MODE)) {
            if (!getTitle().equals("AccessPoint")) {
                if (client != null) client.close();

                Toast.makeText(this, "open server", Toast.LENGTH_SHORT).show();
                server = new EchoServer(this, port);
                server.startListen();

            } else {
                Toast.makeText(this, "close server", Toast.LENGTH_SHORT).show();
                server.close();
            }

        }
    }

    /**
     * 控件初始化
     */
    private void initView() {
        showStatus = (TextView) findViewById(R.id.ShowStatus);
        showReceived = (TextView) findViewById(R.id.textView);
        edit = (EditText) findViewById(R.id.editText);
        editIP = (EditText) findViewById(R.id.editIP);
        editPort = (EditText) findViewById(R.id.editPort);
        Button button = (Button) findViewById(R.id.buttonSend);
        button.setOnClickListener(this);
        button = (Button) findViewById(R.id.buttonClear);
        button.setOnClickListener(this);
        button = (Button) findViewById(R.id.buttonConnect);
        button.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 连接服务端
            case R.id.buttonConnect:
                try {
                    MODE = "STA";
                    initMode(editIP.getText().toString(), Integer.parseInt(editPort.getText().toString().trim(), 10));
                } catch (NumberFormatException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                break;
            // 清除输入区文字
            case R.id.buttonClear:
                edit.setText("");
                Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
                break;
            // 发送信息
            case R.id.buttonSend:
                if (!getTitle().equals("STA") && !getTitle().equals("AccessPoint")) {
                    return;
                }
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if ("STA".equals(MODE)) {
                            if (TFormat.isHexFormat()) {
                                client.sendData(SysConvert.parseHex(edit.getText().toString()));

                            } else if (TFormat.isStringFormat()) {
                                client.sendData(edit.getText().toString());
                            }

                        } else if ("AccessPoint".equals(MODE)) {
                            if (TFormat.isHexFormat()) {
                                server.sendData(SysConvert.parseHex(edit.getText().toString()));

                            } else if (TFormat.isStringFormat()) {
                                server.sendData(edit.getText().toString());
                            }
                        }
                    }
                }.start();

                break;
        }

    }
    /***********************************************************************************************************/
    /**
     * 初始化菜单并加载
     * 设置监听事件
     */
    /***********************************************************************************************************/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.setting_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            /******************开启服务器******************/
            case R.id.openServer:
                MODE = "AccessPoint";
                try {
                    initMode(null, Integer.parseInt(editPort.getText().toString().trim(), 10));
                } catch (NumberFormatException e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


                break;
            /******************断开连接服务端******************/
            case R.id.disconnect:

                if ("STA".equals(getTitle())) {
                    Toast.makeText(this, "disconnect", Toast.LENGTH_SHORT).show();
                    client.close();
                }
                break;
            /******************清空接收区******************/
            case R.id.clearReceived:
                showReceived.setText("");
                Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
                break;

            /******************设定IO模式******************/
            case R.id.setFormat:
                setIOFormat();
                break;

            /******************设定字符编码******************/
            case R.id.setEncodeMode:
                showEncodeList();
                break;

            /******************打开命令列表******************/
            case R.id.openCommandTable:
                showCommandTable();
                break;
            /******************打开命令列表******************/
            case R.id.myIPAddress:
                edit.setText(getIPAddress(this));
                break;
            /******************打开地图******************/
            case R.id.openMap:

                if (!getTitle().equals("STA") && !getTitle().equals("AccessPoint")) {
                    Toast.makeText(this, "你没有连接网络设备,不能进入哦!", Toast.LENGTH_SHORT).show();
                } else {
                    startActivity(new Intent(this, MapActivity.class));
                }

                break;

        }
        return true;
    }

    /*****************************************************************
     * 显示设置IO格式的页面(PopupWindow)
     *****************************************************************/
    private void setIOFormat() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.io_option, null);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        IOpopupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, 400, true);
        IOpopupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        CompoundButton button = null;
        if (RFormat.isStringFormat()) {
            button = (RadioButton) contentView.findViewById(R.id.receivedString);
            button.setChecked(true);
        } else {
            button = (RadioButton) contentView.findViewById(R.id.receivedHex);
            button.setChecked(true);
        }

        if (TFormat.isStringFormat()) {
            button = (RadioButton) contentView.findViewById(R.id.sendString);
            button.setChecked(true);
        } else {
            button = (RadioButton) contentView.findViewById(R.id.sendHex);
            button.setChecked(true);
        }
        RadioGroup radioGroup = (RadioGroup) contentView.findViewById(R.id.receivedGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.receivedString:
                        RFormat.setFormat(DataFormat.STRING_FORMAT);
                        Toast.makeText(CommunicationActivity.this, "你选择接收数据是字符串IO格式", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.receivedHex:
                        RFormat.setFormat(DataFormat.HEX_FORMAT);
                        Toast.makeText(CommunicationActivity.this, "你选择接收数据是十六进制IO格式", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        radioGroup = (RadioGroup) contentView.findViewById(R.id.sendGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.sendString:
                        TFormat.setFormat(DataFormat.STRING_FORMAT);
                        Toast.makeText(CommunicationActivity.this, "你选择发送数据是字符串IO格式", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sendHex:
                        TFormat.setFormat(DataFormat.HEX_FORMAT);
                        Toast.makeText(CommunicationActivity.this, "你选择发送数据是十六进制IO格式", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }

    /*****************************************************************
     * 显示编码列表(PopupWindow)
     *****************************************************************/

    private void showEncodeList() {
        View contentView = LayoutInflater.from(this).inflate(R.layout.devices, null);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_main, null);
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, 1000, true);

        popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
        TextView textView = (TextView) contentView.findViewById(R.id.closeListWindow);
        textView.setText("当前字符编码是 " + encodeMode);

        ListView listView = (ListView) contentView.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(CommunicationActivity.this, R.layout.support_simple_spinner_dropdown_item, encodeList));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                encodeMode = encodeList.get(i);
                Toast.makeText(CommunicationActivity.this, "你选择" + encodeList.get(i) + "字符编码", Toast.LENGTH_SHORT).show();
                popupWindow.dismiss();
            }
        });
    }

    /*****************************************************************
     * 显示命令列表(PopupWindow)
     *****************************************************************/

    private void showCommandTable() {
        View contentView = LayoutInflater.from(CommunicationActivity.this).inflate(R.layout.devices, null);
        View rootView = LayoutInflater.from(CommunicationActivity.this).inflate(R.layout.activity_main, null);
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, 1000, true);

        popupWindow.showAtLocation(rootView, Gravity.TOP, 0, 230);
        TextView textView = (TextView) contentView.findViewById(R.id.closeListWindow);
        textView.setText("请选择要操控的命令");

        ListView listView = (ListView) contentView.findViewById(R.id.listView);
        SharedPreferences commandTable = getSharedPreferences("CommandTable", MODE_PRIVATE);
        commandCount = commandTable.getAll().size();

        commadList.clear();

        for (Map.Entry<String, ?> entry : commandTable.getAll().entrySet()) {
            commadList.add(entry.getKey() + " - " + entry.getValue());
        }
        commadList.add("添加操控命令");

        listView.setAdapter(new ArrayAdapter<String>(CommunicationActivity.this, R.layout.support_simple_spinner_dropdown_item, commadList));
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();
                String item = commadList.get(i);
                if (commandCount == 0 || "添加操控命令".equals(item)) {
                    popupWindow.dismiss();
                    showSetCommandTable(null, null);
                    return true;
                }

                showSetCommandTable(item.substring(0, item.indexOf(" - ")), item.substring((item.indexOf(" - ")) + " - ".length()));
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = commadList.get(i);
                if (commandCount == 0 || "添加操控命令".equals(item)) {
                    popupWindow.dismiss();
                    showSetCommandTable(null, null);
                    return;
                } else if (showStatus.getText().toString().equals("Not Connected")) {
                    Toast.makeText(CommunicationActivity.this, "请先连接网络!", Toast.LENGTH_SHORT).show();
                    return;
                }


                final String data = item.substring(item.indexOf(" - ") + " - ".length());
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        if ("STA".equals(MODE)) {
                            if (TFormat.isHexFormat()) {
                                client.sendData(SysConvert.parseHex(data));

                            } else if (TFormat.isStringFormat()) {
                                client.sendData(data);
                            }

                        } else if ("AccessPoint".equals(MODE)) {
                            if (TFormat.isHexFormat()) {
                                server.sendData(SysConvert.parseHex(data));

                            } else if (TFormat.isStringFormat()) {
                                server.sendData(data);
                            }
                        }
                    }
                }.start();


            }
        });
    }

    /*****************************************************************
     * 显示设置命令列表(PopupWindow)
     *****************************************************************/

    private void showSetCommandTable(String skey, String svalue) {
        View contentView = LayoutInflater.from(CommunicationActivity.this).inflate(R.layout.command_set, null);
        View rootView = LayoutInflater.from(CommunicationActivity.this).inflate(R.layout.activity_main, null);
        popupWindow = new PopupWindow(contentView, ActionBar.LayoutParams.WRAP_CONTENT, 1000, true);

        popupWindow.showAtLocation(rootView, Gravity.TOP, 0, 230);

        key = (EditText) contentView.findViewById(R.id.editCommandKey);
        if (skey != null) key.setText(skey);

        value = (EditText) contentView.findViewById(R.id.editCommandValue);
        if (svalue != null) value.setText(svalue);

        Button button = (Button) contentView.findViewById(R.id.buttonOK);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSharedPreferences("CommandTable", MODE_PRIVATE).edit().putString(key.getText().toString(), value.getText().toString()).apply();
                popupWindow.dismiss();
                showCommandTable();
            }
        });

        button = (Button) contentView.findViewById(R.id.buttonCancel);
        if (skey != null && svalue != null) {
            button.setText("delete");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Button) view).getText().toString().equals("delete")) {
                    getSharedPreferences("CommandTable", MODE_PRIVATE).edit().remove(key.getText().toString()).apply();
                }
                popupWindow.dismiss();
                showCommandTable();
            }
        });

    }

    // 清空接收区
    public void clearMessage(View v) {
        if (v.getId() == R.id.receivingArea) {
            Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
            showReceived.setText("");
        }

    }

    // 关闭popupWindow
    public void dismiss(View v) {
        if (popupWindow != null) popupWindow.dismiss();

    }

    // 恢复上次连接成功配置数据
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences shared = getSharedPreferences("Config", MODE_PRIVATE);
        editIP.setText(shared.getString("ip", ""));
        editIP.setSelection(editIP.length());
        editPort.setText(shared.getString("port", ""));
        editPort.setSelection(editPort.length());
    }

    // 活动销毁 回收socket资源
    @Override
    protected void onDestroy() {

        if ("STA".equals(MODE)) {
            if (client != null) client.close();
        } else if ("AccessPoint".equals(MODE)) {
            if (server != null) server.close();
        }
        super.onDestroy();
    }


    /**
     * 获取本机ip地址
     *
     * @param context
     * @return
     */
    public String getIPAddress(Context context) {

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}