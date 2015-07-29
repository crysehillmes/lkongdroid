package org.cryse.lkong.utils;

import android.content.Context;
import android.provider.Settings;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeFormatUtils {
    public static String formatDate(Context context, Date date, boolean withTime)
    {
        String result = "";
        DateFormat dateFormat;

        if (date != null) {
            String format = Settings.System.getString(context.getContentResolver(), Settings.System.DATE_FORMAT);
            if (TextUtils.isEmpty(format)) {
                dateFormat = android.text.format.DateFormat.getDateFormat(context);
            } else {
                dateFormat = new SimpleDateFormat(format);
            }
            result = dateFormat.format(date);

            if (withTime) {
                dateFormat = android.text.format.DateFormat.getTimeFormat(context);
                result += " " + dateFormat.format(date);
            }
        }

        return result;
    }
}
