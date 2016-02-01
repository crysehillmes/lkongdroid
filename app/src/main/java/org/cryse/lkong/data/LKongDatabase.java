package org.cryse.lkong.data;

import android.support.annotation.Nullable;

import org.cryse.lkong.data.model.FollowedForum;
import org.cryse.lkong.data.provider.browsehistory.BrowseHistoryModel;
import org.cryse.lkong.data.provider.followedforum.FollowedForumModel;
import org.cryse.lkong.model.BrowseHistory;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.PunchResult;

import java.util.List;

public interface LKongDatabase {
    void initialize() throws Exception;
    void close() throws Exception;
    boolean isOpen() throws Exception;

    void cachePunchResult(PunchResult punchResult);
    void removePunchResult(long uid);
    PunchResult getCachePunchResult(long uid);

    void cacheNoticeCount(long uid, NoticeCountModel noticeCountModel);
    void removeNoticeCount(long uid);
    NoticeCountModel loadNoticeCount(long uid);

    void saveBrowseHistory(long uid,
                           long threadId,
                           String threadTitle,
                           @Nullable Long forumId,
                           @Nullable String forumTitle,
                           @Nullable Long postId,
                           long authorId,
                           String authorName,
                           long lastReadTime
    );
    List<BrowseHistory> getBrowseHistory(long uid, int start);
    List<BrowseHistory> getBrowseHistory(int start);
    void clearBrowserHistory(long uid);
    void removeBrowserHistory(long uid, long threadId);
    void clearBrowserHistory();
}
