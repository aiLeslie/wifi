package com.example.administrator.wifitest.biz.handle;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.administrator.wifitest.socket.Task.Task;
import com.example.administrator.wifitest.socket.Task.TaskHandler;

import java.lang.ref.SoftReference;

public class MyHandler implements TaskHandler {
    private SoftReference<AppCompatActivity> activitySR;

    public MyHandler(AppCompatActivity activity) {
        activitySR = new SoftReference<>(activity);
    }

    public void setActivitySR(AppCompatActivity activity) {
        activitySR.clear();
        activitySR = new SoftReference<>(activity);
    }

    @Override
    public void handleTask(Task task) {
        switch (task.getId()){
            default:
                final StringBuffer buffer = new StringBuffer();
                for (int i : task.getImportanceData()){
                    buffer.append(i + "");
                }
                activitySR.get().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activitySR.get(),buffer.toString() , Toast.LENGTH_SHORT).show();

                    }
                });

                break;
        }
    }
}
