package org.cryse.lkong.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class ContentProcessor {
    public static final int IMG_TYPE_LOCAL = 1;
    public static final int IMG_TYPE_URL = 2;
    public static final int IMG_TYPE_EMOJI = 3;
    private String mOriginalContent;
    private String mResultContent;
    private UploadImageCallback mUploadImageCallback;

    public ContentProcessor(String html) {
        this.mOriginalContent = html;
    }

    private static final Pattern mImagePattern = Pattern.compile("\\(\\[\\(\\[\\[(\\d)\\]\\[([^\\]]+)\\]\\]\\)\\]\\)");
    private static final Pattern mBookNamePattern = Pattern.compile("《([^》]+)》");
    public void run() {
        mResultContent = processImage(mOriginalContent);
        mResultContent = processBookName(mResultContent);
    }

    private String processImage(String content) {
        Matcher matcher = mImagePattern.matcher(content);
        StringBuffer replaceBuffer = new StringBuffer();
        while (matcher.find()) {
            switch(Integer.valueOf(matcher.group(1))) {
                case IMG_TYPE_URL:
                    matcher.appendReplacement(replaceBuffer, matcher.group(2));
                    break;
                case IMG_TYPE_EMOJI:
                    matcher.appendReplacement(replaceBuffer, "http://img.lkong.cn/bq/" + matcher.group(2) + ".gif\"" + " em=\"" + matcher.group(2).substring(2));
                    break;
                case IMG_TYPE_LOCAL:
                        String uploadUrl = mUploadImageCallback.uploadImage(matcher.group(2));
                    matcher.appendReplacement(replaceBuffer, uploadUrl);
                    break;
            }
        }
        matcher.appendTail(replaceBuffer);
        return replaceBuffer.toString();
    }

    private String processBookName(String content) {
        Matcher matcher = mBookNamePattern.matcher(content);
        StringBuffer replaceBuffer = new StringBuffer();
        while (matcher.find()) {
            String bookNameEncoded = matcher.group(1);
            try {
                bookNameEncoded = URLEncoder.encode(matcher.group(1), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                Timber.e(e, e.getMessage(), "ContentProcessor");
            }
            String bookUrl = String.format("http://www.lkong.net/book.php?mod=view&bookname=%s", bookNameEncoded);
            matcher.appendReplacement(replaceBuffer, "<a href=\"" + bookUrl + "\"" + ">" + matcher.group(1) + "</a>");
        }
        matcher.appendTail(replaceBuffer);
        return replaceBuffer.toString();
    }

    public void setUploadImageCallback(UploadImageCallback uploadImageCallback) {
        this.mUploadImageCallback = uploadImageCallback;
    }

    public String getResultContent() {
        return mResultContent;
    }

    public interface UploadImageCallback {
        String uploadImage(String path);
    }
}
