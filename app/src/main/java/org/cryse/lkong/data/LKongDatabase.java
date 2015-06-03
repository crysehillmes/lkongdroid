package org.cryse.lkong.data;

import org.cryse.lkong.data.model.PinnedForumEntity;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.model.ForumModel;

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
    void removePinnedForum(long fid) throws Exception;
    boolean isForumPinned(long fid) throws Exception;
    List<PinnedForumEntity> loadAllPinnedForums() throws Exception;
}
