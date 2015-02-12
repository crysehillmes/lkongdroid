package org.cryse.lkong.utils;

public class ConnectionUtils {
    public static final int IMAGE_DOWNLOAD_ALWAYS = 0;
    public static final int IMAGE_DOWNLOAD_NEVER = 1;
    public static final int IMAGE_DOWNLOAD_ONLY_WIFI = 2;

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
}
