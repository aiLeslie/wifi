package com.example.administrator.wifitest.serveices;

import android.accessibilityservice.AccessibilityService;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class MyAccessibilityService extends AccessibilityService {
    private static final String TAG = "MyAccessibilityService";
    // 控件文本过滤器
    private String[] filter = {"恭喜发财", "恭喜发财,大吉大利", "clear"};
    // 节点信息
    private AccessibilityNodeInfo rootNodeInfo;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 获得根节点信息
        rootNodeInfo = event.getSource();
        // 如果为空返回
        if (rootNodeInfo == null) return;
        // 开始点击
        startClick(rootNodeInfo);
//        int eventType = event.getEventType();
//        switch (eventType) {
//            case AccessibilityEvent.TYPE_VIEW_CLICKED:
//                // 捕获到点击事件
//                Log.i(TAG, "capture click event!");
//                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
//                if (nodeInfo != null) {
//                    // 查找text为Test!的控件
//                    List<AccessibilityNodeInfo> button = nodeInfo.findAccessibilityNodeInfosByText("Test!");
//                    nodeInfo.recycle();
//                    for (AccessibilityNodeInfo item : button) {
//                        Log.i(TAG, "long-click button!");
//                        // 执行长按操作
//                        item.performAction(AccessibilityNodeInfo.ACTION_LONG_CLICK);
//                    }
//                }
//                break;
//            default:
//                break;
//        }
    }

    private boolean startClick(AccessibilityNodeInfo rootNodeInfo) {
        // 获得符合过滤器的节点信息
        List<AccessibilityNodeInfo> list = findByText(rootNodeInfo);
        // 如果为空返回
        if (list == null) return false;
        // 获得list中最后一个节点
        AccessibilityNodeInfo nodeInfo = list.get(list.size() - 1);
        // 如果节点信息不为空
        if (nodeInfo != null) {
            if ("已拆开".equals(nodeInfo.getText())) return false;
            // 如果可以点击
            if (nodeInfo.isClickable()) {
                // 点击该控件
                boolean isClick = nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                return isClick;

            }


        }
        return false;

    }

    private List<AccessibilityNodeInfo> findByText(AccessibilityNodeInfo rootNodeInfo) {
        // 遍历过滤器字符串数组
        for (String name : filter) {
            // 找出含有关键词的节点信息
            List<AccessibilityNodeInfo> list = rootNodeInfo.findAccessibilityNodeInfosByText(name);
            if (list != null && !list.isEmpty()) {
                return list;
            }
        }
        return null;
    }

    @Override
    public void onInterrupt() {
        Log.i(TAG, "onInterrupt");
    }
}
