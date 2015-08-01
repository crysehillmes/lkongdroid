package org.cryse.lkong.data.provider.followedforum;

import org.cryse.lkong.data.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Followed forum.
 */
public interface FollowedForumModel extends BaseModel {

    /**
     * Owner id.
     */
    long getUserId();

    /**
     * Followed forum id.
     */
    long getForumId();

    /**
     * Forum name.
     * Cannot be {@code null}.
     */
    @NonNull
    String getForumName();

    /**
     * Forum icon url.
     * Cannot be {@code null}.
     */
    @NonNull
    String getForumIcon();

    /**
     * Sort value of forum.
     */
    long getForumSortValue();
}
