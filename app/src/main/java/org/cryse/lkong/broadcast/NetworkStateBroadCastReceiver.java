package org.cryse.lkong.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.cryse.lkong.application.NetworkPolicyManager;

public class NetworkStateBroadCastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            NetworkPolicyManager.sNetworkType = activeNetwork.getType();
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                NetworkPolicyManager.sIsWifiConnected = activeNetwork.isConnected() && activeNetwork.isAvailable();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                NetworkPolicyManager.sIsWifiConnected = false;
            } else {
                // connected to the mobile provider's data plan
                NetworkPolicyManager.sIsWifiConnected = false;
            }
        } else {
            // not connected to the internet
            NetworkPolicyManager.sNetworkType = -1;
            NetworkPolicyManager.sIsWifiConnected = false;
        }
    }
}
