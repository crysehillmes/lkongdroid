package org.cryse.lkong.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.webkit.WebView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.lkong.R;

import timber.log.Timber;

public class AboutFragment extends PreferenceFragment {
    private static final String LOG_TAG = AboutFragment.class.getSimpleName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_about);
    }
}
