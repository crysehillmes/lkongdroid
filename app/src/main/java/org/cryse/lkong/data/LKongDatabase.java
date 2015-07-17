package org.cryse.lkong.data;

import org.cryse.lkong.data.model.PinnedForumEntity;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.PunchResult;

import java.util.List;

public interface LKongDatabase {
    void initialize() throws Exception;
    void close() throws Exception;
    boolean isOpen() throws Exception;

    void addUserAccount(UserAccountEntity userAccountEntity) throws Exception;
    void updateUserAccount(UserAccountEntity userAccountEntity) throws Exception;
    UserAccountEntity getUserAccount(long uid) throws Exception;
    List<UserAccountEntity> getAllUserAccounts() throws Exception;
    boolean isUserAccountExist(long uid) throws Exception;
    void removeUserAccount(long uid) throws Exception;

    void cacheForumList(List<ForumModel> forumModels) throws Exception;
    List<ForumModel> getCachedForumList() throws Exception;
    void removeCachedForumList() throws Exception;
    boolean isCachedForumList() throws Exception;

    void pinForum(PinnedForumEntity pinnedForumEntity) throws Exception;
    void removePinnedForum(long uid, long fid) throws Exception;
    void removePinnedForums(long uid) throws Exception;
    boolean isForumPinned(long uid, long fid) throws Exception;
    List<PinnedForumEntity> loadAllForUser(long uid) throws Exception;
    List<PinnedForumEntity> loadAllPinnedForums() throws Exception;

    void cachePunchResult(PunchResult punchResult);
    void removePunchResult(long uid);
    PunchResult getCachePunchResult(long uid);

    void cacheNoticeCount(long uid, NoticeCountModel noticeCountModel);
    void removeNoticeCount(long uid);
    NoticeCountModel loadNoticeCount(long uid);
}
