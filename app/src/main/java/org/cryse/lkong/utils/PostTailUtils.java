package org.cryse.lkong.utils;

import android.content.Context;
import android.text.TextUtils;

import org.cryse.lkong.R;

public class PostTailUtils {
    public static String getPostTail(Context context, String postTail) {
        if(!TextUtils.isEmpty(postTail))
           return "<br><br>" + context.getString(R.string.format_post_tail_prefix) + "<a href=\"http://lkong.cn/thread/1153838\">" + postTail + "</a>";
        return "";
    }
}
