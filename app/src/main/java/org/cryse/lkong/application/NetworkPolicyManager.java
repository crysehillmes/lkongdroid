package org.cryse.lkong.application;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.cryse.lkong.utils.ConnectionUtils;

public class NetworkPolicyManager {
    private Context mContext;
    private ConnectivityManager mConnectivityManager;
    public NetworkPolicyManager(Context context) {
        mContext = context;
        mConnectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isWifiConnected() {
        NetworkInfo wifiInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isConnected() && wifiInfo.isAvailable();
    }

    public boolean shouldDownloadImage(int policy) {

        return ConnectionUtils.shouldDownloadImage(policy, isWifiConnected());
    }
}
