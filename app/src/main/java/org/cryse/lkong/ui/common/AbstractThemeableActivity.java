package org.cryse.lkong.ui.common;

import android.os.Bundle;

import org.cryse.lkong.R;
import org.cryse.lkong.application.qualifier.PrefsNightMode;
import org.cryse.utils.preference.BooleanPreference;

import javax.inject.Inject;

public abstract class AbstractThemeableActivity extends AbstractActivity {
    @Inject
    @PrefsNightMode
    BooleanPreference mPrefNightMode;


    private int mDarkTheme = R.style.LKongDroidTheme_Dark;
    private int mLightTheme = R.style.LKongDroidTheme_Light;
    private int mTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = getAppTheme();
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void reload() {
        recreate();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mTheme != getAppTheme() || isNeedToReload()) {
            reload();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    protected boolean isNeedToReload() {
        return false;
    }

    public void reloadTheme() {
        reloadTheme(false);
    }

    public void reloadTheme(boolean forceReload) {
        int appTheme = getAppTheme();
        if(this.mTheme != appTheme || forceReload) {
            this.mTheme = appTheme;
            reload();
        }
    }

    public boolean isNightMode() {
        return mPrefNightMode.get();
    }

    public int getAppTheme() {
        if(isNightMode())
            return mDarkTheme;
        else
            return mLightTheme;
    }

    public void setNightMode(boolean isNightMode) {
        if(isNightMode != isNightMode()) {
            mPrefNightMode.set(isNightMode);
            //mTheme = getAppTheme();
            reloadTheme();
        }
    }
}