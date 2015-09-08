package org.cryse.utils;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormatUtils {
    public static String formatDateDividByToday(Date datetime, String todayPrefix, Locale locale) {
        SimpleDateFormat formatter = null;
        if(DateUtils.isToday(datetime.getTime())) {
            formatter = new SimpleDateFormat("HH:mm", locale);
            return todayPrefix + " " + formatter.format(datetime);
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd", locale);
            return formatter.format(datetime);
        }
    }

    public static String formatFullDateDividByToday(Date datetime, String todayPrefix, Locale locale) {
        SimpleDateFormat formatter = null;
        if(DateUtils.isToday(datetime.getTime())) {
            formatter = new SimpleDateFormat("HH:mm", locale);
            return todayPrefix + " " + formatter.format(datetime);
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", locale);
            return formatter.format(datetime);
        }
    }
}
