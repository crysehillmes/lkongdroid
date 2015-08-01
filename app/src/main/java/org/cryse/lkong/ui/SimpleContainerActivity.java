package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import org.cryse.lkong.R;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;

public abstract class SimpleContainerActivity extends AbstractThemeableActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        requestWindowFeatures();
        setContentView(R.layout.activity_simple_container);
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
        fragmentTransaction.replace(R.id.container, newFragment());
        fragmentTransaction.commit();
    }

    protected void requestWindowFeatures() {

    }

    protected abstract String getLogTag();
    protected abstract Fragment newFragment();
}
