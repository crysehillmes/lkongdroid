package org.cryse.lkong.application;

import android.app.Application;
import android.content.Context;

import org.cryse.lkong.application.component.Dagger_MainActivityComponent;
import org.cryse.lkong.application.component.MainActivityComponent;
import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.PreferenceModule;

public class LKongApplication extends Application {
    private static final String TAG = LKongApplication.class.getCanonicalName();
    private MainActivityComponent mainActivityComponent;
    @Override
    public void onCreate() {
        super.onCreate();
        initComponents();
    }

    private void initComponents() {
        mainActivityComponent = Dagger_MainActivityComponent
                .builder()
                .contextModule(new ContextModule(this))
                .preferenceModule(new PreferenceModule(this))
                .build();
    }

    public static LKongApplication get(Context context) {
        return (LKongApplication) context.getApplicationContext();
    }

    public MainActivityComponent mainActivityComponent() {
        return mainActivityComponent;
    }
}
