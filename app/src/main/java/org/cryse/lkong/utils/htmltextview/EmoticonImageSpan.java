package org.cryse.lkong.utils.htmltextview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

public class EmoticonImageSpan extends DynamicDrawableSpan implements PendingImageSpan {

    private static final String EMOJI_PREFIX = "http://img.lkong.cn/bq/";
    private static final String EMOJI_PATH_WITH_SLASH = "emoji/";

    private AsyncTargetDrawable mDrawable;
    private WeakReference<Context> mContext;
    private WeakReference<Picasso> mPicasso;
    private String mSource;
    private String mLocalSource;
    private int mPlaceHolderRes;
    private int mErrorRes;
    private int mEmoticonSize;
    private Object mIdentityTag;
    private Object mPicassoTag;
    private WeakReference<ImageSpanContainer> mContainer;
    private boolean mIsLoaded = false;

    public EmoticonImageSpan(
            Context context,
            Picasso picasso,
            ImageSpanContainer container,
            Object identityTag,
            Object picassoTag,
            String source,
            @DrawableRes int placeholderRes,
            @DrawableRes int errorRes,
            int emoticonSize
    ) {
        super(ALIGN_BASELINE);
        mContext = new WeakReference<Context>(context);
        mPicasso = new WeakReference<Picasso>(picasso);
        mContainer = new WeakReference<ImageSpanContainer>(container);
        mIdentityTag = identityTag;
        mPicassoTag = picassoTag;
        mSource = source;
        mLocalSource = getEmoticonLocalSource(mSource);
        mPlaceHolderRes = placeholderRes;
        mErrorRes = errorRes;
        mEmoticonSize = emoticonSize;
        mDrawable = new AsyncTargetDrawable(mContext.get(), mContainer.get(), mIdentityTag, mEmoticonSize, mEmoticonSize);
        // Picasso.with(context).load(mLocalSource).tag(mPicassoTag).error(mErrorRes).placeholder(mPlaceHolderRes).resize(mEmoticonSize, mEmoticonSize).into((AsyncTargetDrawable) mDrawable);
    }

    @Override
    public Drawable getDrawable() {
        Drawable drawable = null;

        if (mDrawable != null) {
            drawable = mDrawable;
        } else {
            Log.e("EmoticonImageSpan", "drawable is null");
        }
        return drawable;
    }

    /**
     * Returns the source string that was saved during construction.
     */
    public String getSource() {
        return mSource;
    }

    private String getEmoticonLocalSource(String source) {
        String localSource;
        if(source.startsWith(EMOJI_PREFIX)) {
            try {
                String emojiFileName = source.substring(EMOJI_PREFIX.length());
                localSource = "file:///android_asset/" + EMOJI_PATH_WITH_SLASH + emojiFileName + ".png";
            } catch (RuntimeException e) {
                localSource = source;
            }
        } else {
            localSource = source;
        }
        return localSource;
    }

    @Override
    public void loadImage(ImageSpanContainer container) {
        mContainer = new WeakReference<ImageSpanContainer>(container);
        mDrawable.setContainer(container);
        if(mPicasso.get() != null && !mIsLoaded) {
            mPicasso.get().load(mLocalSource).tag(mPicassoTag).error(mErrorRes).placeholder(mPlaceHolderRes).resize(mEmoticonSize, mEmoticonSize).noFade().into(mDrawable);
            mIsLoaded = true;
        }
    }
}
