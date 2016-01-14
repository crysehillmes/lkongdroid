package org.cryse.lkong.utils;

import android.content.Context;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.afollestad.appthemeengine.Config;

public class ThemeUtils {
    public static boolean isNightMode() {
        return false;
    }

    public static int textColorSecondary(Context context) {
        String key = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
        return Config.textColorSecondary(context, key);
    }

    public static int accentColor(Context context) {
        String key = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
        return Config.accentColor(context, key);
    }

    public static int makeColorDarken(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= factor; // value component
        return Color.HSVToColor(hsv);
    }

    public static int makeColorLighten(int color, float factor) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] = 0.2f + 0.8f * hsv[2];
        return Color.HSVToColor(hsv);
    }
}
