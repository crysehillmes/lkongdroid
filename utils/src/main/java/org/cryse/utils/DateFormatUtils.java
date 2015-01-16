package org.cryse.utils;

import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatUtils {
    public static String formatDateDividByToday(Date datetime, String todayPrefix) {
        SimpleDateFormat formatter = null;
        if(DateUtils.isToday(datetime.getTime())) {
            formatter = new SimpleDateFormat("HH:mm");
            return todayPrefix + " " + formatter.format(datetime);
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.format(datetime);
        }
    }

    public static String formatFullDateDividByToday(Date datetime, String todayPrefix) {
        SimpleDateFormat formatter = null;
        if(DateUtils.isToday(datetime.getTime())) {
            formatter = new SimpleDateFormat("HH:mm");
            return todayPrefix + " " + formatter.format(datetime);
        } else {
            formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return formatter.format(datetime);
        }
    }
}
