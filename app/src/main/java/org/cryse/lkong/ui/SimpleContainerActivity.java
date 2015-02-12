package org.cryse.lkong.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import org.cryse.lkong.R;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.utils.ColorUtils;

public abstract class SimpleContainerActivity extends AbstractThemeableActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_container);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        setUpFragment();
    }

    @Override
    protected abstract void injectThis();

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentActivityEnter(this, getLogTag());
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentActivityExit(this, getLogTag());
    }

    protected void setUpFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.container, newFragment());
        fragmentTransaction.commit();
    }

    protected abstract String getLogTag();
    protected abstract Fragment newFragment();
}
