package org.cryse.lkong.logic;

public class LKongWebConstants {
    public static final String LKONG_DOMAIN_URL = "http://lkong.cn";
    public static final String LKONG_INDEX_URL = LKONG_DOMAIN_URL + "/index.php";

    public static final String LKONG_FORUM_LIST_REQUEST_URL = LKONG_INDEX_URL + "?mod=ajax&action=forumlist";

    public static final String USER_CONFIG_URL = LKONG_DOMAIN_URL + "/user/index.php?mod=ajax&action=userconfig_%06d";
    public static final String LKONG_FORUM_CONFIG_REQUEST_URL = LKONG_INDEX_URL + "?mod=ajax&action=forumconfig_%d";
    public static final String FORUM_THREAD_LIST_URL = LKONG_INDEX_URL + "?mod=data&sars=forum/%d%s";
    public static final String THREAD_INFO_URL = LKONG_INDEX_URL + "?mod=ajax&action=threadconfig_%d";
    public static final String THREAD_POST_LIST_URL = LKONG_INDEX_URL + "?mod=data&sars=thread/%d/%s";

    public static final String FOLLOW_URL = LKONG_DOMAIN_URL + "/forum/index.php?mod=follow";
    public static final String PRIVATE_CHAT_LIST_URL = LKONG_INDEX_URL + "?mod=data&sars=my/pm";
    public static final String PRIVATE_CHAT_CONFIG_URL = LKONG_INDEX_URL + "?mod=ajax&action=pmconfig_%d";
    public static final String PRIVATE_MESSAGES_URL = LKONG_INDEX_URL + "?mod=data&sars=pm/%d";
    public static final String SUBMIT_BOX_URL = LKONG_INDEX_URL + "?mod=ajax&action=submitbox";
    public static final String CHECK_NOTICE_COUNT_URL = LKONG_INDEX_URL + "?mod=ajax&action=langloop";
}
