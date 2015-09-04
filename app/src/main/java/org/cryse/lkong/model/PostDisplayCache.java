package org.cryse.lkong.model;

import android.text.DynamicLayout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;

import java.util.ArrayList;

public class PostDisplayCache {
    private SpannableStringBuilder spannableStringBuilder = null;
    private ArrayList<Object> importantSpans;
    private ArrayList<Object> emoticonSpans;
    private ArrayList<String> imageUrls;
    private int urlSpanCount;
    private DynamicLayout textLayout = null;
    private StaticLayout authorLayout = null;

    public PostDisplayCache() {
        importantSpans = new ArrayList<>();
        imageUrls = new ArrayList<>();
        emoticonSpans = new ArrayList<>();
    }

    public PostDisplayCache(int spanCapacity, int urlCapacity, int emoticonCapacity) {
        importantSpans = new ArrayList<>(spanCapacity);
        imageUrls = new ArrayList<>(urlCapacity);
        emoticonSpans = new ArrayList<>(emoticonCapacity);
    }

    public SpannableStringBuilder getSpannableStringBuilder() {
        return spannableStringBuilder;
    }

    public void setSpannableStringBuilder(SpannableStringBuilder spannableString) {
        this.spannableStringBuilder = spannableString;
    }

    public ArrayList<Object> getImportantSpans() {
        return importantSpans;
    }

    public void setImportantSpans(ArrayList<Object> importantSpans) {
        this.importantSpans = importantSpans;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public int getUrlSpanCount() {
        return urlSpanCount;
    }

    public void setUrlSpanCount(int urlSpanCount) {
        this.urlSpanCount = urlSpanCount;
    }

    public ArrayList<Object> getEmoticonSpans() {
        return emoticonSpans;
    }

    public void setEmoticonSpans(ArrayList<Object> emoticonSpans) {
        this.emoticonSpans = emoticonSpans;
    }

    public DynamicLayout getTextLayout() {
        return textLayout;
    }

    public void setTextLayout(DynamicLayout textLayout) {
        this.textLayout = textLayout;
    }

    public StaticLayout getAuthorLayout() {
        return authorLayout;
    }

    public void setAuthorLayout(StaticLayout authorLayout) {
        this.authorLayout = authorLayout;
    }
}
