package org.cryse.lkong.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.lkong.R;

public class DonateUtils {
    public static void showDonateDialog(Activity activity) {
        new MaterialDialog.Builder(activity)
                .title(R.string.donation_title)
                .content(R.string.donation_content)
                .positiveText(R.string.donation_positive)
                .onPositive((dialog, which) -> {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.alipay.com"));
                    activity.startActivity(browserIntent);
                    dialog.dismiss();
                })
                .show();
    }
}
