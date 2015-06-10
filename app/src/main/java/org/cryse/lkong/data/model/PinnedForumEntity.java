package org.cryse.lkong.data.model;

public class PinnedForumEntity {
    private long forumId;
    private long userId;
    private String forumName;
    private String forumIcon;

    public PinnedForumEntity(long forumId, long userId, String forumName, String forumIcon, long sortValue) {
        this.forumId = forumId;
        this.userId = userId;
        this.forumName = forumName;
        this.forumIcon = forumIcon;
        this.sortValue = sortValue;
    }

    private long sortValue;

    public long getForumId() {
        return forumId;
    }

    public void setForumId(long forumId) {
        this.forumId = forumId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getForumName() {
        return forumName;
    }

    public void setForumName(String forumName) {
        this.forumName = forumName;
    }

    public String getForumIcon() {
        return forumIcon;
    }

    public void setForumIcon(String forumIcon) {
        this.forumIcon = forumIcon;
    }

    public long getSortValue() {
        return sortValue;
    }

    public void setSortValue(long sortValue) {
        this.sortValue = sortValue;
    }
}
