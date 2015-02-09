package org.cryse.lkong.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.cryse.lkong.R;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.utils.preference.PreferenceConstant;

public class SettingsFragment extends PreferenceFragment {
    private OnConcisePreferenceChangedListener mOnConcisePreferenceChangedListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnConcisePreferenceChangedListener = new OnConcisePreferenceChangedListener();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_settings);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mOnConcisePreferenceChangedListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mOnConcisePreferenceChangedListener);
    }

    public class OnConcisePreferenceChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            AbstractThemeableActivity parentActivity = (AbstractThemeableActivity) getActivity();
            if (parentActivity != null) {
                if (key.compareTo(PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE) == 0) {
                    parentActivity.reloadTheme();
                }
            }
        }
    }
}
