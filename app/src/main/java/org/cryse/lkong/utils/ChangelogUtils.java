package org.cryse.lkong.utils;

import android.app.Activity;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.changelog.ChangeLogUtils;
import org.cryse.lkong.BuildConfig;
import org.cryse.lkong.R;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.IntegerPrefs;
import org.cryse.utils.preference.Prefs;

public class ChangelogUtils {


    public static void checkVersionCode(Activity activity) {
        IntegerPrefs versionCodePref = Prefs.getIntPrefs(
                PreferenceConstant.SHARED_PREFERENCE_VERSION_CODE,
                PreferenceConstant.SHARED_PREFERENCE_VERSION_CODE_VALUE
        );
        int versionCode = 0;

        versionCode = BuildConfig.VERSION_CODE;//getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        if (versionCode > versionCodePref.get()) {
            versionCodePref.set(versionCode);
            ChangeLogUtils reader = new ChangeLogUtils(activity, R.xml.changelog);

            new MaterialDialog.Builder(activity)
                    .title(R.string.text_new_version_changes)
                    .content(reader.toSpannable(versionCode))
                    .show();
            return;
        }
    }
}
