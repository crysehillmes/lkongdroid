package org.cryse.lkong.utils;

import android.net.Uri;
import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LKongUrlDispatcher {
    static final Pattern sOldLKongPidPattern = Pattern.compile("#pid(\\d+)");
    static final Pattern sOldLKongThreadPattern = Pattern.compile("thread\\-(\\d+)\\-(\\d+)\\-(\\d+)\\.html");
    static final Pattern sNewLKongThreadPattern = Pattern.compile("lkong.cn/thread/(\\d+)(/(\\d+))?(\\.p_(\\d+))?");

    private UrlCallback mUrlCallback;

    public LKongUrlDispatcher(UrlCallback urlCallback) {
        this.mUrlCallback = urlCallback;
    }

    public void parseUrl(String url) {
        if(url.contains("lkong.net")) {
            parseLegacyUrl(url);
        } else if(url.contains("lkong.cn")) {
            parseNewUrl(url);
        } else {
            if(mUrlCallback != null)
                mUrlCallback.onFailed(url);
        }
    }

    public void parseNewUrl(String url) {
        Matcher matcher = sNewLKongThreadPattern.matcher(url);
        if(matcher.find()) {
            int groupCount = matcher.groupCount();
            String tidString = matcher.group(1);
            String pageString = null;
            String pidString = null;
            if(groupCount >= 3)
                pageString = matcher.group(3);
            if(groupCount >= 5)
                pidString = matcher.group(5);
            long tid = Long.valueOf(tidString);
            int page = !TextUtils.isEmpty(pageString) && TextUtils.isDigitsOnly(pageString) ? Integer.valueOf(pageString) : -1;
            long pid = !TextUtils.isEmpty(pidString) && TextUtils.isDigitsOnly(pidString) ?  Integer.valueOf(pidString) : -1l;

            if(mUrlCallback != null) {
                if (pid != -1l)
                    mUrlCallback.onThreadByPostId(tid, pid);
                else if (page != -1)
                    mUrlCallback.onThreadByThreadId(tid, page);
                else
                    mUrlCallback.onThreadByThreadId(tid);
            }
        }
    }

    public void parseLegacyUrl(String url) {
        if(url.contains("forum.php")) {
            Uri uri = Uri.parse(url);
            String tidString = uri.getQueryParameter("tid");
            String pageString = uri.getQueryParameter("page");
            String pidString = null;
            Matcher mPidMacher = sOldLKongPidPattern.matcher(url);
            if (mPidMacher.find( )) {
                pidString = mPidMacher.group(1);
            }
            if(!TextUtils.isEmpty(tidString)) {
                long tid = Long.valueOf(tidString);
                if(!TextUtils.isEmpty(pidString)) {
                    long pid = Long.valueOf(pidString);
                    if(mUrlCallback != null) {
                        mUrlCallback.onThreadByPostId(tid, pid);
                    }
                } else if(!TextUtils.isEmpty(pageString) && TextUtils.isDigitsOnly(pageString)) {
                    // 楼层未知但是知道页数
                    int page = Integer.valueOf(pageString);
                    if(mUrlCallback != null) {
                        mUrlCallback.onThreadByThreadId(tid, page);
                    }
                } else {
                    // 解析失败
                    if(mUrlCallback != null)
                        mUrlCallback.onFailed(url);
                }
            } else if(url.contains("mod=redirect")) {
                // 解析失败
                int index = url.lastIndexOf("pid=");
                String pid = url.substring(index + 4);
                if(mUrlCallback != null)
                    mUrlCallback.onThreadByPostId(Long.valueOf(pid));
            } else {
                // 解析失败
                if(mUrlCallback != null)
                    mUrlCallback.onFailed(url);
            }
        } else if(url.contains("thread-")) {
            Matcher matcher = sOldLKongThreadPattern.matcher(url);
            String tidString = null;
            String pageString = null;
            if(matcher.find()) {
                tidString = matcher.group(1);
                pageString = matcher.group(2);
                if(!TextUtils.isEmpty(tidString) && TextUtils.isDigitsOnly(tidString) && !TextUtils.isEmpty(pageString) && TextUtils.isDigitsOnly(pageString)) {
                    long tid = Long.valueOf(tidString);
                    int page = Integer.valueOf(pageString);
                    if(mUrlCallback != null) {
                        mUrlCallback.onThreadByThreadId(tid, page);
                    }
                }
            }
        } else {
            if(mUrlCallback != null)
                mUrlCallback.onFailed(url);
        }
    }

    public interface UrlCallback {
        void onThreadByPostId(long postId);
        void onThreadByPostId(long tid, long postId);
        void onThreadByThreadId(long threadId);
        void onThreadByThreadId(long threadId, int page);
        void onFailed(String url);
    }
}
