package org.cryse.lkong.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;

import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.ThemeUtils;
import org.cryse.lkong.utils.snackbar.ToastErrorConstant;
import org.cryse.lkong.utils.snackbar.SimpleSnackbarType;
import org.cryse.lkong.utils.snackbar.SnackbarSupport;
import org.cryse.lkong.utils.snackbar.SnackbarUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractFragment extends Fragment implements SnackbarSupport {
    private int mPrimaryColor;
    private int mPrimaryDarkColor;
    private int mAccentColor;
    protected String mATEKey;

    private List<Runnable> mDeferredUiOperations = new ArrayList<Runnable>();

    RxEventBus mEventBus = RxEventBus.getInstance();

    private Subscription mEventBusSubscription;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mATEKey = getATEKey();
        mEventBusSubscription = mEventBus.toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onEvent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //ATE.apply(this, mATEKey);
        ATE.postApply(getActivity(), mATEKey);
        mPrimaryColor = Config.primaryColor(getActivity(), mATEKey);
        mPrimaryDarkColor = Config.primaryColorDark(getActivity(), mATEKey);
        mAccentColor = Config.accentColor(getActivity(), mATEKey);
        getAppCompatActivity().invalidateOptionsMenu();
    }

    @Nullable
    protected final String getATEKey() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.checkAndUnsubscribe(mEventBusSubscription);
    }

    public AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity)getActivity();
    }

    public AbstractActivity getThemedActivity() {
        return (AbstractActivity)getActivity();
    }

    public AbstractSwipeBackActivity getSwipeBackActivity() {
        return (AbstractSwipeBackActivity)getActivity();
    }

    protected String getFragmentName() {
        return getClass().getCanonicalName();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().invalidateOptionsMenu();
    }

    public Boolean isNightMode() {
        if(isAdded())
            return getThemedActivity().isNightMode();
        else
            return null;
    }

    public void toggleNightMode() {
        if(getThemedActivity() != null)
            getThemedActivity().toggleNightMode();
    }

    protected int getPrimaryColor() {
        return mPrimaryColor;
    }

    protected int getPrimaryDarkColor() {
        return mPrimaryDarkColor;
    }

    protected int getAccentColor() {
        return mAccentColor;
    }

    protected abstract void analyticsTrackEnter();

    protected abstract void analyticsTrackExit();

    protected void onEvent(AbstractEvent event) {

    }

    protected View getSnackbarRootView() {
        return getView();
    }

    @Override
    public void showSnackbar(CharSequence text, SimpleSnackbarType type, Object... args) {
        SnackbarUtils.makeSimple(
                getSnackbarRootView(),
                text,
                type,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void showSnackbar(int errorCode, SimpleSnackbarType type, Object... args) {
        SnackbarUtils.makeSimple(
                getSnackbarRootView(),
                getString(ToastErrorConstant.errorCodeToStringRes(errorCode)),
                type,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    public RxEventBus getEventBus() {
        return mEventBus;
    }
}
