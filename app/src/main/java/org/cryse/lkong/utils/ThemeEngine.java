package org.cryse.lkong.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.cryse.lkong.R;
import org.cryse.utils.preference.BooleanPreference;
import org.cryse.utils.preference.IntegerPreference;
import org.cryse.utils.preference.PreferenceConstant;

public class ThemeEngine {
    Context mContext;
    IntegerPreference mPrefColorIndex;
    BooleanPreference mPrefNightMode;

    static final int[] mPrimaryColors = new int[]{
            R.color.md_red_500,
            R.color.md_pink_500,
            R.color.md_purple_500,
            R.color.md_deep_purple_500,
            R.color.md_indigo_500,
            R.color.md_blue_500,
            R.color.md_light_blue_500,
            R.color.md_cyan_500,
            R.color.md_teal_500,
            R.color.md_green_500,
            R.color.md_light_green_500,
            R.color.md_lime_500,
            R.color.md_yellow_500,
            R.color.md_amber_500,
            R.color.md_orange_500,
            R.color.md_deep_orange_500,
            R.color.md_brown_500,
            R.color.md_grey_500,
            R.color.md_blue_grey_500,
            android.R.color.background_dark
    };

    static final int[] mPrimaryDarkColors = new int[]{
            R.color.md_red_700,
            R.color.md_pink_700,
            R.color.md_purple_700,
            R.color.md_deep_purple_700,
            R.color.md_indigo_700,
            R.color.md_blue_700,
            R.color.md_light_blue_700,
            R.color.md_cyan_700,
            R.color.md_teal_700,
            R.color.md_green_700,
            R.color.md_light_green_700,
            R.color.md_lime_700,
            R.color.md_yellow_700,
            R.color.md_amber_700,
            R.color.md_orange_700,
            R.color.md_deep_orange_700,
            R.color.md_brown_700,
            R.color.md_grey_700,
            R.color.md_blue_grey_700,
            android.R.color.background_dark
    };

    public static ThemeEngine bind(Activity activity) {
        return new ThemeEngine(activity);
    }

    private ThemeEngine(Context context) {
        mContext = context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mPrefNightMode = new BooleanPreference(sharedPreferences, PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE, PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE_VALUE);
        mPrefColorIndex = new IntegerPreference(sharedPreferences, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR_VALUE);
    }

    public int getPrimaryColor() {
        int colorResId = mPrimaryColors[mPrefColorIndex.get()];
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mContext.getResources().getColor(colorResId, null);
        } else {
            return mContext.getResources().getColor(colorResId);
        }
    }

    public int getPrimaryDarkColor() {
        int colorResId = mPrimaryDarkColors[mPrefColorIndex.get()];
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return mContext.getResources().getColor(colorResId, null);
        } else {
            return mContext.getResources().getColor(colorResId);
        }
    }

    public boolean isNightMode() {
        return mPrefNightMode.get();
    }

    public void setNightMode(boolean isNightMode) {
        mPrefNightMode.set(isNightMode);
    }

    public int getPrimaryColorResId() {
        return mPrimaryColors[mPrefColorIndex.get()];
    }

    public int getPrimaryDarkColorResId() {
        return mPrimaryDarkColors[mPrefColorIndex.get()];
    }
}
