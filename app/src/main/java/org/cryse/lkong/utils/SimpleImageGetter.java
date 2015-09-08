package org.cryse.lkong.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html.ImageGetter;
import android.util.Log;

import org.cryse.lkong.R;

import java.io.IOException;

public class SimpleImageGetter implements ImageGetter {
    Context mContext;
    int mEmoticonSize  = 0;
    int mMaxImageWidth = 0;
    int mMaxImageHeight = 0;
    int mPlaceHolderResource  = 0;
    int mErrorResource = 0;
    int mImageDownloadPolicy = 0;

    public SimpleImageGetter(Context context, int downloadPolicy) {
        this.mContext = context;
        this.mImageDownloadPolicy = downloadPolicy;
    }

    public SimpleImageGetter setMaxImageSize(int maxImageWidth, int maxImageHeight) {
        this.mMaxImageWidth = maxImageWidth;
        this.mMaxImageHeight = maxImageHeight;
        return this;
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
        if(source == null) {
            return mContext.getResources().getDrawable(mPlaceHolderResource);
        }
        if(source.startsWith(EMOJI_PREFIX)) {
            String emojiFileName = source.substring(EMOJI_PREFIX.length());
            try {
                Drawable emojiDrawable = Drawable.createFromStream(mContext.getAssets().open(EMOJI_PATH_WITH_SLASH + emojiFileName + ".png"), null);
                emojiDrawable.setBounds(0, 0, mEmoticonSize == 0 ? emojiDrawable.getIntrinsicWidth() : mEmoticonSize,
                        mEmoticonSize == 0 ? emojiDrawable.getIntrinsicHeight() : mEmoticonSize);
                return emojiDrawable;
            } catch (IOException e) {
                Log.d("UrlImageGetter", "getDrawable() from assets failed.", e);
                Drawable emojiPlaceholder = ResourcesCompat.getDrawable(
                        mContext.getResources(),
                        R.drawable.placeholder_error,
                        null
                );
                emojiPlaceholder.setBounds(0, 0, mEmoticonSize == 0 ? emojiPlaceholder.getIntrinsicWidth() : mEmoticonSize,
                        mEmoticonSize == 0 ? emojiPlaceholder.getIntrinsicHeight() : mEmoticonSize);
                return emojiPlaceholder;
            }
        } else {
            Drawable placeHolderDrawable = ResourcesCompat.getDrawable(
                    mContext.getResources(),
                    R.drawable.placeholder_loading,
                    null
            );
            placeHolderDrawable.setBounds(0, 0, mMaxImageWidth == 0 ? placeHolderDrawable.getIntrinsicWidth() : mMaxImageWidth,
                    mMaxImageHeight == 0 ? placeHolderDrawable.getIntrinsicHeight() : mMaxImageHeight);
            return placeHolderDrawable;
        }
    }
}
