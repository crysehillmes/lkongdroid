package org.cryse.lkong.data.provider.browsehistory;

import org.cryse.lkong.data.provider.base.BaseModel;

import java.util.Date;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Store User browse history here.
 */
public interface BrowseHistoryModel extends BaseModel {

    /**
     * The history record user id, nullable.
     */
    long getUserId();

    /**
     * The history record forum id, nullable.
     * Can be {@code null}.
     */
    @Nullable
    Long getForumId();

    /**
     * The history record forum title, nullable.
     * Can be {@code null}.
     */
    @Nullable
    String getForumTitle();

    /**
     * The history record thread id, not null.
     */
    long getThreadId();

    /**
     * The history record post id, nullable.
     * Can be {@code null}.
     */
    @Nullable
    Long getPostId();

    /**
     * The history record thread id, not null.
     * Cannot be {@code null}.
     */
    @NonNull
    String getThreadTitle();

    /**
     * The history record author id, nullable.
     */
    long getThreadAuthorId();

    /**
     * The history record author name, nullable.
     * Cannot be {@code null}.
     */
    @NonNull
    String getThreadAuthorName();

    /**
     * The history record read timestamp, nullable.
     */
    long getLastReadTime();
}
