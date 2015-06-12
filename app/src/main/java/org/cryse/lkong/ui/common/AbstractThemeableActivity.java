package org.cryse.lkong.ui.common;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.utils.ThemeEngine;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.inject.Inject;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

public abstract class AbstractThemeableActivity extends AbstractActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;
    @Inject
    ThemeEngine mThemeEngine;
    protected Handler mMainThreadHandler;
    private static final int DarkTheme = R.style.LKongDroidTheme_Dark;
    private static final int LightTheme = R.style.LKongDroidTheme_Light;
    private static final int DarkThemeTranslucent = R.style.LKongDroidTheme_Dark_Translucent;
    private static final int LightThemeTranslucent = R.style.LKongDroidTheme_Light_Translucent;
    private int mTheme;
    private boolean mIsOverrideStatusBarColor = true;
    private boolean mIsOverrideToolbarColor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = getAppTheme();
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
        mMainThreadHandler = new Handler();
        if(hasSwipeBackLayout()) {
            mHelper = new SwipeBackActivityHelper(this);
            mHelper.onActivityCreate();
            DisplayMetrics displaymetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels;
            getSwipeBackLayout().setEdgeSize(width);
            getSwipeBackLayout().setSensitivity(this, 0.5f);
            getSwipeBackLayout().setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            updateTaskDescription();
    }



    protected boolean hasSwipeBackLayout() {
        return true;
    }

    @Override
    protected void setUpToolbar(int toolbarLayoutId, int customToolbarShadowId) {
        super.setUpToolbar(toolbarLayoutId, customToolbarShadowId);
        if(getSupportActionBar() != null && mIsOverrideToolbarColor)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mThemeEngine.getPrimaryColor(this)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(mIsOverrideStatusBarColor)
                setStatusBarColor(mThemeEngine.getPrimaryDarkColor(this));
        }
    }

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(mThemeEngine.getPrimaryDarkColor(this));
        }
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(color);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(mHelper != null) mHelper.onPostCreate();
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
            return hasSwipeBackLayout() ? DarkThemeTranslucent : DarkTheme;
        else
            return hasSwipeBackLayout() ? LightThemeTranslucent : LightTheme;
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
        if(mHelper == null) return null;
        return mHelper.getSwipeBackLayout();
    }

    @Override
    public void setSwipeBackEnable(boolean enable) {
        if(getSwipeBackLayout() != null)
            getSwipeBackLayout().setEnableGesture(enable);
    }

    @Override
    public void scrollToFinishActivity() {
        Utils.convertActivityToTranslucent(this);
        if(getSwipeBackLayout() != null)
            getSwipeBackLayout().scrollToFinishActivity();
    }

    public void setIsOverrideStatusBarColor(boolean isOverrideStatusBarColor) {
        this.mIsOverrideStatusBarColor = isOverrideStatusBarColor;
    }

    public void setIsOverrideToolbarColor(boolean isOverrideToolbarColor) {
        this.mIsOverrideToolbarColor = isOverrideToolbarColor;
    }

    public ThemeEngine getThemeEngine() {
        return mThemeEngine;
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof ThemeColorChangedEvent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(mIsOverrideStatusBarColor)
                    setStatusBarColor(mThemeEngine.getPrimaryDarkColor(this));
                updateTaskDescription();
            }
            if(getSupportActionBar() != null)
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mThemeEngine.getPrimaryColor(this)));
        }
    }

    @Override
    public void onBackPressed() {
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            closeActivityWithTransition();
        }
    }

    public void closeActivityWithTransition() {
        if (mHelper != null && mHelper.getSwipeBackLayout() != null) {
            try {
                mHelper.getSwipeBackLayout().scrollToFinishActivity();
            } catch (Exception e) {
                finish();
            }
        } else {
            supportFinishAfterTransition();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void updateTaskDescription() {
        Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        setTaskDescription(
                new ActivityManager.TaskDescription(
                        getTitle().toString(),
                        iconBitmap,
                        mThemeEngine.getPrimaryColor(this)
                )
        );
        iconBitmap.recycle();
    }
}