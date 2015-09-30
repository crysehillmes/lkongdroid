package org.cryse.lkong.utils.htmltextview;

public interface PendingImageSpan {
    void loadImage(ImageSpanContainer container);
    void loadImage(ImageSpanContainer container, int newMaxWidth, int backgroundColor);
}
