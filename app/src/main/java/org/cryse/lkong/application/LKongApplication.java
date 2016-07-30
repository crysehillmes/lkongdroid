package org.cryse.lkong.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.tencent.bugly.crashreport.CrashReport;

import org.cryse.lkong.BuildConfig;
import org.cryse.lkong.R;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.application.component.DaggerLKongPresenterComponent;
import org.cryse.lkong.application.component.DaggerSendServiceComponet;
import org.cryse.lkong.application.component.DaggerSimpleActivityComponent;
import org.cryse.lkong.application.component.DaggerUserAccountComponent;
import org.cryse.lkong.application.component.LKongPresenterComponent;
import org.cryse.lkong.application.component.SendServiceComponet;
import org.cryse.lkong.application.component.SimpleActivityComponent;
import org.cryse.lkong.application.component.UserAccountComponent;
import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.data.LKongDatabase2;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.UpgradeUtils;
import org.cryse.utils.preference.Prefs;

import javax.inject.Singleton;

import io.fabric.sdk.android.Fabric;

import timber.log.Timber;

@Singleton
public class LKongApplication extends Application {
    private static final String TAG = LKongApplication.class.getName();
    private SimpleActivityComponent simpleActivityComponent;
    private LKongPresenterComponent mLKongPresenterComponent;
    private UserAccountComponent mUserAccountComponent;
    private SendServiceComponet mSendServiceComponet;
    private UserAccountManager mUserAccountManager;

    /*@Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }*/

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new CrashReportingTree());
        Prefs.with(this).useDefault().init();
        AnalyticsUtils.init(this, getString(R.string.UMENG_APPKEY_VALUE));
        Fabric.with(this, new Crashlytics());
        if(BuildConfig.DEBUG) {
            CrashReport.initCrashReport(getApplicationContext(), getString(R.string.bugly_app_id), false);
        }
        if(BuildConfig.InAppUpdate) {
            /*UmengUpdateAgent.setAppkey(getString(R.string.UMENG_APPKEY_VALUE));
            UmengUpdateAgent.update(this);*/
        }
        UserAccountManager.startHandlerThread();
        UpgradeUtils.checkVersionCode(this);
        mUserAccountManager = new UserAccountManager();
        NetworkPolicyManager.checkNetworkState(this);
        initComponents();
        LKongDatabase2.init(this);
        userAccountComponent().inject(mUserAccountManager);
        mUserAccountManager.init();
        Log.e("ABC", "LKongApplication checkVersionCode() done.");
    }

    private void initComponents() {
        simpleActivityComponent = DaggerSimpleActivityComponent
                .builder()
                .contextModule(new ContextModule(this))
                .build();
        mLKongPresenterComponent = DaggerLKongPresenterComponent
                .builder()
                .contextModule(new ContextModule(this))
                .lKongModule(new LKongModule())
                .build();
        mUserAccountComponent = DaggerUserAccountComponent
                .builder()
                .contextModule(new ContextModule(this))
                .lKongModule(new LKongModule())
                .build();
        mSendServiceComponet = DaggerSendServiceComponet
                .builder()
                .contextModule(new ContextModule(this))
                .lKongModule(new LKongModule())
                .build();
    }

    public static LKongApplication get(Context context) {
        return (LKongApplication) context.getApplicationContext();
    }

    public UserAccountManager getUserAccountManager() {
        return mUserAccountManager;
    }

    public SimpleActivityComponent simpleActivityComponent() {
        return simpleActivityComponent;
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
    private static class CrashReportingTree extends Timber.DebugTree {
        @Override public void i(String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.i((String) args[0], message);
            }
            if(args.length >= 2)
                Crashlytics.log(args[0] + "<||>" + message + "<||>" + (String) args[1]);
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
            } else {
                Crashlytics.logException(t);
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
