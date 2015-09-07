package org.cryse.lkong.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

public class FitWidthTransformation extends BitmapTransformation {
    private int mMaxWidth = Integer.MAX_VALUE;
    public FitWidthTransformation(Context context, int maxWidth) {
        super(context);
        this.mMaxWidth = maxWidth;
    }

    public FitWidthTransformation(BitmapPool bitmapPool, int maxWidth) {
        super(bitmapPool);
        this.mMaxWidth = maxWidth;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        if(mMaxWidth > 0 && mMaxWidth != Integer.MAX_VALUE && mMaxWidth < toTransform.getWidth()) {
            return TransformationUtils.fitWidthScale(pool, toTransform, mMaxWidth);
        } else {
            return toTransform;
        }
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
