package org.cryse.lkong.utils.htmltextview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.cryse.lkong.application.LKongApplication;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class UrlImageGetter implements ImageGetter {
    Context mContext;
    Picasso mPicasso;
    final Resources mResources;
    int mEmoticonSize  = 0;
    int mMaxImageWidth = 0;
    int mPlaceHolderResource  = 0;
    int mErrorResource = 0;
    int mImageDownloadPolicy = 0;
    /**
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     *
     * @param context
     */
    public UrlImageGetter(Context context, Picasso picasso, int downloadPolicy) {
        this.mContext = context;
        this.mPicasso = picasso;
        this.mResources = context.getResources();
        this.mImageDownloadPolicy = downloadPolicy;
        // this.mEmoticonSize = UIUtils.sp2px(context, context.getResources().getDimension(R.dimen.text_size_body1));
    }

    public UrlImageGetter setMaxImageWidth(int maxImageWidth) {
        this.mMaxImageWidth = maxImageWidth;
        return this;
    }

    public UrlImageGetter setEmoticonSize(int emoticonSize) {
        this.mEmoticonSize = emoticonSize;
        return this;
    }

    public UrlImageGetter setPlaceHolder(@DrawableRes int placeHolder) {
        this.mPlaceHolderResource = placeHolder;
        return this;
    }

    public UrlImageGetter setError(@DrawableRes int error) {
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
            }
        }

        UrlDrawable urlDrawable = new UrlDrawable(mContext, mMaxImageWidth);
        if(LKongApplication.get(mContext).getNetworkPolicyManager().shouldDownloadImage(mImageDownloadPolicy)) {
            mPicasso.load(source).placeholder(mPlaceHolderResource).error(mErrorResource).into(urlDrawable);
        } else {
            mPicasso.load(mPlaceHolderResource).placeholder(mPlaceHolderResource).error(mErrorResource).into(urlDrawable);
        }
        return urlDrawable;
    }

    public static class UrlDrawable extends BitmapDrawable implements Target {
        protected Context mContext;
        protected Drawable mDrawable;
        protected int mMaxWidth = 0;
        public UrlDrawable(Context context, int maxWidth) {
            super(context.getResources(), (Bitmap)null);
            this.mContext = context;
            this.mMaxWidth = maxWidth;
        }

        @Override
        public void draw(Canvas canvas) {
            // override the draw to facilitate refresh function later
            if (mDrawable != null) {
                mDrawable.draw(canvas);
            }
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            Drawable newDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
            setDrawableAndSelfBounds(newDrawable);
            invalidateTargetTextView();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            setDrawableAndSelfBounds(errorDrawable);
            invalidateTargetTextView();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            setDrawableAndSelfBounds(placeHolderDrawable);
            invalidateTargetTextView();
        }

        private void invalidateTargetTextView() {
        }

        public void setDrawableAndSelfBounds(Drawable newDrawable) {
            int drawableWidth = newDrawable.getIntrinsicWidth();
            int drawableHeight = newDrawable.getIntrinsicHeight();
            int newDrawableIntrinsicWidth = newDrawable.getIntrinsicWidth();
            int newDrawableIntrinsicHeight = newDrawable.getIntrinsicHeight();
            // double whAspectRatio = (double)newDrawableIntrinsicWidth / (double)newDrawableIntrinsicHeight;
            if(mMaxWidth != 0 && newDrawableIntrinsicWidth > mMaxWidth) {
                double ratio = (double) newDrawableIntrinsicWidth / (double) mMaxWidth;
                drawableWidth = mMaxWidth;
                drawableHeight = (int)((double)newDrawableIntrinsicHeight / ratio);
            }
            newDrawable.setBounds(0, 0, drawableWidth, drawableHeight);
            this.mDrawable = newDrawable;
            this.setBounds(0, 0, drawableWidth, drawableHeight);
        }

        @Override
        public int getIntrinsicHeight() {
            return mDrawable.getIntrinsicHeight();
        }

        @Override
        public int getIntrinsicWidth() {
            return mDrawable.getIntrinsicWidth();
        }
    }
} 
