package org.cryse.lkong.ui.common;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import org.cryse.lkong.R;
import org.cryse.lkong.application.qualifier.PrefsNightMode;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.utils.ThemeEngine;
import org.cryse.utils.preference.BooleanPreference;

import javax.inject.Inject;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;
import rx.android.schedulers.AndroidSchedulers;

public abstract class AbstractThemeableActivity extends AbstractActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;
    @Inject
    ThemeEngine mThemeEngine;

    private int mDarkTheme = R.style.LKongDroidTheme_Dark;
    private int mLightTheme = R.style.LKongDroidTheme_Light;
    private int mTheme;
    private boolean mIsOverrideStatusBarColor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = getAppTheme();
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
        mHelper = new SwipeBackActivityHelper(this);
        mHelper.onActivityCreate();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
        getSwipeBackLayout().setEdgeSize(width);
        getSwipeBackLayout().setSensitivity(this, 0.5f);
        getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(mIsOverrideStatusBarColor)
                getWindow().setStatusBarColor(mThemeEngine.getPrimaryDarkColor(this));
        }
        if(getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mThemeEngine.getPrimaryColor(this)));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mHelper.onPostCreate();
        mEventBus.toObservable().subscribeOn(AndroidSchedulers.mainThread()).subscribe(this::onEvent);
    }

    @Override
    public View findViewById(int id) {
        View v = super.findViewById(id);
        if (v == null && mHelper != null)
            return mHelper.findViewById(id);
        return v;
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
        return mThemeEngine.isNightMode();
    }

    protected int getAppTheme() {
        if(isNightMode())
            return mDarkTheme;
        else
            return mLightTheme;
    }

    public void setNightMode(boolean isNightMode) {
        if(isNightMode != isNightMode()) {
            mThemeEngine.setNightMode(isNightMode);
            //mTheme = getAppTheme();
            reloadTheme();
        }
    }

    @Override
    public SwipeBackLayout getSwipeBackLayout() {
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        getSwipeBackLayout().scrollToFinishActivity();
    }

    public void setIsOverrideStatusBarColor(boolean isOverrideStatusBarColor) {
        this.mIsOverrideStatusBarColor = isOverrideStatusBarColor;
    }

    public ThemeEngine getThemeEngine() {
        return mThemeEngine;
    }
}