package org.cryse.lkong.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.changelog.ChangeLogUtils;
import org.cryse.lkong.R;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.sync.SyncUtils;
import org.cryse.lkong.ui.common.AbstractSwipeBackActivity;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.lkong.utils.DonateUtils;

import javax.inject.Inject;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment {
    private static final String LOG_TAG = SettingsFragment.class.getName();
    private OnConcisePreferenceChangedListener mOnConcisePreferenceChangedListener = null;
    AppNavigation mNavigation = new AppNavigation();
    RxEventBus mEventBus = RxEventBus.getInstance();
    protected String mATEKey;

    @Inject
    UserAccountManager mUserAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mATEKey = getATEKey();
        injectThis();
        mOnConcisePreferenceChangedListener = new OnConcisePreferenceChangedListener();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_settings);

        setImagePolicySummary();
        setAvatarPolicySummary();
        setupVersionPrefs();
        setupFeedbackPreference();
        setupThemePreference();
        setupDonationPreference();

        Preference syncPrefs = findPreference("prefs_goto_account_settings");
        syncPrefs.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
            //intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[] {"org.cryse.lkong"});
            intent.putExtra(Settings.EXTRA_AUTHORITIES, new String[] {SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE, SyncUtils.SYNC_AUTHORITY_FOLLOW_STATUS});
            startActivity(intent);
            return true;
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ATE.apply(this, mATEKey);
    }

    @Nullable
    protected final String getATEKey() {
        return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
    }

    private void injectThis() {
        LKongApplication.get(getActivity()).simpleActivityComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshCheckNoticeAutoSync();
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
            AbstractSwipeBackActivity parentActivity = (AbstractSwipeBackActivity) getActivity();
            switch (key) {
                case PreferenceConstant.SHARED_PREFERENCE_IMAGE_DOWNLOAD_POLICY:
                    setImagePolicySummary();
                    break;
                case PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY:
                    setAvatarPolicySummary();
                    break;
                case PreferenceConstant.SHARED_PREFERENCE_ENABLE_BACKGROUND_NOTIFICATION:
                    Boolean newIsAutoSync = sharedPreferences.getBoolean(key, true);
                    boolean isCheckNoticeAutoSync2 = ContentResolver.getSyncAutomatically(
                            mUserAccountManager.getCurrentUserAccount().getAccount(),
                            SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE
                    );
                    if (newIsAutoSync != isCheckNoticeAutoSync2) {
                        ContentResolver.setSyncAutomatically(
                                mUserAccountManager.getCurrentUserAccount().getAccount(),
                                SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE, newIsAutoSync);

                    }
                    break;
                case PreferenceConstant.SHARED_PREFERENCE_CHECK_NOTIFICATION_DURATION:
                    String newDurationString = sharedPreferences.getString(key, PreferenceConstant.SHARED_PREFERENCE_CHECK_NOTIFICATION_DURATION_VALUE);
                    int newDuration = Integer.valueOf(newDurationString);
                    SyncUtils.setPeriodicSync(
                            mUserAccountManager.getCurrentUserAccount().getAccount(),
                            SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE,
                            false,
                            newDuration
                    );
                    break;
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

    public void setAvatarPolicySummary() {
        String avatarPolicyString = getPreferenceManager().getSharedPreferences().getString(PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY, PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY_VALUE);
        int imageDownloadPolicy = Integer.valueOf(avatarPolicyString);
        String[] policyArray = getActivity().getResources().getStringArray(R.array.image_download_policy_arrays);
        Preference avatarDownloadPolicyPrefs = findPreference(PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY);
        avatarDownloadPolicyPrefs.setSummary(policyArray[imageDownloadPolicy]);
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

        Preference changelogPref = findPreference("prefs_about_changelog");
        changelogPref.setOnPreferenceClickListener(preference -> {
            ChangeLogUtils reader = new ChangeLogUtils(getActivity(), R.xml.changelog);

            new MaterialDialog.Builder(getActivity())
                    .title(R.string.settings_item_change_log_title)
                    .content(reader.toSpannable())
                    .show();
            return true;
        });
    }

    private void setupFeedbackPreference() {
        Preference feedBackPreference = findPreference("prefs_feedback");
        feedBackPreference.setOnPreferenceClickListener(preference -> {
            mNavigation.openActivityForPostListByThreadId(getActivity(), 1153838l);
            return true;
        });
    }

    private void setupThemePreference() {
        Preference themePreference = findPreference("prefs_theme");
        themePreference.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(getActivity(), ThemeSettingsActivity.class));
            return true;
        });
    }

    private void setupDonationPreference() {
        Preference donatePreference = findPreference("prefs_about_donate");
        donatePreference.setOnPreferenceClickListener(preference -> {
            DonateUtils.showDonateDialog(getActivity());
            return true;
        });
    }

    private void refreshCheckNoticeAutoSync() {
        boolean isCheckNoticeAutoSync = ContentResolver.getSyncAutomatically(
                mUserAccountManager.getCurrentUserAccount().getAccount(),
                SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE
        );
        CheckBoxPreference autoSyncPreference = (CheckBoxPreference) findPreference(PreferenceConstant.SHARED_PREFERENCE_ENABLE_BACKGROUND_NOTIFICATION);
        getPreferenceManager().getSharedPreferences().edit().putBoolean(PreferenceConstant.SHARED_PREFERENCE_ENABLE_BACKGROUND_NOTIFICATION, isCheckNoticeAutoSync);
        autoSyncPreference.setChecked(isCheckNoticeAutoSync);
    }
}
