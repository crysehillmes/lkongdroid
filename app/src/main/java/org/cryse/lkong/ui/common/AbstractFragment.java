package org.cryse.lkong.ui.common;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFragment extends Fragment {
    private List<Runnable> mDeferredUiOperations = new ArrayList<Runnable>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected List<Runnable> getDeferredUiOperations() {
        return mDeferredUiOperations;
    }

    protected void tryExecuteDeferredUiOperations() {
        for (Runnable r : mDeferredUiOperations) {
            r.run();
        }
        mDeferredUiOperations.clear();
    }

    protected abstract void injectThis();

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public ActionBarActivity getActionBarActivity() {
        return (ActionBarActivity)getActivity();
    }

    public AbstractThemeableActivity getThemedActivity() {
        return (AbstractThemeableActivity)getActivity();
    }

    protected String getFragmentName() {
        return getClass().getCanonicalName();
    }

    public void setActionMode(ActionMode actionMode) {
        ((AbstractActivity)getActionBarActivity()).setActionMode(actionMode);
    }

    public ActionMode getActionMode() {
        return ((AbstractActivity)getActionBarActivity()).getActionMode();
    }

    public boolean isNightMode() {
        return ((AbstractThemeableActivity)getActionBarActivity()).isNightMode();
    }
}
