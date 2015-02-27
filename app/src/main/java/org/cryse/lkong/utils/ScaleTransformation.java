package org.cryse.lkong.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.squareup.picasso.Transformation;

public class ScaleTransformation implements Transformation {
    private int mMaxWidth;
    private int mMaxHeight;
    private int mBgColor;
    public ScaleTransformation(int maxWidth, int maxHeight, int bgColor) {
        this.mMaxWidth = maxWidth;
        this.mMaxHeight = maxHeight;
        this.mBgColor = bgColor;
    }
    @Override
    public Bitmap transform(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();
        Log.d("BITMAP_TRANSFORMATION", String.format("source width %d, source height %d", source.getWidth(), source.getHeight()));
        Matrix m = new Matrix();
        float xScale = ((float) mMaxWidth) / width;
        float yScale = ((float) mMaxHeight) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        m.postScale(scale, scale);
        Bitmap transformed = Bitmap.createBitmap(source, 0, 0, width, height, m, true);
        source.recycle();
        Bitmap result = Bitmap.createBitmap(mMaxWidth, mMaxHeight, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        int left = mMaxWidth / 2 - transformed.getWidth() / 2;
        int top = mMaxHeight / 2 - transformed.getHeight() / 2;
        canvas.drawBitmap(transformed, left, top, new Paint(Paint.ANTI_ALIAS_FLAG));
        transformed.recycle();
        return result;
    }

    @Override
    public String key() {
        return "circle";
    }
}