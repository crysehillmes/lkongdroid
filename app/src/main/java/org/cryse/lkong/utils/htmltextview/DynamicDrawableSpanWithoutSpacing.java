package org.cryse.lkong.utils.htmltextview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ReplacementSpan;

import java.lang.ref.WeakReference;

public abstract class DynamicDrawableSpanWithoutSpacing extends ReplacementSpan {
    private static final String TAG = DynamicDrawableSpanWithoutSpacing.class.getSimpleName();

    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the bottom of the surrounding text, i.e., at the same level as the
     * lowest descender in the text.
     */
    public static final int ALIGN_BOTTOM = 0;

    /**
     * A constant indicating that the bottom of this span should be aligned
     * with the baseline of the surrounding text.
     */
    public static final int ALIGN_BASELINE = 1;

    protected final int mVerticalAlignment;

    public DynamicDrawableSpanWithoutSpacing() {
        mVerticalAlignment = ALIGN_BOTTOM;
    }

    /**
     * @param verticalAlignment one of {@link #ALIGN_BOTTOM} or {@link #ALIGN_BASELINE}.
     */
    protected DynamicDrawableSpanWithoutSpacing(int verticalAlignment) {
        mVerticalAlignment = verticalAlignment;
    }

    /**
     * Returns the vertical alignment of this span, one of {@link #ALIGN_BOTTOM} or
     * {@link #ALIGN_BASELINE}.
     */
    public int getVerticalAlignment() {
        return mVerticalAlignment;
    }

    /**
     * Your subclass must implement this method to provide the bitmap
     * to be drawn.  The dimensions of the bitmap must be the same
     * from each call to the next.
     */
    public abstract Drawable getDrawable();

    @Override
    public int getSize(Paint paint, CharSequence text,
                       int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();
        float ratio = 1.0f;
        int newAscent = (int)((float)(rect.bottom - rect.top) / ratio);
        if (fm != null) {
            fm.ascent = - newAscent;
            fm.descent = 0;

            fm.top = - newAscent; //fm.ascent;
            fm.bottom = 0;
        }

        return rect.right;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, Paint paint) {
        //Log.e("DrawableSpan.draw", String.format("Args: %d, %d, %f, %d, %d, %d, %s", start, end, x, top, y, bottom, text));
        Drawable b = getCachedDrawable();
        canvas.save();

        int drawableBottom = b.getBounds().bottom;
        int transY = bottom - drawableBottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY -= paint.getFontMetricsInt().descent;
        }
        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null)
            d = wr.get();

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<Drawable>(d);
        }

        return d;
    }

    private WeakReference<Drawable> mDrawableRef;
}