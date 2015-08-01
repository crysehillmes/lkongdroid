package org.cryse.lkong.data.provider.cacheobject;

import java.util.Date;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.lkong.data.provider.base.AbstractCursor;

/**
 * Cursor wrapper for the {@code cache_object} table.
 */
public class CacheObjectCursor extends AbstractCursor implements CacheObjectModel {
    public CacheObjectCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(CacheObjectColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The key of cache object, unique and indexed.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getCacheKey() {
        String res = getStringOrNull(CacheObjectColumns.CACHE_KEY);
        if (res == null)
            throw new NullPointerException("The value of 'cache_key' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The value of cache, could be simple String or Json String.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getCacheValue() {
        String res = getStringOrNull(CacheObjectColumns.CACHE_VALUE);
        if (res == null)
            throw new NullPointerException("The value of 'cache_value' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * The create time of cache, nullable.
     * Can be {@code null}.
     */
    @Nullable
    public Long getCacheTimeCreate() {
        Long res = getLongOrNull(CacheObjectColumns.CACHE_TIME_CREATE);
        return res;
    }

    /**
     * The expire time of cache, nullable.
     * Can be {@code null}.
     */
    @Nullable
    public Long getCacheTimeExpire() {
        Long res = getLongOrNull(CacheObjectColumns.CACHE_TIME_EXPIRE);
        return res;
    }
}
