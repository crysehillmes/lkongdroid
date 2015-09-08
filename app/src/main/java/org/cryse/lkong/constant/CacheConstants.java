package org.cryse.lkong.constant;

public class CacheConstants {
    public static final String CACHE_KEY_FORUM_LIST = "cache_forum_list";
    public static final String CACHE_KEY_PUNCH_RESULT = "cache_punch_result";
    public static final String CACHE_KEY_NOTIFICATION_COUNT = "cache_notification_count";



    public static String generatePunchResultKey(long uid) {
        return CACHE_KEY_PUNCH_RESULT + "|||" + uid;
    }

    public static String generateNoticeCountKey(long uid) {
        return CACHE_KEY_NOTIFICATION_COUNT + "|||" + uid;
    }
}