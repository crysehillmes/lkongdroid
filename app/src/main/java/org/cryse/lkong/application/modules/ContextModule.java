package org.cryse.lkong.application.modules;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.application.qualifier.PrefsNightMode;
import org.cryse.lkong.application.qualifier.PrefsThemeColor;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.ThemeEngine;
import org.cryse.utils.preference.BooleanPreference;
import org.cryse.utils.preference.IntegerPreference;

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

    @Singleton
    @Provides
    public ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager)mApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Provides
    public AndroidNavigation provideAndroidNavigation() {
        return mNavigation;
    }

    @Provides
    public ThemeEngine provideThemeEngine(@PrefsNightMode BooleanPreference nightModePrefs, @PrefsThemeColor IntegerPreference themeColorPrefs) {
        return new ThemeEngine(nightModePrefs, themeColorPrefs);
    }
}
