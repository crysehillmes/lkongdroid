package org.cryse.lkong.application;

import android.app.Application;
import android.content.Context;

import org.cryse.lkong.application.component.Dagger_LKongPresenterComponent;
import org.cryse.lkong.application.component.Dagger_MainActivityComponent;
import org.cryse.lkong.application.component.LKongPresenterComponent;
import org.cryse.lkong.application.component.MainActivityComponent;
import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.application.modules.PreferenceModule;
import org.cryse.lkong.ui.navigation.AndroidNavigation;

import javax.inject.Singleton;

@Singleton
public class LKongApplication extends Application {
    private static final String TAG = LKongApplication.class.getCanonicalName();
    private MainActivityComponent mainActivityComponent;
    private LKongPresenterComponent lKongPresenterComponent;
    private AndroidNavigation mNavigation;
    @Override
    public void onCreate() {
        super.onCreate();
        mNavigation = new AndroidNavigation(this);
        initComponents();
    }

    private void initComponents() {
        mainActivityComponent = Dagger_MainActivityComponent
                .builder()
                .contextModule(new ContextModule(this, mNavigation))
                .preferenceModule(new PreferenceModule(this))
                .build();
        lKongPresenterComponent = Dagger_LKongPresenterComponent
                .builder()
                .contextModule(new ContextModule(this, mNavigation))
                .lKongModule(new LKongModule())
                .preferenceModule(new PreferenceModule(this))
                .build();
    }

    public static LKongApplication get(Context context) {
        return (LKongApplication) context.getApplicationContext();
    }

    public MainActivityComponent mainActivityComponent() {
        return mainActivityComponent;
    }

    public LKongPresenterComponent lKongPresenterComponent() {
        return lKongPresenterComponent;
    }
}
