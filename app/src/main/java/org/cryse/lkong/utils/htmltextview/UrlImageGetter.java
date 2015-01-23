/*
 * Copyright (C) 2013 Antarix Tandon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

import java.io.IOException;
import java.lang.ref.WeakReference;

public class UrlImageGetter implements ImageGetter {
    Context mContext;
    TextView mTargetTextView;
    Picasso picasso;
    final Resources mResources;
    int mEmoticonSize  = 0;
    int mMaxImageWidth = 0;
    int mPlaceHolderResource  = 0;
    int mErrorResource = 0;
    /**
     * Construct the URLImageParser which will execute AsyncTask and refresh the container
     *
     * @param context
     * @param targetTextView
     */
    public UrlImageGetter(Context context, TextView targetTextView) {
        this.mContext = context;
        this.mTargetTextView = targetTextView;
        this.picasso = Picasso.with(context);
        this.mResources = context.getResources();
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
                Drawable emojiDrawable = Drawable.createFromStream(mContext.getAssets().open(EMOJI_PATH_WITH_SLASH + emojiFileName), null);
                emojiDrawable.setBounds(0, 0, mEmoticonSize == 0 ? emojiDrawable.getIntrinsicWidth() : mEmoticonSize,
                        mEmoticonSize == 0 ? emojiDrawable.getIntrinsicHeight() : mEmoticonSize);
                return emojiDrawable;
            } catch (IOException e) {
                Log.d("UrlImageGetter::getDrawable()", "getDrawable from assets failed.", e);
            }
        }

        UrlDrawable urlDrawable = new UrlDrawable(mContext, mTargetTextView, mMaxImageWidth);
        picasso.load(source).placeholder(mPlaceHolderResource).error(mErrorResource).into(urlDrawable);
        return urlDrawable;
    }

    public static class UrlDrawable extends BitmapDrawable implements Target {
        protected Context mContext;
        protected Drawable mDrawable;
        protected int mMaxWidth = 0;
        protected WeakReference<TextView> mTargetView;
        public UrlDrawable(Context context, TextView targetTextView, int maxWidth) {
            super(context.getResources(), (Bitmap)null);
            this.mContext = context;
            this.mTargetView = new WeakReference<TextView>(targetTextView);
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
            TextView textView = mTargetView.get();
            if(textView != null) {
                textView.invalidate();
                textView.setText(textView.getText());
            }
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
