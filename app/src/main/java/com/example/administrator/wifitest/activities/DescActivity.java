package com.example.administrator.wifitest.activities;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.wifitest.R;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class DescActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desc);
        try{
            StringBuffer stringBuffer = new StringBuffer();
            ScanResult result = (ScanResult)(getIntent().getParcelableExtra("desc"));
            String separator = System.getProperty("line.separator");
            stringBuffer.append("BSSID: " + result.BSSID);
            stringBuffer.append(separator);
            stringBuffer.append("SSID: " + result.SSID);
            stringBuffer.append(separator);
            stringBuffer.append("level: " + result.level);
            stringBuffer.append(separator);
            stringBuffer.append("capabilities: " + result.capabilities);
            stringBuffer.append(separator);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                stringBuffer.append("venueName: " + result.venueName);
//                stringBuffer.append(separator);
//
//                stringBuffer.append("isPasspointNetwork: " + result.isPasspointNetwork());
//                stringBuffer.append(separator);
//            }


            TextView textView = (TextView) findViewById(R.id.textView);
//            textView.setText(stringBuffer);
            textView.setText(result.toString().replaceAll(",","\n"));
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
