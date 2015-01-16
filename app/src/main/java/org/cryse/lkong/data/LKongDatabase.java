package org.cryse.lkong.data;

import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.UserInfoModel;

import java.util.List;

public interface LKongDatabase {
    public void initialize() throws Exception;
    public void close() throws Exception;
    public boolean isOpen() throws Exception;

    public void addUserAccount(UserAccountEntity userAccountEntity) throws Exception;
    public void updateUserAccount(UserAccountEntity userAccountEntity) throws Exception;
    public UserAccountEntity getUserAccount(long uid) throws Exception;
    public List<UserAccountEntity> getAllUserAccounts() throws Exception;
    public boolean isUserAccountExist(long uid) throws Exception;
    public void removeUserAccount(long uid) throws Exception;

    public void cacheForumList(List<ForumModel> forumModels) throws Exception;
    public List<ForumModel> getCachedForumList() throws Exception;
    public void removeCachedForumList() throws Exception;
    public boolean isCachedForumList() throws Exception;
}
