package org.cryse.lkong.utils.htmltextview;

import android.graphics.drawable.Drawable;

public interface ImageSpanContainer {
    void notifyImageSpanLoaded(Object tag, Drawable drawable, AsyncDrawableType type);
}
