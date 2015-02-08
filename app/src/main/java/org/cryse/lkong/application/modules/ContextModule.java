package org.cryse.lkong.application.modules;

import android.app.Application;
import android.content.Context;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.ui.navigation.AndroidNavigation;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    Application mApplicationContext;
    AndroidNavigation mNavigation;
    RxEventBus mEventBus;
    public ContextModule(Application application, AndroidNavigation navigation, RxEventBus eventBus) {
        this.mApplicationContext = application;
        this.mNavigation = navigation;
        this.mEventBus = eventBus;
    }

    @Singleton
    @Provides
    @ApplicationContext
    public Context provideApplicationContext() {
        return mApplicationContext;
    }

    @Singleton
    @Provides
    public LKongApplication provideLKongApplication() {
        return (LKongApplication)mApplicationContext;
    }

    @Singleton
    @Provides
    public RxEventBus provideRxEventBus() {
        return mEventBus;
    }

    @Provides
    public AndroidNavigation provideAndroidNavigation() {
        return mNavigation;
    }
}
