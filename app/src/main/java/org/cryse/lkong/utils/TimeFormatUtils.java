/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.cryse.lkong.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.cryse.lkong.R;

public class TimeFormatUtils {
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    public static String getTimeAgo(Context ctx, long time) {
        // TODO: use DateUtils methods instead
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = getCurrentTime(ctx);
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE) {
            return ctx.getString(R.string.format_time_just_now);
        } else if (diff < 2 * MINUTE) {
            return ctx.getString(R.string.format_time_a_minute_ago);
        } else if (diff < 50 * MINUTE) {
            return diff / MINUTE + ctx.getString(R.string.format_time_n_minutes_ago);
        } else if (diff < 90 * MINUTE) {
            return ctx.getString(R.string.format_time_an_hour_ago);
        } else if (diff < 24 * HOUR) {
            return diff / HOUR + ctx.getString(R.string.format_time_n_hours_ago);
        } else if (diff < 48 * HOUR) {
            return ctx.getString(R.string.format_time_yesterday);
        } else {
            return diff / DAY + ctx.getString(R.string.format_time_n_days_ago);
        }
    }

    private static final SimpleDateFormat[] ACCEPTED_TIMESTAMP_FORMATS = {
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US),
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss Z", Locale.US)
    };

    private static final SimpleDateFormat VALID_IFMODIFIEDSINCE_FORMAT =
            new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);

    public static Date parseTimestamp(String timestamp) {
        for (SimpleDateFormat format : ACCEPTED_TIMESTAMP_FORMATS) {
            format.setTimeZone(TimeZone.getTimeZone("GMT"));
            try {
                return format.parse(timestamp);
            } catch (ParseException ex) {
                continue;
            }
        }

        // All attempts to parse have failed
        return null;
    }

    public static boolean isValidFormatForIfModifiedSinceHeader(String timestamp) {
        try {
            return VALID_IFMODIFIEDSINCE_FORMAT.parse(timestamp)!=null;
        } catch (Exception ex) {
            return false;
        }
    }

    public static long timestampToMillis(String timestamp, long defaultValue) {
        if (TextUtils.isEmpty(timestamp)) {
            return defaultValue;
        }
        Date d = parseTimestamp(timestamp);
        return d == null ? defaultValue : d.getTime();
    }

    public static String formatShortDate(Context context, Date date) {
        StringBuilder sb = new StringBuilder();
        Formatter formatter = new Formatter(sb);
        return DateUtils.formatDateRange(context, formatter, date.getTime(), date.getTime(),
                DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_NO_YEAR,
                getDisplayTimeZone(context).getID()).toString();
    }

    public static String formatShortTime(Context context, Date time) {
        DateFormat format = DateFormat.getTimeInstance(DateFormat.SHORT);
        TimeZone tz = getDisplayTimeZone(context);
        if (tz != null) {
            format.setTimeZone(tz);
        }
        return format.format(time);
    }

    /**
     * Returns "Today", "Tomorrow", "Yesterday", or a short date format.
     */
    public static String formatHumanFriendlyShortDate(final Context context, long timestamp) {
        long localTimestamp, localTime;
        long now = getCurrentTime(context);

        TimeZone tz = getDisplayTimeZone(context);
        localTimestamp = timestamp + tz.getOffset(timestamp);
        localTime = now + tz.getOffset(now);

        long dayOrd = localTimestamp / 86400000L;
        long nowOrd = localTime / 86400000L;

        if (dayOrd == nowOrd) {
            return context.getString(R.string.text_day_title_today);
        } else if (dayOrd == nowOrd - 1) {
            return context.getString(R.string.text_day_title_yesterday);
        } else if (dayOrd == nowOrd + 1) {
            return context.getString(R.string.text_day_title_tomorrow);
        } else {
            return formatShortDate(context, new Date(timestamp));
        }
    }

    public static long getCurrentTime(final Context context) {
        return System.currentTimeMillis();
    }

    public static TimeZone getDisplayTimeZone(Context context) {
        TimeZone defaultTz = TimeZone.getDefault();
        return defaultTz;
    }

    public static String formatDate(Context context, Date date, boolean withTime)
    {
        String result = "";
        /*DateFormat dateFormat;

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
        }*/
        DateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.format_time_date));
        result = dateFormat.format(date);
        if (withTime) {
            DateFormat timeFormat = new SimpleDateFormat(context.getString(R.string.format_time_time));
            result += " " + timeFormat.format(date);
        }
        return result;
    }

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
