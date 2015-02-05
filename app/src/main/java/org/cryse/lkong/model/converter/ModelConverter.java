package org.cryse.lkong.model.converter;

import android.util.Log;

import org.cryse.lkong.logic.restservice.model.LKForumThreadItem;
import org.cryse.lkong.logic.restservice.model.LKForumThreadList;
import org.cryse.lkong.logic.restservice.model.LKPostItem;
import org.cryse.lkong.logic.restservice.model.LKPostList;
import org.cryse.lkong.logic.restservice.model.LKPostRateItem;
import org.cryse.lkong.logic.restservice.model.LKPostUser;
import org.cryse.lkong.logic.restservice.model.LKThreadInfo;
import org.cryse.lkong.logic.restservice.model.LKTimelineData;
import org.cryse.lkong.logic.restservice.model.LKTimelineItem;
import org.cryse.lkong.logic.restservice.model.LKUserInfo;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.utils.htmltextview.HtmlCleaner;
import org.jsoup.safety.Whitelist;

import java.util.ArrayList;
import java.util.Date;
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
        userInfoModel.setUserIcon(uidToAvatarUrl(lkUserInfo.getUid()));
        userInfoModel.setRegDate(lkUserInfo.getRegdate());
        return userInfoModel;
    }

    public static List<ThreadModel> toForumThreadModel(LKForumThreadList lkForumThreadList, boolean checkNextTimeSortKey) {
        List<ThreadModel> threadList = new ArrayList<ThreadModel>();
        ThreadModel nextSortKeyItem = null;
        for(LKForumThreadItem item : lkForumThreadList.getData()) {
            ThreadModel threadModel = new ThreadModel();
            threadModel.setSortKey(item.getSortkey());
            threadModel.setUserName(item.getUsername());
            threadModel.setUserIcon(uidToAvatarUrl(item.getUid()));
            threadModel.setUid(item.getUid());
            threadModel.setClosed(item.getClosed());
            threadModel.setDateline(item.getDateline());
            threadModel.setDigest(item.getDigest());
            threadModel.setFid(item.getFid());
            threadModel.setId(item.getId());
            threadModel.setReplyCount(item.getReplynum());
            threadModel.setSubject(item.getSubject());
            threadModel.setSortKeyTime(new Date(item.getSortkey() * 1000l));
            if(checkNextTimeSortKey && lkForumThreadList.getNexttime() == item.getSortkey())
                nextSortKeyItem = threadModel;
            else
                threadList.add(threadModel);
        }
        if(checkNextTimeSortKey && nextSortKeyItem != null) threadList.add(nextSortKeyItem);
        return threadList;
    }

    public static ThreadInfoModel toThreadInfoModel(LKThreadInfo lkThreadInfo) {
        ThreadInfoModel threadInfo = new ThreadInfoModel();
        threadInfo.setFid(lkThreadInfo.getFid());
        threadInfo.setTid(lkThreadInfo.getTid());
        threadInfo.setSubject(lkThreadInfo.getSubject());
        threadInfo.setViews(lkThreadInfo.getViews());
        threadInfo.setReplies(lkThreadInfo.getReplies());
        threadInfo.setForumName(lkThreadInfo.getForumname());
        threadInfo.setDigest(lkThreadInfo.isDigest());

        threadInfo.setTimeStamp(new Date(lkThreadInfo.getTimestamp()));
        threadInfo.setUid(lkThreadInfo.getUid());
        threadInfo.setUserName(lkThreadInfo.getUsername());
        threadInfo.setAuthorId(lkThreadInfo.getAuthorid());
        threadInfo.setAuthorName(lkThreadInfo.getAuthor());
        threadInfo.setDateline(lkThreadInfo.getDateline());
        threadInfo.setId(lkThreadInfo.getId());
        return threadInfo;
    }


    public static List<PostModel> toPostModelList(LKPostList lkPostList) {
        List<PostModel> itemList = new ArrayList<PostModel>();
        for(LKPostItem item : lkPostList.getData()) {
            PostModel postModel = new PostModel();
            postModel.setAdmin(item.getIsadmin() != 0);
            //postModel.setAuthor(item.getAuthor());
            postModel.setAuthorId(item.getAuthorid());
            postModel.setAuthorName(item.getAuthor());
            postModel.setFavorite(item.isFavorite());
            postModel.setDateline(item.getDateline());
            postModel.setFid(item.getFid());
            postModel.setFirst(item.getFirst() != 0);
            postModel.setId(item.getId());
            postModel.setMe(item.getIsme() != 0);
            // postModel.setMessage(item.getMessage());
            postModel.setNotGroup(item.getNotgroup() != 0);
            postModel.setOrdinal(item.getLou());
            postModel.setPid(Long.parseLong(item.getPid()));
            //postModel.setRateLog();
            postModel.setSortKey(item.getSortkey());
            postModel.setSortKeyTime(new Date(item.getSortkey() * 1000l));
            postModel.setStatus(item.getStatus());
            postModel.setTid(item.getTid());
            postModel.setTsAdmin(item.isTsadmin());

            if(item.getAlluser() != null) {
                LKPostUser itemUser = item.getAlluser();
                PostModel.PostAuthor author = new PostModel.PostAuthor(
                        itemUser.getAdminid(),
                        itemUser.getCustomstatus(),
                        itemUser.getGender(),
                        new Date(itemUser.getRegdate()),
                        itemUser.getUid(),
                        itemUser.getUsername(),
                        itemUser.isVerify(),
                        itemUser.getVerifymessage(),
                        itemUser.getColor(),
                        itemUser.getStars(),
                        itemUser.getRanktitle()
                );
                postModel.setAuthor(author);
            }

            if(item.getRatelog() != null) {
                List<LKPostRateItem> lkRateLog = item.getRatelog();
                List<PostModel.PostRate> rateList = new ArrayList<PostModel.PostRate>(lkRateLog.size());
                for(LKPostRateItem rateItem : lkRateLog) {
                    PostModel.PostRate newRate = new PostModel.PostRate(
                            rateItem.getDateline(),
                            rateItem.getExtcredits(),
                            rateItem.getPid(),
                            rateItem.getReason(),
                            rateItem.getScore(),
                            rateItem.getUid(),
                            rateItem.getUsername()
                    );
                    rateList.add(newRate);
                }
                postModel.setRateLog(rateList);
            }

            postModel.setMessage(
                    HtmlCleaner.fixTagBalanceAndRemoveEmpty(
                            item.getMessage(),
                            Whitelist.basicWithImages()
                                    .addTags("font")
                                    .addAttributes(":all","style", "color")
                    )
            );
            itemList.add(postModel);
        }
        return itemList;
    }

    public static List<TimelineModel> toTimelineModel(LKTimelineData timelineData) {
        List<TimelineModel> timelineModels = new ArrayList<>(timelineData.getData().size());
        for(LKTimelineItem item : timelineData.getData()) {
            TimelineModel model = new TimelineModel();
            model.setId(item.getId());
            model.setQuote(item.isIsquote());
            model.setUserId(Long.valueOf(item.getUid()));
            model.setUserName(item.getUsername());
            model.setDateline(new Date(Long.valueOf(item.getDateline())* 1000l));
            model.setMessage(item.getMessage());
            model.setThread(item.isIsthread());
            if(item.isIsthread()) {
                model.setTid(Long.valueOf(item.getId().substring(7)));
                model.setThreadReplyCount(item.getReplynum());
                model.setThreadAuthor(item.getUsername());
                model.setThreadAuthorId(Long.valueOf(item.getUid()));
            } else {
                model.setTid(Long.valueOf(item.getTid()));
                model.setThreadReplyCount(item.getT_replynum());
                model.setThreadAuthor(item.getT_author());
                model.setThreadAuthorId(item.getT_authorid());
            }
            model.setSubject(item.getSubject());
            model.setSortKey(item.getSortkey());
            model.setSortKeyDate(new Date(item.getSortkey() * 1000l));
            timelineModels.add(model);
        }
        return timelineModels;
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
