package org.cryse.lkong.utils;

public class LKongUrlBuilder {
    public static String buildThreadUrl(long threadId) {
        String threadIdString = Long.toString(threadId);
        return "http://lkong.cn/thread/" + threadIdString;
    }

    public static String buildPostUrl(long threadId, int page, long postId) {
        return String.format("http://lkong.cn/thread/%d/%d.p_%d", threadId, page, postId);
    }
}
