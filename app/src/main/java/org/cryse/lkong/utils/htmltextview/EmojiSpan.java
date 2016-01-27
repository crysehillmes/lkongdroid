package org.cryse.lkong.utils.htmltextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.DynamicDrawableSpan;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

public class EmojiSpan extends DynamicDrawableSpan {
    private static final String EMOJI_PREFIX = "http://img.lkong.cn/bq/";
    private static final String EMOJI_PATH_WITH_SLASH = "emoji/";

    private final Context mContext;

    private final String mEmojiPath;

    private final int mSize;

    private final int mTextSize;

    private int mHeight;

    private int mWidth;

    private int mTop;

    private Drawable mDrawable;

    private WeakReference<Drawable> mDrawableRef;

    public EmojiSpan(Context context, String emojiPath, int size, int alignment, int textSize) {
        super(alignment);
        mContext = context;
        mEmojiPath = emojiPath;
        mWidth = mHeight = mSize = size;
        mTextSize = textSize;
    }

    public Drawable getDrawable() {
        if (mDrawable == null) {
            try {
                if (mEmojiPath.startsWith(EMOJI_PREFIX)) {
                    String emojiFileName = mEmojiPath.substring(EMOJI_PREFIX.length());
                    String assetsPath = /*"file:///android_asset/" + */EMOJI_PATH_WITH_SLASH + emojiFileName + ".png";
                    mDrawable = getDrawableFromAssets(mContext, assetsPath);
                    mHeight = mSize;
                    mWidth = mHeight * mDrawable.getIntrinsicWidth() / mDrawable.getIntrinsicHeight();
                    mTop = (mTextSize - mHeight) / 2;
                    mDrawable.setBounds(0, mTop, mWidth, mTop + mHeight);
                }

            } catch (Exception e) {
                // swallow
            }
        }
        return mDrawable;
    }

    public static Drawable getDrawableFromAssets(Context context, String url){
        Drawable drawable = null;
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(url);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return drawable;
    }

    @Override
    public void draw(Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, Paint paint) {
        //super.draw(canvas, text, start, end, x, top, y, bottom, paint);
        Drawable b = getCachedDrawable();
        canvas.save();

        int transY = bottom - b.getBounds().bottom;
        if (mVerticalAlignment == ALIGN_BASELINE) {
            transY = top + ((bottom - top) / 2) - ((b.getBounds().bottom - b.getBounds().top) / 2) - mTop;
        }

        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }

    private Drawable getCachedDrawable() {
        if (mDrawableRef == null || mDrawableRef.get() == null) {
            mDrawableRef = new WeakReference<Drawable>(getDrawable());
        }
        return mDrawableRef.get();
    }

    public String getSource() {
        return mEmojiPath;
    }
}