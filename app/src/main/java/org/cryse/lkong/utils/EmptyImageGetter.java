package org.cryse.lkong.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

public class EmptyImageGetter implements Html.ImageGetter {
    private static final Drawable sEmptyDrawable = new ColorDrawable(Color.TRANSPARENT);
    static {
        sEmptyDrawable.setBounds(0, 0, 0, 0);
    }
    public EmptyImageGetter() {
    }

    public Drawable getDrawable(String source) {
        return sEmptyDrawable;
    }
}