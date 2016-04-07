package org.cryse.lkong.utils;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.lkong.R;

public class DonateUtils {
    public static void showDonateDialog(Activity activity) {
        new MaterialDialog.Builder(activity)
                .title(R.string.donation_title)
                .content(R.string.donation_content)
                .positiveText(R.string.donation_positive)
                .onPositive((dialog, which) -> {
                    ClipboardManager clipboard = (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("tyk5555@hotmail.com", "tyk5555@hotmail.com");
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(activity, activity.getString(R.string.donation_clipboard_toast), Toast.LENGTH_SHORT).show();
                    //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.alipay.com"));
                    //activity.startActivity(browserIntent);
                    Intent startAlipayIntent = activity.getPackageManager().getLaunchIntentForPackage("com.eg.android.AlipayGphone");
                    if(startAlipayIntent == null) {
                        startAlipayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.alipay.com"));
                    }
                    dialog.dismiss();
                    activity.startActivity(startAlipayIntent);
                })
                .show();
    }
}
