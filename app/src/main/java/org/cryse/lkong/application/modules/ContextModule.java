package org.cryse.lkong.application.modules;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.event.RxEventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {
    Application mApplicationContext;
    public ContextModule(Application application) {
        this.mApplicationContext = application;
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
    public ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager)mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
