package org.cryse.lkong.utils.transformation;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Build;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;

public class TransformationUtils {
    private static final String TAG = TransformationUtils.class.getSimpleName();
    public static final int PAINT_FLAGS = Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG;

    public static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);
        Bitmap.Config config = getSafeConfig(source);
        Bitmap result = pool.get(size, size, config);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, config);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);
        return result;
    }

    public static Bitmap fitWidthScale(BitmapPool pool, Bitmap toFit, int width) {
        if (toFit.getWidth() <= width) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "requested target size matches input, returning input");
            }
            return toFit;
        }
        final float widthPercentage = width / (float) toFit.getWidth();

        // take the floor of the target width/height, not round. If the matrix
        // passed into drawBitmap rounds differently, we want to slightly
        // overdraw, not underdraw, to avoid artifacts from bitmap reuse.
        final int targetWidth = (int) (widthPercentage * toFit.getWidth());
        final int targetHeight = (int) (widthPercentage * toFit.getHeight());

        if (toFit.getWidth() == targetWidth && toFit.getHeight() == targetHeight) {
            if (Log.isLoggable(TAG, Log.VERBOSE)) {
                Log.v(TAG, "adjusted target size matches input, returning input");
            }
            return toFit;
        }

        Bitmap.Config config = getSafeConfig(toFit);
        Bitmap toReuse = pool.get(targetWidth, targetHeight, config);
        if (toReuse == null) {
            toReuse = Bitmap.createBitmap(targetWidth, targetHeight, config);
        }
        // We don't add or remove alpha, so keep the alpha setting of the Bitmap we were given.
        setAlpha(toFit, toReuse);

        if (Log.isLoggable(TAG, Log.VERBOSE)) {
            Log.v(TAG, "request: " + width);
            Log.v(TAG, "toFit:   " + toFit.getWidth() + "x" + toFit.getHeight());
            Log.v(TAG, "toReuse: " + toReuse.getWidth() + "x" + toReuse.getHeight());
            Log.v(TAG, "minPct:   " + widthPercentage);
        }

        Canvas canvas = new Canvas(toReuse);
        Matrix matrix = new Matrix();
        matrix.setScale(widthPercentage, widthPercentage);
        Paint paint = new Paint(PAINT_FLAGS);
        canvas.drawBitmap(toFit, matrix, paint);

        return toReuse;
    }

    private static Bitmap.Config getSafeConfig(Bitmap bitmap) {
        return bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static void setAlpha(Bitmap toTransform, Bitmap outBitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1 && outBitmap != null) {
            outBitmap.setHasAlpha(toTransform.hasAlpha());
        }
    }
}
