package org.cryse.lkong.utils.htmltextview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.cryse.lkong.utils.transformation.FitSizeTransformation;

import java.lang.ref.WeakReference;

public class ClickableImageSpan extends DynamicDrawableSpanWithoutSpacing implements PendingImageSpan {
    private static final int MAX_HEIGHT = 1280;
    private AsyncTargetDrawable mDrawable;
    private WeakReference<Context> mContext;
    private String mSource;
    private String mSourceMiddle;
    private int mPlaceHolderRes;
    private int mErrorRes;
    private int mMaxWidth;
    private int mMaxHeight;
    private Object mIdentityTag;
    private Object mPicassoTag;
    private WeakReference<ImageSpanContainer> mContainer;
    private boolean mIsLoaded = false;

    /**
     * @param verticalAlignment one of {@link android.text.style.DynamicDrawableSpan#ALIGN_BOTTOM} or
     * {@link android.text.style.DynamicDrawableSpan#ALIGN_BASELINE}.
     */
    public ClickableImageSpan(
            Context context,
            ImageSpanContainer container,
            Object identityTag,
            Object picassoTag,
            String source,
            @DrawableRes int placeholderRes,
            @DrawableRes int errorRes,
            int maxWidth,
            int maxHeight,
            int verticalAlignment
    ) {
        super(verticalAlignment);
        mContext = new WeakReference<Context>(context);
        mContainer = new WeakReference<ImageSpanContainer>(container);
        mIdentityTag = identityTag;
        mPicassoTag = picassoTag;
        mSource = source;
        if(source.contains("sinaimg"))
            mSourceMiddle = source.replace("/large/", "/bmiddle/");
        else
            mSourceMiddle = source;
        mPlaceHolderRes = placeholderRes;
        mErrorRes = errorRes;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        mDrawable = new AsyncTargetDrawable(
                mContext.get(),
                mContainer.get(),
                mIdentityTag,
                AsyncDrawableType.NORMAL,
                mMaxWidth,
                mMaxHeight
        );
    }

    public ClickableImageSpan(
            Context context,
            ImageSpanContainer container,
            Object identityTag,
            Object picassoTag,
            String source,
            @DrawableRes int placeholderRes,
            @DrawableRes int errorRes,
            int maxWidth,
            int maxHeight,
            int verticalAlignment,
            Drawable initDrawable
    ) {
        super(verticalAlignment);
        mContext = new WeakReference<Context>(context);
        mContainer = new WeakReference<ImageSpanContainer>(container);
        mIdentityTag = identityTag;
        mPicassoTag = picassoTag;
        mSource = source;
        if(source.contains("sinaimg"))
            mSourceMiddle = source.replace("/large/", "/bmiddle/");
        else
            mSourceMiddle = source;
        mPlaceHolderRes = placeholderRes;
        mErrorRes = errorRes;
        mMaxWidth = maxWidth;
        mMaxHeight = maxHeight;
        initDrawable.setBounds(0,0, maxWidth, maxHeight);
        mDrawable = new AsyncTargetDrawable(
                mContext.get(),
                mContainer.get(),
                mIdentityTag,
                AsyncDrawableType.NORMAL,
                initDrawable,
                mMaxWidth,
                mMaxHeight
        );
    }

    @Override
    public Drawable getDrawable() {
        Drawable drawable = null;

        if (mDrawable != null) {
            drawable = mDrawable;
        } else {
            Log.e("ClickableImageSpan", "drawable is null");
        }
        return drawable;
    }

    /**
     * Returns the source string that was saved during construction.
     */
    public String getSource() {
        return mSource;
    }

    @Override
    public void loadImage(ImageSpanContainer container) {
        mContainer = new WeakReference<ImageSpanContainer>(container);
        mDrawable.setContainer(container);
        if(!mIsLoaded && mContext.get() != null) {
            Glide
                    .with(mContext.get())
                    .load(mSourceMiddle)
                    .error(mErrorRes)
                    .placeholder(mPlaceHolderRes)
                    .override(mMaxWidth, mMaxHeight)
                    .centerCrop()
                    .into(mDrawable);
            mIsLoaded = true;
        }
    }

    @Override
    public void loadImage(ImageSpanContainer container, int newMaxWidth) {
        if(newMaxWidth > 0)
        mMaxWidth = newMaxWidth;
        mContainer = new WeakReference<ImageSpanContainer>(container);
        mDrawable.setContainer(container);
        if(!mIsLoaded && mContext.get() != null) {
            Glide
                    .with(mContext.get())
                    .load(mSourceMiddle)
                    .error(mErrorRes)
                    .placeholder(mPlaceHolderRes)
                    .override(Integer.MAX_VALUE, Integer.MAX_VALUE)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .skipMemoryCache(true)
                    .transform(
                            new FitSizeTransformation(mContext.get(), mMaxWidth, MAX_HEIGHT)
                    )
                    .into(mDrawable);
            mIsLoaded = true;
        }
    }
}
