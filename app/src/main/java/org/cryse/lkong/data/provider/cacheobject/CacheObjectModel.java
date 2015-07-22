package org.cryse.lkong.data.provider.cacheobject;

import org.cryse.lkong.data.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Cache any kind of object here.
 */
public interface CacheObjectModel extends BaseModel {

    /**
     * The key of cache object, unique and indexed.
     * Cannot be {@code null}.
     */
    @NonNull
    String getCacheKey();

    /**
     * The value of cache, could be simple String or Json String.
     * Cannot be {@code null}.
     */
    @NonNull
    String getCacheValue();

    /**
     * The create time of cache, nullable.
     * Can be {@code null}.
     */
    @Nullable
    Long getCacheTimeCreate();

    /**
     * The expire time of cache, nullable.
     * Can be {@code null}.
     */
    @Nullable
    Long getCacheTimeExpire();
}
