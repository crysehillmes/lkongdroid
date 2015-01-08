package org.cryse.lkong.application.modules;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.application.qualifier.PrefsNightMode;
import org.cryse.utils.preference.BooleanPreference;
import org.cryse.utils.preference.PreferenceConstant;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class PreferenceModule {
    Application mApplicationContext;
    public PreferenceModule(Application application) {
        this.mApplicationContext = application;
    }

    @Provides
    public SharedPreferences provideSharedPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);
        return prefs;
    }

    @Provides
    @PrefsNightMode
    BooleanPreference provideIsNightMode(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE, PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE_VALUE);
    }
}
