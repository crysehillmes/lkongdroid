package org.cryse.lkong.utils;

import android.app.Activity;
import android.content.Context;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.changelog.ChangeLogUtils;
import org.cryse.lkong.BuildConfig;
import org.cryse.lkong.R;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.IntegerPrefs;
import org.cryse.utils.preference.Prefs;
import org.cryse.utils.preference.StringPrefs;

public class ChangelogUtils {


    public static void checkVersionCode(Activity activity) {
        IntegerPrefs versionCodePref = Prefs.getIntPrefs(
                PreferenceConstant.SHARED_PREFERENCE_VERSION_CODE,
                PreferenceConstant.SHARED_PREFERENCE_VERSION_CODE_VALUE
        );
        int versionCode = 0;

        versionCode = BuildConfig.VERSION_CODE;//getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        if (versionCode > versionCodePref.get()) {
            int oldVersion = versionCodePref.get();
            versionCodePref.set(versionCode);
            onUpgrade(activity, oldVersion, versionCode);
            ChangeLogUtils reader = new ChangeLogUtils(activity, R.xml.changelog);

            new MaterialDialog.Builder(activity)
                    .title(R.string.text_new_version_changes)
                    .content(reader.toSpannable(versionCode))
                    .show();
            return;
        }
    }

    protected static void onUpgrade(Context context, int oldVersion, int newVersion) {
        if(oldVersion < 910) {
            StringPrefs stringPrefs = Prefs.getStringPrefs(
                    PreferenceConstant.SHARED_PREFERENCE_POST_TAIL_TEXT,
                    PreferenceConstant.SHARED_PREFERENCE_POST_TAIL_TEXT_VALUE
            );
            if(stringPrefs.get().compareTo(PreferenceConstant.SHARED_PREFERENCE_POST_TAIL_TEXT_OLD_VALUE_BEFORE_910) == 0) {
                stringPrefs.set(context.getString(R.string.settings_item_post_extra_tail_text_default));
            }
        }
    }
}
