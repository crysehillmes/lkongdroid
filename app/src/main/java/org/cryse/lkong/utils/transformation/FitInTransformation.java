package org.cryse.lkong.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.target.Target;

public class FitInTransformation extends BitmapTransformation {
    private int mMaxWidth = Target.SIZE_ORIGINAL;
    private int mMaxHeight = Target.SIZE_ORIGINAL;
    private int mBackground = Color.BLACK;
    public FitInTransformation(Context context, int maxWidth, int maxHeight, int backgroundColor) {
        super(context);
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
        this.mBackground = backgroundColor;
    }

    public FitInTransformation(BitmapPool bitmapPool, int maxWidth, int maxHeight, int backgroundColor) {
        super(bitmapPool);
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
        this.mBackground = backgroundColor;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        boolean partOne = mMaxWidth > 0 && mMaxWidth != Target.SIZE_ORIGINAL && mMaxWidth < toTransform.getWidth();
        boolean partTwo = mMaxHeight > 0 && mMaxHeight != Target.SIZE_ORIGINAL && mMaxHeight < toTransform.getHeight();
        if((partOne) || (partTwo)) {
            return TransformationUtils.fitInScale(pool, toTransform, mMaxWidth, mMaxHeight, mBackground);
        } else {
            return toTransform;
        }
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
