package org.cryse.lkong.application.modules;

import android.app.Application;
import android.content.Context;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.navigation.AndroidNavigation;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    Application mApplicationContext;
    public ContextModule(Application application) {
        this.mApplicationContext = application;
    }

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

    @Provides
    public AndroidNavigation provideAndroidNavigation() {
        return new AndroidNavigation(mApplicationContext);
    }
}
