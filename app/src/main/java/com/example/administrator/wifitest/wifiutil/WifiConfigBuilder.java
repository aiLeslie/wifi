package com.example.administrator.wifitest.wifiutil;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;

public class WifiConfigBuilder {
    public static final int SECURITY_NONE = 0;
    public static final int SECURITY_WEP = 1;
    public static final int SECURITY_PSK = 2; // WPA、WPA2、WPA_WPA2
//    public static final int SECURITY_EAP = 3;

    /**
     * 得到wifi加密方式
     *
     * @param result
     * @return
     */
    public static int getWifiCipherTypeByScanResult(ScanResult result) {
        if (result.capabilities.contains("WPA")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else {
            return SECURITY_NONE;
        }
    }

    /**
     * 创建wifi配置
     * @param ssid
     * @param password
     * @param wifiCipherType
     * @return
     */
    public static WifiConfiguration createConfig(String ssid, String password, int wifiCipherType) {

        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "\"" + ssid + "\"";
        config.hiddenSSID = true;
        switch (wifiCipherType) {
            case SECURITY_NONE:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;

            case SECURITY_WEP:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                int length = password.length();
                // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                if ((length == 10 || length == 26 || length == 58)
                        && password.matches("[0-9A-Fa-f]*")) {
                    config.wepKeys[0] = password;
                } else {
                    config.wepKeys[0] = '"' + password + '"';
                }
                break;

            case SECURITY_PSK:
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                if (password.length() != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;

//            case SECURITY_EAP:
//                //EAP暂时还没不完整
//                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
//                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
//                break;
            default:
                return null;
        }

        return config;
    }

}
