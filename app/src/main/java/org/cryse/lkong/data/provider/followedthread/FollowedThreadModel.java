package org.cryse.lkong.data.provider.followedthread;

import org.cryse.lkong.data.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Followed thread.
 */
public interface FollowedThreadModel extends BaseModel {

    /**
     * Owner id.
     */
    long getUserId();

    /**
     * Followed thread id.
     */
    long getThreadId();

    /**
     * Thread title.
     * Cannot be {@code null}.
     */
    @NonNull
    String getThreadTitle();

    /**
     * Thread author id.
     */
    long getThreadAuthorId();

    /**
     * Thread author name.
     * Cannot be {@code null}.
     */
    @NonNull
    String getThreadAuthorName();

    /**
     * Thread timestamp.
     */
    long getThreadTimestamp();

    /**
     * Thread timestamp.
     */
    int getThreadReplyCount();
}
