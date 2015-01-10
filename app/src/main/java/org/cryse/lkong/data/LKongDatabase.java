package org.cryse.lkong.data;

import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.UserInfoModel;

import java.util.List;

public interface LKongDatabase {
    public void initialize() throws Exception;
    public void close() throws Exception;
    public boolean isOpen() throws Exception;
    public void cacheUserInfo(UserInfoModel userInfoModel) throws Exception;
    public UserInfoModel getCachedUserInfo() throws Exception;
    public boolean isCachedUserInfo() throws Exception;
    public void removeCachedUserInfo() throws Exception;
    public void cacheForumList(List<ForumModel> forumModels) throws Exception;
    public List<ForumModel> getCachedForumList() throws Exception;
    public void removeCachedForumList() throws Exception;
    public boolean isCachedForumList() throws Exception;
}
