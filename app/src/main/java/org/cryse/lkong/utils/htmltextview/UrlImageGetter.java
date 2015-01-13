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
import android.os.AsyncTask;
import android.support.annotation.DrawableRes;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.cryse.lkong.R;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;

public class UrlImageGetter implements ImageGetter {
    Context mContext;
    TextView mTargetTextView;
    Picasso picasso;
    final Resources resources;
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
        this.resources = context.getResources();
    }

    public Drawable getDrawable(String source) {
        UrlDrawable urlDrawable = new UrlDrawable(mContext, mTargetTextView);

        picasso.load(source).placeholder(R.drawable.ic_default_avatar).error(R.drawable.ic_default_avatar).into(urlDrawable);

        // get the actual source
        // ImageGetterAsyncTask asyncTask = new ImageGetterAsyncTask(urlDrawable);

        // asyncTask.execute(source);

        // return reference to URLDrawable which will asynchronously load the image specified in the src tag
        return urlDrawable;
    }

    public static class UrlDrawable extends BitmapDrawable implements Target {
        protected Context mContext;
        protected Drawable mDrawable;
        protected WeakReference<TextView> mTargetView;
        public UrlDrawable(Context context, TextView targetTextView) {
            super(context.getResources(), (Bitmap)null);
            this.mContext = context;
            mTargetView = new WeakReference<TextView>(targetTextView);
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
            newDrawable.setBounds(0, 0, 0 + newDrawable.getIntrinsicWidth(), 0 + newDrawable.getIntrinsicHeight());
            this.mDrawable = newDrawable;
            this.setBounds(0, 0, 0 + newDrawable.getIntrinsicWidth(), 0 + newDrawable.getIntrinsicHeight());
            invalidateTargetTextView();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            this.mDrawable = errorDrawable;
            invalidateTargetTextView();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            this.mDrawable = placeHolderDrawable;
            invalidateTargetTextView();
        }

        private void invalidateTargetTextView() {
            TextView textView = mTargetView.get();
            if(textView != null) {
                Log.d("abc invalidateTargetTextView", "textView != null");
                textView.invalidate();
                textView.setText(textView.getText());
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

    }
} 
