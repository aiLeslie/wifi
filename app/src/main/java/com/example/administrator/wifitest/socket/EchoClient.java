package com.example.administrator.wifitest.socket;

import android.widget.Toast;

import com.example.administrator.wifitest.ApplicationUtil;
import com.example.administrator.wifitest.R;
import com.example.administrator.wifitest.activities.CommunicationActivity;
import com.example.administrator.wifitest.socket.Task.MsgCounter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.LinkedList;

public class EchoClient implements Serializable {
    private static EchoClient nowClient = new EchoClient(null, null, -1);
    private String mHost;
    private int mPort;
    private Socket mSocket;
    private InputStream in;
    private OutputStream out;
    public ReceiveThread receiveThread;
    private CommunicationActivity activity;
    private MsgCounter msgCounter;
    private LinkedList<Integer> values = new LinkedList<>(); // 16进制数值集合

    private EchoClient(CommunicationActivity activity, String host, int port) {
        // 创建 socket 并连接服务器
        this.activity = activity;
        mHost = host;
        mPort = port;
    }

    public EchoClient(String host, int port) {
        // 创建 socket 并连接服务器
        mHost = host;
        mPort = port;
    }

    public static EchoClient newInstance(CommunicationActivity activity, String host, int port) {
        if (!nowClient.isConnect()) {
            return new EchoClient(activity, host, port);
        }
        return nowClient;

    }

    /**
     * 获取信息计数器的实例
     */
    public MsgCounter getMsgCounter() {
        return msgCounter;
    }

    public void connectServer() {
        synchronized (nowClient) {
            if (nowClient != null && nowClient.isConnect()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "connected server", Toast.LENGTH_SHORT).show();


                    }
                });
                return;
            }
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    try {
                        mSocket = new Socket(mHost, mPort);


                        // 和服务端进行通信
                        in = mSocket.getInputStream();
                        out = mSocket.getOutputStream();
                        receiveThread = new ReceiveThread();
                        receiveThread.start();

                        // 实例化信息计数器
                        msgCounter = new MsgCounter();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, "Connection successful", Toast.LENGTH_SHORT).show();
                                activity.showStatus.setText(mSocket.toString());

                                activity.setTitle("STA");
                                ApplicationUtil.MODE = "STA";

                                activity.getSharedPreferences("Config", activity.MODE_PRIVATE).edit().putString("ip", activity.editIP.getText().toString()).putString("port", activity.editPort.getText().toString()).apply();

                            }
                        });
                        nowClient = EchoClient.this;

                    } catch (final ConnectException e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });


                    } catch (final IOException e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

                    }
                }
            }.start();
        }

    }

    /**
     * 向客户发送数据
     *
     * @param content
     */
    public void sendData(byte[] content) {
        if (out == null)
            return;
        try {
            out.write(content, 0, content.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(int[] content) {
        if (out == null)
            return;
        try {
            for (int i = 0; i < content.length; i++)
                out.write(content[i]);
            System.out.println("客户端发送数据 >>>");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String content) {
        if (out == null)
            return;
        try {

            out.write(content.getBytes(activity.encodeMode));


            System.out.println("客户端发送数据 >>>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 接收客户发送数据
     */
    private int value = -1;
    private byte[] bytes;

    private void receiveData() {
        if (in == null)
            return;
        try {
            if (in.available() == 0) {
                return;
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        try {


            while (in.available() != 0) {
                if (activity.RFormat.isHexFormat()) {

                    value = in.read();

                    // 装载信息
                    msgCounter.add(value);

                    // 16进制数值集合进队
                    values.offer(value);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 如果数值集合第一个元素不为空继续循环
                            while (values.peekFirst() != null) {
                                activity.showReceived.setText(activity.showReceived.getText().toString() + " " + Integer.toHexString(values.pop()));
                            }

                        }
                    });


                } else if (activity.RFormat.isStringFormat()) {
                    bytes = new byte[in.available()];
                    in.read(bytes, 0, bytes.length);

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                activity.showReceived.setText(activity.showReceived.getText().toString() + " " + new String(bytes, activity.encodeMode));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }


            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class ReceiveThread extends Thread {
        private boolean aswitch = true;

        public boolean isAswitch() {
            return aswitch;
        }

        public void setAswitch(boolean aswitch) {
            this.aswitch = aswitch;
        }

        @Override
        public void run() {
            super.run();

            while (aswitch) {
                System.out.flush();

                receiveData();
            }
        }
    }

    public boolean isConnect() {
        if (mSocket != null && mSocket.isClosed() || mSocket == null) {
            return false;
        } else {
            return true;
        }

    }

    public void close() {
        // 关闭服务度接收数据线程
        if (receiveThread != null)
            receiveThread.setAswitch(false);
        // 关闭socket资源
        try {

            if (mSocket != null) {

                if (!mSocket.isClosed())
                    mSocket.close();
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.setTitle(R.string.app_name);
                    activity.showStatus.setText("Not Connected");

                }
            });
            System.out.println("客户端关闭 !!!");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
