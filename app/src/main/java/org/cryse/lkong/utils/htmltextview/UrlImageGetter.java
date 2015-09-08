package org.cryse.lkong.utils.htmltextview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.Html.ImageGetter;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;

import java.io.IOException;

public class UrlImageGetter implements ImageGetter {
    Context mContext;
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
    public UrlImageGetter(Context context, int downloadPolicy) {
        this.mContext = context;
        this.mResources = context.getResources();
        this.mImageDownloadPolicy = downloadPolicy;
        this.mErrorResource = R.drawable.placeholder_error;
        this.mPlaceHolderResource = R.drawable.placeholder_loading;
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
            Glide
                    .with(mContext)
                    .load(source)
                    .placeholder(mPlaceHolderResource)
                    .error(mErrorResource)
                    .into(urlDrawable);
        } else {
            Glide
                    .with(mContext)
                    .load(mPlaceHolderResource)
                    .placeholder(mPlaceHolderResource)
                    .error(mErrorResource)
                    .into(urlDrawable);
        }
        return urlDrawable;
    }

    public static class UrlDrawable extends BitmapDrawable implements Target<GlideDrawable>, Drawable.Callback {
        protected Context mContext;
        private Drawable mDrawable;
        protected int mMaxWidth = 0;
        public UrlDrawable(Context context, int maxWidth) {
            super(context.getResources(), (Bitmap)null);
            this.mContext = context;
            this.mMaxWidth = maxWidth;
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
            if (this.mDrawable != null) {
                this.mDrawable.setCallback(null);
            }
            newDrawable.setCallback(this);
            this.mDrawable = newDrawable;
            this.setBounds(0, 0, drawableWidth, drawableHeight);
        }

        @Override
        public void draw(Canvas canvas) {
            if (mDrawable != null) {
                mDrawable.draw(canvas);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            if (mDrawable != null) {
                mDrawable.setAlpha(alpha);
            }
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            if (mDrawable != null) {
                mDrawable.setColorFilter(cf);
            }
        }

        @Override
        public int getOpacity() {
            if (mDrawable != null) {
                return mDrawable.getOpacity();
            }
            return 0;
        }

        public void setDrawable(GlideDrawable drawable) {
            if (this.mDrawable != null) {
                this.mDrawable.setCallback(null);
            }
            drawable.setCallback(this);
            this.mDrawable = drawable;
        }

        @Override
        public void invalidateDrawable(Drawable who) {
            if (getCallback() != null) {
                getCallback().invalidateDrawable(who);
            }
        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {
            if (getCallback() != null) {
                getCallback().scheduleDrawable(who, what, when);
            }
        }

        @Override
        public void unscheduleDrawable(Drawable who, Runnable what) {
            if (getCallback() != null) {
                getCallback().unscheduleDrawable(who, what);
            }
        }

        @Override
        public int getIntrinsicHeight() {
            return mDrawable.getIntrinsicHeight();
        }

        @Override
        public int getIntrinsicWidth() {
            return mDrawable.getIntrinsicWidth();
        }

        @Override
        public void onLoadStarted(Drawable placeholder) {
            setDrawableAndSelfBounds(placeholder);
            invalidateTargetTextView();
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            setDrawableAndSelfBounds(errorDrawable);
            invalidateTargetTextView();
        }

        @Override
        public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
            setDrawableAndSelfBounds(resource);
            invalidateTargetTextView();
        }

        @Override
        public void onLoadCleared(Drawable placeholder) {

        }

        @Override
        public void getSize(SizeReadyCallback cb) {

        }

        @Override
        public void setRequest(Request request) {

        }

        @Override
        public Request getRequest() {
            return null;
        }

        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }

        @Override
        public void onDestroy() {

        }
    }
} 
