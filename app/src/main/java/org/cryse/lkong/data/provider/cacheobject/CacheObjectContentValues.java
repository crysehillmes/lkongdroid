package org.cryse.lkong.data.provider.cacheobject;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code cache_object} table.
 */
public class CacheObjectContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return CacheObjectColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable CacheObjectSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable CacheObjectSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * The key of cache object, unique and indexed.
     */
    public CacheObjectContentValues putCacheKey(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("cacheKey must not be null");
        mContentValues.put(CacheObjectColumns.CACHE_KEY, value);
        return this;
    }


    /**
     * The value of cache, could be simple String or Json String.
     */
    public CacheObjectContentValues putCacheValue(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("cacheValue must not be null");
        mContentValues.put(CacheObjectColumns.CACHE_VALUE, value);
        return this;
    }


    /**
     * The create time of cache, nullable.
     */
    public CacheObjectContentValues putCacheTimeCreate(@Nullable Long value) {
        mContentValues.put(CacheObjectColumns.CACHE_TIME_CREATE, value);
        return this;
    }

    public CacheObjectContentValues putCacheTimeCreateNull() {
        mContentValues.putNull(CacheObjectColumns.CACHE_TIME_CREATE);
        return this;
    }

    /**
     * The expire time of cache, nullable.
     */
    public CacheObjectContentValues putCacheTimeExpire(@Nullable Long value) {
        mContentValues.put(CacheObjectColumns.CACHE_TIME_EXPIRE, value);
        return this;
    }

    public CacheObjectContentValues putCacheTimeExpireNull() {
        mContentValues.putNull(CacheObjectColumns.CACHE_TIME_EXPIRE);
        return this;
    }
}
