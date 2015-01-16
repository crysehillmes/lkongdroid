package org.cryse.utils;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;

import java.util.Date;
import java.util.Random;

public class ColorUtils {
    public static int getColorFromAttr(Context context, int attr) {
        int[] textSizeAttr = new int[] { attr };
        TypedArray a = context.obtainStyledAttributes(textSizeAttr);
        int color = a.getColor(0, Color.RED);
        a.recycle();
        return color;
    }

    private static int getColorFromResources(Context context, int resId) {
        Resources resources = context.getResources();
        return resources.getColor(resId);
    }

    private static int[] getColorsFromResources(Context context, int[] resIds) {
        Resources resources = context.getResources();
        int count = resIds.length;
        int[] colors = new int[count];
        for(int i = 0; i < count; i++) {
            colors[i] = resources.getColor(resIds[i]);
        }
        return colors;
    }

    public static String colorToHexString(int color) {
        return String.format("#%08X", color);
    }
}
