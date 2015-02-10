package org.cryse.lkong.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtils {
    public static final int IMAGE_DOWNLOAD_ALWAYS = 0;
    public static final int IMAGE_DOWNLOAD_NEVER = 1;
    public static final int IMAGE_DOWNLOAD_ONLY_WIFI = 2;

    public static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return wifiInfo.isConnected() && wifiInfo.isAvailable();
    }

    public static boolean shouldDownloadImage(int policy, boolean wifiAvailability) {
        switch (policy) {
            case IMAGE_DOWNLOAD_ONLY_WIFI:
                return wifiAvailability;
            case IMAGE_DOWNLOAD_NEVER:
                return false;
            case IMAGE_DOWNLOAD_ALWAYS:
            default:
                return true;
        }
    }

    public static boolean shouldDownloadImage(Context context, int policy) {
        boolean wifiAvailability = isWifiConnected(context);
        return shouldDownloadImage(policy, wifiAvailability);
    }
}
