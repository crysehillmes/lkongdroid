package org.cryse.lkong.model.converter;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.logic.restservice.model.LKForumThreadItem;
import org.cryse.lkong.logic.restservice.model.LKForumThreadList;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.ForumThreadModel;
import org.cryse.lkong.model.UserInfoModel;

import java.util.ArrayList;
import java.util.List;

public class ModelConverter {
    public static UserInfoModel toUserInfoModel(LKUserInfo lkUserInfo) {
        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setThreads(lkUserInfo.getThreads());
        userInfoModel.setBlacklists(lkUserInfo.getBlacklists());
        userInfoModel.setCustomStatus(lkUserInfo.getCustomstatus());
        userInfoModel.setDigestPosts(lkUserInfo.getDigestposts());
        userInfoModel.setEmail(lkUserInfo.getEmail());
        userInfoModel.setFansCount(lkUserInfo.getFansnum());
        userInfoModel.setFollowCount(lkUserInfo.getFollowuidnum());
        userInfoModel.setGender(lkUserInfo.getGender());
        userInfoModel.setPhoneNum(lkUserInfo.getPhonenum());
        userInfoModel.setMe(lkUserInfo.getMe());
        userInfoModel.setPosts(lkUserInfo.getPosts());
        userInfoModel.setUid(lkUserInfo.getUid());
        userInfoModel.setUserName(lkUserInfo.getUsername());
        userInfoModel.setRegDate(lkUserInfo.getRegdate());
        userInfoModel.setUserIcon(uidToAvatarUrl(lkUserInfo.getUid()));
        return userInfoModel;
    }

    public static List<ForumThreadModel> toForumThreadModel(LKForumThreadList lkForumThreadList) {
        List<ForumThreadModel> threadList = new ArrayList<ForumThreadModel>();
        for(LKForumThreadItem item : lkForumThreadList.getData()) {
            ForumThreadModel threadModel = new ForumThreadModel();
            threadModel.setSortKey(item.getSortkey());
            threadModel.setUserName(item.getUsername());
            threadModel.setUid(item.getUid());
            threadModel.setClosed(item.getClosed());
            threadModel.setDateline(item.getDateline());
            threadModel.setDigest(item.getDigest());
            threadModel.setFid(item.getFid());
            threadModel.setId(item.getId());
            threadModel.setReplyCount(item.getReplynum());
            threadModel.setSubject(item.getSubject());
            threadList.add(threadModel);
        }
        return threadList;
    }

    public static String uidToAvatarUrl(long uid) {
        String uidString = String.format("%1$06d", uid);
        String avatarUrl = String.format("http://img.lkong.cn/avatar/000/%s/%s/%s_avatar_middle.jpg",
                    uidString.substring(0,2),
                    uidString.substring(2,4),
                    uidString.substring(4,6)
            );
        return avatarUrl;
    }

    public static String fidToForumIconUrl(long fid) {
        String fidString = String.format("%1$06d", fid);
        String iconUrl = String.format("http://img.lkong.cn/forumavatar/000/%s/%s/%s_avatar_middle.jpg",
                fidString.substring(0, 2),
                fidString.substring(2, 4),
                fidString.substring(4, 6)
        );
        return iconUrl;
    }
}
