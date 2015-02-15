package org.cryse.lkong.ui.common;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;

import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.RxEventBus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractFragment extends Fragment {
    private List<Runnable> mDeferredUiOperations = new ArrayList<Runnable>();

    @Inject
    RxEventBus mEventBus;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mEventBus.toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onEvent);
    }

    @Override
    public void onResume() {
        super.onResume();
        analyticsTrackEnter();
    }

    @Override
    public void onPause() {
        super.onPause();
        analyticsTrackExit();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().invalidateOptionsMenu();
    }

    public Boolean isNightMode() {
        if(isAdded())
            return ((AbstractThemeableActivity)getActionBarActivity()).isNightMode();
        else
            return null;

    }

    protected abstract void analyticsTrackEnter();

    protected abstract void analyticsTrackExit();

    protected void onEvent(AbstractEvent event) {

    }
}
