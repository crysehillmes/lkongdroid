package org.cryse.lkong.ui;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.webkit.WebView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.dialog.ColorChooserDialog;
import org.cryse.lkong.utils.ThemeEngine;
import org.cryse.utils.preference.IntegerPreference;
import org.cryse.utils.preference.PreferenceConstant;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.inject.Inject;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment {
    private static final String LOG_TAG = SettingsFragment.class.getName();
    private OnConcisePreferenceChangedListener mOnConcisePreferenceChangedListener = null;

    @Inject
    RxEventBus mEventBus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectThis();
        mOnConcisePreferenceChangedListener = new OnConcisePreferenceChangedListener();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_settings);

        setUpThemeColorPreference();
        setImagePolicySummary();
        setupVersionPrefs();
    }

    private void injectThis() {
        LKongApplication.get(getActivity()).simpleActivityComponent().inject(this);
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
            if(key.equals(PreferenceConstant.SHARED_PREFERENCE_IMAGE_DOWNLOAD_POLICY)) {
                setImagePolicySummary();
            }
        }
    }

    public void setImagePolicySummary() {
        String downloadPolicyString = getPreferenceManager().getSharedPreferences().getString(PreferenceConstant.SHARED_PREFERENCE_IMAGE_DOWNLOAD_POLICY, PreferenceConstant.SHARED_PREFERENCE_IMAGE_DOWNLOAD_POLICY_VALUE);
        int imageDownloadPolicy = Integer.valueOf(downloadPolicyString);
        String[] policyArray = getActivity().getResources().getStringArray(R.array.image_download_policy_arrays);
        Preference imageDownloadPolicyPrefs = findPreference(PreferenceConstant.SHARED_PREFERENCE_IMAGE_DOWNLOAD_POLICY);
        imageDownloadPolicyPrefs.setSummary(policyArray[imageDownloadPolicy]);
    }

    public void setupVersionPrefs() {
        Preference versionPrefs = findPreference("prefs_about_version");
        try {
            versionPrefs
                    .setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.d(e, e.getMessage(), LOG_TAG);
        }
        versionPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            private long exitTime = 0;
            private int times = 0;
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    exitTime = System.currentTimeMillis();
                    times = 0;
                } else {
                    times++;
                    if(times >= 4) {
                        try {
                            int versionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                            Toast.makeText(getActivity(), String.format("versionCode: %d", versionCode), Toast.LENGTH_SHORT).show();
                        } catch (PackageManager.NameNotFoundException e) {
                            Timber.d(e, e.getMessage(), LOG_TAG);
                        } finally {
                            times = 0;
                        }
                    }
                }
                return true;
            }
        });

        Preference changelogPref = (Preference) findPreference("prefs_about_changelog");
        changelogPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                        .title(R.string.settings_item_change_log_title)
                        .customView(R.layout.dialog_webview, false)
                        .positiveText(android.R.string.ok)
                        .build();
                WebView webView = (WebView) dialog.getCustomView().findViewById(R.id.webview);
                try {
                    String data = readChangelogFromAssets();
                    webView.loadDataWithBaseURL(null, data, "text/html", "UTF-8", null);
                } catch (Exception e) {
                    webView.loadUrl("file:///android_asset/changelog.html");
                }
                dialog.show();
                return true;
            }
        });
    }

    private String readChangelogFromAssets() throws Exception {
        StringBuilder buffer = new StringBuilder();
        InputStream inputStream = getActivity().getAssets().open("changelog.html");
        BufferedReader textReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String str;
        while ((str = textReader.readLine()) != null) {
            buffer.append(str);
        }
        textReader.close();
        return buffer.toString();
    }

    private void setUpThemeColorPreference() {
        Preference themeColorPreference = (Preference) findPreference("prefs_theme_color");
        IntegerPreference themeColorPrefsValue = new IntegerPreference(getPreferenceManager().getSharedPreferences(), PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR_VALUE);

        themeColorPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new ColorChooserDialog().show(getActivity(), themeColorPrefsValue.get(), new ColorChooserDialog.Callback() {
                    @Override
                    public void onColorSelection(int index, int color, int darker) {
                        themeColorPrefsValue.set(index);
                        ThemeEngine themeEngine = ((AbstractThemeableActivity)getActivity()).getThemeEngine();
                        mEventBus.sendEvent(new ThemeColorChangedEvent(themeEngine.getPrimaryColor(getActivity()), themeEngine.getPrimaryDarkColor(getActivity())));
                    }
                });
                return true;
            }
        });
    }
}
