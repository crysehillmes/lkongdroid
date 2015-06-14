package org.cryse.lkong.utils;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

public class AnalyticsUtils {
    public static void init(String appKey) {
        AnalyticsConfig.setAppkey(appKey);
        MobclickAgent.openActivityDurationTrack(false);
        AnalyticsConfig.enableEncrypt(true);
    }

    public static void trackActivityEnter(Context context, Object... args) {
        MobclickAgent.onPageStart((String)args[0]); //统计页面
        MobclickAgent.onResume(context);
    }

    public static void trackActivityExit(Context context, Object... args) {
        MobclickAgent.onPageEnd((String)args[0]);
        MobclickAgent.onPause(context);
    }

    public static void trackFragmentActivityEnter(Context context, Object... args) {
        MobclickAgent.onResume(context);
    }

    public static void trackFragmentActivityExit(Context context, Object... args) {
        MobclickAgent.onPause(context);
    }

    public static void trackFragmentEnter(Fragment fragment, Object... args) {
        if(args.length > 0)
            MobclickAgent.onPageStart((String)args[0]);
    }

    public static void trackFragmentExit(Fragment fragment, Object... args) {
        if(args.length > 0)
            MobclickAgent.onPageEnd((String)args[0]);
    }
}