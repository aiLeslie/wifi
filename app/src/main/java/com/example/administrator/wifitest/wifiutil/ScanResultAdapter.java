package com.example.administrator.wifitest.wifiutil;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.wifitest.R;
import com.example.administrator.wifitest.activities.DescActivity;

import java.util.List;

/**
 * Created by Administrator on 2018/1/6.
 */

public class ScanResultAdapter extends ArrayAdapter<ScanResult> {
    private int resourceId;
    private WifiManager wifiManage;

    public ScanResultAdapter(Context context, int textViewResourceid, List<ScanResult> obj) {
        super(context, textViewResourceid, obj);
        resourceId = textViewResourceid;

    }

    public ScanResultAdapter setWifiManage(WifiManager wifiManage) {
        this.wifiManage = wifiManage;
        return this;
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null, true);
        try {
//                /**
//                 * 特殊情况
//                 * 长度不为零list进行设配时,list因更新被清空
//                 * 如果带有数据的list长度为0,就不进行适配
//                 * 直接返回
//                 */
//                if (((BaseAdapter)this).getCount() == 0){
//                    return view;
//                }
            final ScanResult result = getItem(position);

            TextView textView = (TextView) view.findViewById(R.id.textWifiName);
            textView.setText(result.SSID);
            textView = (TextView) view.findViewById(R.id.textWifiLevel);
            int level = result.level;
            String signLevel = null;
            if (level > 0) {
                signLevel = "信号很差";
            } else if ( level >= -50) {
                signLevel = "信号很好";
            } else if (level >= -70) {
                signLevel = "信号较好";
            } else if (level >= -80) {
                signLevel = "信号一般";
            } else {
                signLevel = "信号很差";
            }
            textView.setText(signLevel);
            ImageView imageView = (ImageView) view.findViewById(R.id.desc);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), DescActivity.class);
                    intent.putExtra("desc", result);
                    getContext().startActivity(intent);
                }
            });

        } catch (java.lang.IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return view;


    }


}
