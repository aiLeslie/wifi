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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EchoServer implements Serializable {
    public final static String MY_IPAddRESS = "192.168.10.193";// 本机的ip地址
    private ServerSocket mServerSocket;
    public boolean isListen = true;
    private List<Socket> sockets = new ArrayList<>();
    private InputStream in;
    private OutputStream out;
    private ReceiveThread receiveThread;
    private LinkedList<Integer> msg = new LinkedList<>();
    private int localPort = -1;
    private CommunicationActivity activity;
    private MsgCounter msgCounter;
    private LinkedList<Integer> values = new LinkedList<>(); // 16进制数值集合

    public EchoServer(CommunicationActivity activity, int port) {
        // 创建 socket 并连接服务器
        this.activity = activity;
        localPort = port;
    }

    /**
     * 获取信息计数器的实例
     */
    public MsgCounter getMsgCounter() {
        return msgCounter;
    }

    /**
     * 监听客户的连接
     */
    public void startListen() {

        new Thread() {
            @Override
            public void run() {
                super.run();
                // 1. 创建一个 ServerSocket 并监听端口 port
                try {
                    mServerSocket = new ServerSocket(localPort);

                    System.out.println("服务器初始化");
                    // 开启接收数据线程
                    receiveThread = new ReceiveThread();
                    receiveThread.start();
                    // 实例化信息计数器
                    msgCounter = new MsgCounter();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.setTitle("AccessPoint");
                            ApplicationUtil.MODE = "AccessPoint";

                        }
                    });
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                // 2. 开始接受客户连接
                isListen = true;
                System.out.println("正在监听客户连接 >>>");
                while (isListen) {
                    try {
                        if (mServerSocket == null) {
                            System.out.println("服务器初始化失败");
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity, "服务器初始化失败", Toast.LENGTH_SHORT).show();
                                }
                            });
                            return;
                        }
                        Socket client = mServerSocket.accept();
                        /**
                         *  遍历客户列表
                         *  如果客户已经连接 移除配置
                         *  否则 不操作
                         */
                        Iterator<Socket> iterator = sockets.iterator();
                        while (iterator.hasNext()) {
                            Socket action = iterator.next();
                            if (action.getInetAddress().equals(client.getInetAddress())) {
                                sockets.remove(action);
                                System.out.println("检测客户重新连接移除配置");
                                System.out.println(">>> remove " + action.toString());
                                break;

                            }
                        }
                        handleClient(client);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    if (mServerSocket != null) {
                        mServerSocket.close();
                        mServerSocket = null;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }

    /**
     * 处理发出请求连接客户
     *
     * @param socket
     */
    private void handleClient(final Socket socket) {
        if (socket == null)
            return;

        // 3. 使用 socket 进行通信 ...

        // 将socket加入队列中
        sockets.add(socket);
        System.out.println();
        System.out.println("<<< " + socket.toString());

        try {
            if (isListen) {
                in = socket.getInputStream();
                out = socket.getOutputStream();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "Connection successful", Toast.LENGTH_SHORT).show();
                activity.showStatus.setText(socket.toString());


            }
        });

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
            System.out.println("服务端端数据 >>>");
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
            System.out.println("服务端发送数据 >>>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendData(String content) {
        if (out == null)
            return;
        try {
            for (int i = 0; i < sockets.size(); i++) {

                out.write(content.getBytes(activity.encodeMode));

            }

            System.out.println("服务端发送数据 >>>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收客户发送数据
     */
    private int value = -1;
    private byte[] bytes;

    private void receiveData(Socket socket) {
        if (socket == null) {
            return;
        } else if (socket.isClosed()) {
            System.out.println(socket.toString() + " socket close!");
            sockets.remove(socket);
            return;
        }

        try {
            // 获得输入流
            in = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (in == null) {
            return;
        }

        try {
            // 输入流中没有数据
            if (in.available() == 0) {
                return;
            }

            PrintStyle.printLine();
            System.out.println("服务端接收数据 <<<");
            System.out.println(socket.toString());

            if (activity.RFormat.isHexFormat()) {
                value = -1;
                while (in.available() > 0) {
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

                    msg.offer(value);
                    if (msg.size() % 10 == 0)
                        System.out.println();
                    System.out.print(value);
                    System.out.print(" ");

                    if (in.available() == 0)
                        System.out.println();
                }
            } else if (activity.RFormat.isStringFormat()) {
                bytes = new byte[in.available()];
                in.read(bytes, 0, bytes.length);
                System.out.println(new String(bytes, activity.encodeMode));
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

            PrintStyle.printLine();
            System.out.println();

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
            aswitch = true;
            while (aswitch) {
                System.out.flush();
                try {
                    for (int i = 0; i < sockets.size(); i++) {
                        if (!isListen)
                            break;
                        receiveData(sockets.get(i));

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void close() {
        // 关闭服务器端的监听
        isListen = false;
        /**
         * 开启一个线程连接服务端 让serverSocket.accept()返回值,跳出阻塞 关闭服务度接收数据线程 关闭socket资源
         * 关闭serverSocket
         */
        new Thread() {
            @SuppressWarnings("resource")
            @Override
            public void run() {
                super.run();
                try {
                    new Socket("localhost", localPort);
                    sleep(1000);
                } catch (ConnectException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 关闭服务度接收数据线程
                if (receiveThread != null)
                    receiveThread.setAswitch(false);
                // 关闭socket资源
                for (Socket action : sockets) {
                    try {
                        if (action != null) {
//                            if (!action.isInputShutdown()) action.shutdownInput();
//                            if (!action.isOutputShutdown()) action.shutdownOutput();
                            if (!action.isClosed()) action.close();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                ;

                try {
                    if (mServerSocket != null)
                        mServerSocket.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        activity.setTitle(R.string.app_name);
                        activity.showStatus.setText("Not Connected");

                    }
                });
                System.out.println("服务器关闭 !!!");

            }
        }.start();

    }

}




