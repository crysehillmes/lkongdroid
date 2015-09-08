package org.cryse.lkong.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import org.cryse.lkong.utils.transformation.TransformationUtils;

public class CircleTransform extends BitmapTransformation {
    public CircleTransform(Context context) {
        super(context);
    }

    @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return TransformationUtils.circleCrop(pool, toTransform);
    }

    @Override public String getId() {
        return getClass().getName();
    }
}