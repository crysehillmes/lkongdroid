package org.cryse.lkong.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.cryse.lkong.BuildConfig;
import org.cryse.lkong.application.component.Dagger_LKongPresenterComponent;
import org.cryse.lkong.application.component.Dagger_MainActivityComponent;
import org.cryse.lkong.application.component.Dagger_SendServiceComponet;
import org.cryse.lkong.application.component.Dagger_UserAccountComponent;
import org.cryse.lkong.application.component.LKongPresenterComponent;
import org.cryse.lkong.application.component.MainActivityComponent;
import org.cryse.lkong.application.component.SendServiceComponet;
import org.cryse.lkong.application.component.UserAccountComponent;
import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.application.modules.PreferenceModule;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.ui.navigation.AndroidNavigation;

import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class LKongApplication extends Application {
    private static final String TAG = LKongApplication.class.getName();
    private MainActivityComponent mainActivityComponent;
    private LKongPresenterComponent mLKongPresenterComponent;
    private UserAccountComponent mUserAccountComponent;
    private SendServiceComponet mSendServiceComponet;
    private AndroidNavigation mNavigation;
    private UserAccountManager mUserAccountManager;
    private RxEventBus mEventBus;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new CrashReportingTree());
        mNavigation = new AndroidNavigation(this);
        mUserAccountManager = new UserAccountManager();
        mEventBus = new RxEventBus();
        initComponents();
        mUserAccountComponent.inject(mUserAccountManager);
        mUserAccountManager.init();
    }

    private void initComponents() {
        mainActivityComponent = Dagger_MainActivityComponent
                .builder()
                .contextModule(new ContextModule(this, mNavigation, mEventBus))
                .preferenceModule(new PreferenceModule(this))
                .build();
        mLKongPresenterComponent = Dagger_LKongPresenterComponent
                .builder()
                .contextModule(new ContextModule(this, mNavigation, mEventBus))
                .lKongModule(new LKongModule())
                .preferenceModule(new PreferenceModule(this))
                .build();
        mUserAccountComponent = Dagger_UserAccountComponent
                .builder()
                .contextModule(new ContextModule(this, mNavigation, mEventBus))
                .lKongModule(new LKongModule())
                .preferenceModule(new PreferenceModule(this))
                .build();
        mSendServiceComponet = Dagger_SendServiceComponet
                .builder()
                .contextModule(new ContextModule(this, mNavigation, mEventBus))
                .lKongModule(new LKongModule())
                .build();
    }

    public static LKongApplication get(Context context) {
        return (LKongApplication) context.getApplicationContext();
    }

    public UserAccountManager getUserAccountManager() {
        return mUserAccountManager;
    }

    public MainActivityComponent mainActivityComponent() {
        return mainActivityComponent;
    }

    public LKongPresenterComponent lKongPresenterComponent() {
        return mLKongPresenterComponent;
    }

    public UserAccountComponent userAccountComponent() {
        return mUserAccountComponent;
    }

    public SendServiceComponet sendServiceComponet() {
        return mSendServiceComponet;
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.HollowTree {
        @Override public void i(String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.i((String) args[0], message);
            }
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.i((String) args[0], message, t);
            }
        }

        @Override
        public void e(String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.e((String) args[0], message);
            }
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
            if (BuildConfig.DEBUG) {
                Log.e((String) args[0], message, t);
            }
        }

        @Override
        public void d(String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.d((String) args[0], message);
            }
        }

        @Override
        public void d(Throwable t, String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.d((String) args[0], message, t);
            }
        }
    }
}
