package com.quinny898.library.persistentsearch;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

public class SearchResult {

    public final CharSequence title;
    public final Drawable icon;

    private final String titleString;
    private Bundle mExtras;

    /**
     * Create a search result with text and an icon
     * @param title
     * @param icon
     */
    public SearchResult(CharSequence title, Drawable icon) {
        this.title = title;
        this.icon = icon;
        titleString = title.toString();
    }

    public void setExtras(Bundle extras) {
        mExtras = extras;
    }

    public Bundle getExtras() {
        return mExtras;
    }

    /**
     * Return the title of the result
     */
    @Override
    public String toString() {
        return title.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((titleString == null) ? 0 : titleString.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SearchResult other = (SearchResult) obj;
        if (titleString == null) {
            if (other.titleString != null)
                return false;
        } else if (!titleString.equals(other.titleString))
            return false;
        return true;
    }

}