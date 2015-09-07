package org.cryse.lkong.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.Target;

public class FitSizeTransformation extends BitmapTransformation {
    private int mMaxWidth = Target.SIZE_ORIGINAL;
    private int mMaxHeight = Target.SIZE_ORIGINAL;
    public FitSizeTransformation(Context context, int maxWidth, int maxHeight) {
        super(context);
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
    }

    public FitSizeTransformation(BitmapPool bitmapPool, int maxWidth, int maxHeight) {
        super(bitmapPool);
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        boolean partOne = mMaxWidth > 0 && mMaxWidth != Target.SIZE_ORIGINAL && mMaxWidth < toTransform.getWidth();
        boolean partTwo = mMaxHeight > 0 && mMaxHeight != Target.SIZE_ORIGINAL && mMaxHeight < toTransform.getHeight();
        if((partOne) || (partTwo)) {
            return TransformationUtils.fitSizeScale(pool, toTransform, mMaxWidth, mMaxHeight);
        } else {
            return toTransform;
        }
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
