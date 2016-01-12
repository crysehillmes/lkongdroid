package org.cryse.lkong.ui.common;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import org.cryse.lkong.event.AbstractEvent;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.Utils;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityBase;
import me.imid.swipebacklayout.lib.app.SwipeBackActivityHelper;

public abstract class AbstractSwipeBackActivity extends AbstractActivity implements SwipeBackActivityBase {
    private SwipeBackActivityHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }



    protected boolean hasSwipeBackLayout() {
        return true;
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

    protected boolean isNeedToReload() {
        return false;
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

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
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
}