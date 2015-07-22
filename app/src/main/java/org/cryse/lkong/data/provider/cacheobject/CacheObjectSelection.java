package org.cryse.lkong.data.provider.cacheobject;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import org.cryse.lkong.data.provider.base.AbstractSelection;

/**
 * Selection for the {@code cache_object} table.
 */
public class CacheObjectSelection extends AbstractSelection<CacheObjectSelection> {
    @Override
    protected Uri baseUri() {
        return CacheObjectColumns.CONTENT_URI;
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param contentResolver The content resolver to query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code CacheObjectCursor} object, which is positioned before the first entry, or null.
     */
    public CacheObjectCursor query(ContentResolver contentResolver, String[] projection) {
        Cursor cursor = contentResolver.query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new CacheObjectCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(contentResolver, null)}.
     */
    public CacheObjectCursor query(ContentResolver contentResolver) {
        return query(contentResolver, null);
    }

    /**
     * Query the given content resolver using this selection.
     *
     * @param context The context to use for the query.
     * @param projection A list of which columns to return. Passing null will return all columns, which is inefficient.
     * @return A {@code CacheObjectCursor} object, which is positioned before the first entry, or null.
     */
    public CacheObjectCursor query(Context context, String[] projection) {
        Cursor cursor = context.getContentResolver().query(uri(), projection, sel(), args(), order());
        if (cursor == null) return null;
        return new CacheObjectCursor(cursor);
    }

    /**
     * Equivalent of calling {@code query(context, null)}.
     */
    public CacheObjectCursor query(Context context) {
        return query(context, null);
    }


    public CacheObjectSelection id(long... value) {
        addEquals("cache_object." + CacheObjectColumns._ID, toObjectArray(value));
        return this;
    }

    public CacheObjectSelection idNot(long... value) {
        addNotEquals("cache_object." + CacheObjectColumns._ID, toObjectArray(value));
        return this;
    }

    public CacheObjectSelection orderById(boolean desc) {
        orderBy("cache_object." + CacheObjectColumns._ID, desc);
        return this;
    }

    public CacheObjectSelection orderById() {
        return orderById(false);
    }

    public CacheObjectSelection cacheKey(String... value) {
        addEquals(CacheObjectColumns.CACHE_KEY, value);
        return this;
    }

    public CacheObjectSelection cacheKeyNot(String... value) {
        addNotEquals(CacheObjectColumns.CACHE_KEY, value);
        return this;
    }

    public CacheObjectSelection cacheKeyLike(String... value) {
        addLike(CacheObjectColumns.CACHE_KEY, value);
        return this;
    }

    public CacheObjectSelection cacheKeyContains(String... value) {
        addContains(CacheObjectColumns.CACHE_KEY, value);
        return this;
    }

    public CacheObjectSelection cacheKeyStartsWith(String... value) {
        addStartsWith(CacheObjectColumns.CACHE_KEY, value);
        return this;
    }

    public CacheObjectSelection cacheKeyEndsWith(String... value) {
        addEndsWith(CacheObjectColumns.CACHE_KEY, value);
        return this;
    }

    public CacheObjectSelection orderByCacheKey(boolean desc) {
        orderBy(CacheObjectColumns.CACHE_KEY, desc);
        return this;
    }

    public CacheObjectSelection orderByCacheKey() {
        orderBy(CacheObjectColumns.CACHE_KEY, false);
        return this;
    }

    public CacheObjectSelection cacheValue(String... value) {
        addEquals(CacheObjectColumns.CACHE_VALUE, value);
        return this;
    }

    public CacheObjectSelection cacheValueNot(String... value) {
        addNotEquals(CacheObjectColumns.CACHE_VALUE, value);
        return this;
    }

    public CacheObjectSelection cacheValueLike(String... value) {
        addLike(CacheObjectColumns.CACHE_VALUE, value);
        return this;
    }

    public CacheObjectSelection cacheValueContains(String... value) {
        addContains(CacheObjectColumns.CACHE_VALUE, value);
        return this;
    }

    public CacheObjectSelection cacheValueStartsWith(String... value) {
        addStartsWith(CacheObjectColumns.CACHE_VALUE, value);
        return this;
    }

    public CacheObjectSelection cacheValueEndsWith(String... value) {
        addEndsWith(CacheObjectColumns.CACHE_VALUE, value);
        return this;
    }

    public CacheObjectSelection orderByCacheValue(boolean desc) {
        orderBy(CacheObjectColumns.CACHE_VALUE, desc);
        return this;
    }

    public CacheObjectSelection orderByCacheValue() {
        orderBy(CacheObjectColumns.CACHE_VALUE, false);
        return this;
    }

    public CacheObjectSelection cacheTimeCreate(Long... value) {
        addEquals(CacheObjectColumns.CACHE_TIME_CREATE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeCreateNot(Long... value) {
        addNotEquals(CacheObjectColumns.CACHE_TIME_CREATE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeCreateGt(long value) {
        addGreaterThan(CacheObjectColumns.CACHE_TIME_CREATE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeCreateGtEq(long value) {
        addGreaterThanOrEquals(CacheObjectColumns.CACHE_TIME_CREATE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeCreateLt(long value) {
        addLessThan(CacheObjectColumns.CACHE_TIME_CREATE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeCreateLtEq(long value) {
        addLessThanOrEquals(CacheObjectColumns.CACHE_TIME_CREATE, value);
        return this;
    }

    public CacheObjectSelection orderByCacheTimeCreate(boolean desc) {
        orderBy(CacheObjectColumns.CACHE_TIME_CREATE, desc);
        return this;
    }

    public CacheObjectSelection orderByCacheTimeCreate() {
        orderBy(CacheObjectColumns.CACHE_TIME_CREATE, false);
        return this;
    }

    public CacheObjectSelection cacheTimeExpire(Long... value) {
        addEquals(CacheObjectColumns.CACHE_TIME_EXPIRE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeExpireNot(Long... value) {
        addNotEquals(CacheObjectColumns.CACHE_TIME_EXPIRE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeExpireGt(long value) {
        addGreaterThan(CacheObjectColumns.CACHE_TIME_EXPIRE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeExpireGtEq(long value) {
        addGreaterThanOrEquals(CacheObjectColumns.CACHE_TIME_EXPIRE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeExpireLt(long value) {
        addLessThan(CacheObjectColumns.CACHE_TIME_EXPIRE, value);
        return this;
    }

    public CacheObjectSelection cacheTimeExpireLtEq(long value) {
        addLessThanOrEquals(CacheObjectColumns.CACHE_TIME_EXPIRE, value);
        return this;
    }

    public CacheObjectSelection orderByCacheTimeExpire(boolean desc) {
        orderBy(CacheObjectColumns.CACHE_TIME_EXPIRE, desc);
        return this;
    }

    public CacheObjectSelection orderByCacheTimeExpire() {
        orderBy(CacheObjectColumns.CACHE_TIME_EXPIRE, false);
        return this;
    }
}
