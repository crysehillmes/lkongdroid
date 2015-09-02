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

public class AsyncTargetDrawable extends Drawable implements Target<GlideDrawable> {
    private WeakReference<Context> mContext;
    private WeakReference<ImageSpanContainer> mContainer;
    private Drawable mInnerDrawable;
    private Object mIdentityTag;

    private int mMaxWidth;
    private int mMaxHeight;

    public AsyncTargetDrawable(Context mContext, ImageSpanContainer mContainer, Object mIdentityTag, int mMaxWidth, int mMaxHeight) {
        this.mContext = new WeakReference<Context>(mContext);
        this.mContainer = new WeakReference<ImageSpanContainer>(mContainer);
        this.mIdentityTag = mIdentityTag;
        this.mMaxWidth = mMaxWidth;
        this.mMaxHeight = mMaxHeight;
        this.setBounds(0, 0, mMaxWidth, mMaxHeight);
    }

    public AsyncTargetDrawable(Context mContext, ImageSpanContainer mContainer, Object mIdentityTag, int mMaxWidth, int mMaxHeight, Drawable placeHolderDrawable) {
        this.mContext = new WeakReference<Context>(mContext);
        this.mContainer = new WeakReference<ImageSpanContainer>(mContainer);
        this.mIdentityTag = mIdentityTag;
        this.mMaxWidth = mMaxWidth;
        this.mMaxHeight = mMaxHeight;
        this.mInnerDrawable = placeHolderDrawable;
        this.mInnerDrawable.setBounds(0, 0, mMaxWidth, mMaxHeight);
        this.setBounds(0, 0, mMaxWidth, mMaxHeight);
    }

    @Override
    public void setAlpha(int alpha) {
        final int oldAlpha = 0xFF; // Const in super.getAlpha()
        if (alpha != oldAlpha) {
            mInnerDrawable.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        if(mInnerDrawable != null)
            mInnerDrawable.draw(canvas);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mInnerDrawable.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return mInnerDrawable.getOpacity();
    }

    public void setContainer(ImageSpanContainer container) {
        this.mContainer = new WeakReference<ImageSpanContainer>(container);
    }

    protected void setDrawable(Drawable drawable) {
        mInnerDrawable = drawable;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if(mInnerDrawable != null)
            mInnerDrawable.setBounds(left, top, right, bottom);
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        if(mInnerDrawable != null)
            mInnerDrawable.setBounds(bounds);
    }

    @Override
    public int getIntrinsicWidth() {
        return mInnerDrawable.getIntrinsicWidth();
    }

    @Override
    public int getIntrinsicHeight() {
        return mInnerDrawable.getIntrinsicHeight();
    }

    @Override
    public void setChangingConfigurations(int configs) {
        mInnerDrawable.setChangingConfigurations(configs);
    }

    @Override
    public int getChangingConfigurations() {
        return mInnerDrawable.getChangingConfigurations();
    }

    @Override
    public void setDither(boolean dither) {
        mInnerDrawable.setDither(dither);
    }

    @Override
    public void setFilterBitmap(boolean filter) {
        mInnerDrawable.setFilterBitmap(filter);
    }

    @Override
    public Callback getCallback() {
        return mInnerDrawable.getCallback();
    }

    @Override
    public void invalidateSelf() {
        if(mInnerDrawable != null)
            mInnerDrawable.invalidateSelf();
    }

    @Override
    public int getAlpha() {
        return 0xFF;
    }

    @Override
    public void setColorFilter(int color, PorterDuff.Mode mode) {
        mInnerDrawable.setColorFilter(color, mode);
    }

    @Override
    public void clearColorFilter() {
        mInnerDrawable.clearColorFilter();
    }

    @Override
    public boolean isStateful() {
        return mInnerDrawable.isStateful();
    }

    @Override
    public boolean setState(int[] stateSet) {
        return mInnerDrawable.setState(stateSet);
    }

    @Override
    public int[] getState() {
        return mInnerDrawable.getState();
    }

    @Override
    public void jumpToCurrentState() {
        mInnerDrawable.jumpToCurrentState();
    }

    @Override
    public Drawable getCurrent() {
        return mInnerDrawable.getCurrent();
    }

    @Override
    public boolean setVisible(boolean visible, boolean restart) {
        return mInnerDrawable.setVisible(visible, restart);
    }

    @Override
    public Region getTransparentRegion() {
        return mInnerDrawable.getTransparentRegion();
    }

    @Override
    public int getMinimumWidth() {
        return mInnerDrawable.getMinimumWidth();
    }

    @Override
    public int getMinimumHeight() {
        return mInnerDrawable.getMinimumHeight();
    }

    @Override
    public boolean getPadding(Rect padding) {
        return mInnerDrawable.getPadding(padding);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        setDrawable(placeholder);
        setBounds(0,0, mMaxWidth, mMaxHeight);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        setDrawable(errorDrawable);
        setBounds(0,0, mMaxWidth, mMaxHeight);
    }

    @Override
    public void onResourceReady(GlideDrawable resource, GlideAnimation glideAnimation) {
        if(mContext.get() == null) throw new IllegalStateException("Context is null, cannot create bitmap drawable.");
        setDrawable(resource);
        setBounds(0,0, mMaxWidth, mMaxHeight);
        if(mContainer.get() != null) {
            mContainer.get().notifyImageSpanLoaded(mIdentityTag);
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
}
