package org.cryse.lkong.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkPolicyManager {
    public static int sNetworkType = ConnectivityManager.TYPE_MOBILE;
    public static boolean sIsWifiConnected = false;

    public static void checkNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            sNetworkType = activeNetwork.getType();
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                sIsWifiConnected = activeNetwork.isConnected() && activeNetwork.isAvailable();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                sIsWifiConnected = false;
            } else {
                // connected to the mobile provider's data plan
                sIsWifiConnected = false;
            }
        } else {
            // not connected to the internet
            sNetworkType = -1;
            sIsWifiConnected = false;
        }
    }

    public static boolean isWifiAvailable() {
        return sNetworkType == ConnectivityManager.TYPE_WIFI && sIsWifiConnected;
    }
}
