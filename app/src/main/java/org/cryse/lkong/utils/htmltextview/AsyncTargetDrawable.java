package org.cryse.lkong.utils.htmltextview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;

import java.lang.ref.WeakReference;

public class AsyncTargetDrawable extends Drawable implements Target<GlideDrawable>, Drawable.Callback {
    public static final int TYPE_NORMAL_DRAWABLE = 11;
    public static final int TYPE_EMOTICON_DRAWABLE = 12;
    private WeakReference<Context> mContext;
    private WeakReference<ImageSpanContainer> mContainer;
    private Drawable mInnerDrawable;
    private Object mIdentityTag;

    private AsyncDrawableType mDrawableType;
    private int mMaxWidth;
    private int mMaxHeight;

    public AsyncTargetDrawable(
            Context mContext,
            ImageSpanContainer mContainer,
            Object mIdentityTag,
            AsyncDrawableType type,
            int maxWidth,
            int maxHeight
    ) {
        this.mContext = new WeakReference<Context>(mContext);
        this.mContainer = new WeakReference<ImageSpanContainer>(mContainer);
        this.mIdentityTag = mIdentityTag;
        this.mDrawableType = type;
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
    }

    public AsyncTargetDrawable(
            Context mContext,
            ImageSpanContainer mContainer,
            Object mIdentityTag,
            AsyncDrawableType type,
            Drawable placeHolderDrawable,
            int maxWidth,
            int maxHeight
    ) {
        this.mContext = new WeakReference<Context>(mContext);
        this.mContainer = new WeakReference<ImageSpanContainer>(mContainer);
        this.mIdentityTag = mIdentityTag;
        this.mDrawableType = type;
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
        this.mInnerDrawable = placeHolderDrawable;
        if(mInnerDrawable != null) {
            mInnerDrawable.setBounds(getRecommendBound(mInnerDrawable, mMaxWidth, mMaxHeight, true));
            this.setBounds(getRecommendBound(mInnerDrawable, mMaxWidth, mMaxHeight, true));
        }
    }

    public void setContainer(ImageSpanContainer container) {
        this.mContainer = new WeakReference<ImageSpanContainer>(container);
    }

    @Override
     public void draw(Canvas canvas) {
        if (mInnerDrawable != null) {
            mInnerDrawable.draw(canvas);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        if (mInnerDrawable != null) {
            mInnerDrawable.setAlpha(alpha);
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (mInnerDrawable != null) {
            mInnerDrawable.setColorFilter(cf);
        }
    }

    @Override
    public int getOpacity() {
        if (mInnerDrawable != null) {
            return mInnerDrawable.getOpacity();
        }
        return 0;
    }

    public void setDrawable(Drawable drawable) {
        if (this.mInnerDrawable != null) {
            this.mInnerDrawable.setCallback(null);
        }
        drawable.setCallback(this);
        this.mInnerDrawable = drawable;
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        setDrawable(placeholder);
        Rect bound = getRecommendBound(placeholder, mMaxWidth, mMaxHeight, true);
        placeholder.setBounds(bound);
        setBounds(bound);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        setDrawable(errorDrawable);
        Rect bound = getRecommendBound(errorDrawable, mMaxWidth, mMaxHeight, true);
        errorDrawable.setBounds(bound);
        setBounds(bound);
        if (mContainer.get() != null) {
            mContainer.get().notifyImageSpanLoaded(mIdentityTag, errorDrawable, mDrawableType);
        }
    }

    @Override
    public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
        if (mContext.get() == null)
            throw new IllegalStateException("Context is null, cannot create bitmap drawable.");
        Rect bound = new Rect(0, 0, resource.getIntrinsicWidth(), resource.getIntrinsicHeight());
        resource.setBounds(bound);
        if(mInnerDrawable != null)
            mInnerDrawable.setBounds(bound);
        setDrawable(resource);
        this.setBounds(bound);
        if (mContainer.get() != null) {
            mContainer.get().notifyImageSpanLoaded(mIdentityTag, resource, mDrawableType);
        }
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

    private static Rect getRecommendBound(Drawable drawable, int maxWidth, int maxHeight, boolean forceUseMax) {
        Rect result;
        if(forceUseMax) {
            result = new Rect(0, 0, maxWidth, maxHeight);
        } else {
            int width;
            int intrinsicWidth = drawable.getIntrinsicWidth();
            if(intrinsicWidth <= 0 || intrinsicWidth > maxWidth)
                width = maxWidth;
            else
                width = intrinsicWidth;
            int height;
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if(intrinsicHeight <= 0 || intrinsicHeight > maxHeight)
                height = maxHeight;
            else
                height = intrinsicHeight;
            result = new Rect(0, 0, width, height);
        }
        return result;
    }
}
