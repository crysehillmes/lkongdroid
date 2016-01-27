package org.cryse.lkong.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html.ImageGetter;
import android.util.Log;

import org.cryse.lkong.R;

import java.io.IOException;
import java.io.InputStream;

public class SimpleImageGetter implements ImageGetter {
    Context mContext;
    int mEmoticonSize  = 0;
    int mPlaceHolderResource  = 0;
    int mErrorResource = 0;
    int mImageDownloadPolicy = 0;

    public SimpleImageGetter(Context context, int downloadPolicy) {
        this.mContext = context;
        this.mImageDownloadPolicy = downloadPolicy;
    }

    public SimpleImageGetter setEmoticonSize(int emoticonSize) {
        this.mEmoticonSize = emoticonSize;
        return this;
    }

    public SimpleImageGetter setPlaceHolder(@DrawableRes int placeHolder) {
        this.mPlaceHolderResource = placeHolder;
        return this;
    }

    public SimpleImageGetter setError(@DrawableRes int error) {
        this.mErrorResource = error;
        return this;
    }

    private static final String EMOJI_PREFIX = "http://img.lkong.cn/bq/";
    private static final String EMOJI_PATH_WITH_SLASH = "emoji/";
    public Drawable getDrawable(String source) {


        Drawable drawable = null;
        if (source != null && source.startsWith(EMOJI_PREFIX)) {
            try {
                String emojiFileName = source.substring(EMOJI_PREFIX.length());
                String assetsPath = /*"file:///android_asset/" + */EMOJI_PATH_WITH_SLASH + emojiFileName + ".png";
                drawable = getDrawableFromAssets(mContext, assetsPath);
                int height = mEmoticonSize;
                int width = height * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                int top = (mEmoticonSize / 2 - height) / 2;
                drawable.setBounds(0, top, width, top + height);

            } catch (Exception e) {
                // swallow
            }
        }
        if(drawable == null) {
            drawable = new ColorDrawable(Color.TRANSPARENT);
        }
        return drawable;
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
}
