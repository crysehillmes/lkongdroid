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

    public ContextModule(Application application, AndroidNavigation navigation) {
        this.mApplicationContext = application;
        this.mNavigation = navigation;
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
        return new RxEventBus();
    }

    @Provides
    public AndroidNavigation provideAndroidNavigation() {
        return mNavigation;
    }
}
