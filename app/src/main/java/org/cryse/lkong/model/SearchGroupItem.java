package org.cryse.lkong.model;

public class SearchGroupItem extends AbstractSearchResult {
    private String iconUrl;
    private CharSequence groupDescription; // description;
    private long forumId; // fid;
    private int fansCount; // fansnum;
    private CharSequence groupName; // name;
    private String id;

    public CharSequence getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(CharSequence groupDescription) {
        this.groupDescription = groupDescription;
    }

    public long getForumId() {
        return forumId;
    }

    public void setForumId(long forumId) {
        this.forumId = forumId;
    }

    public int getFansCount() {
        return fansCount;
    }

    public void setFansCount(int fansCount) {
        this.fansCount = fansCount;
    }

    public CharSequence getGroupName() {
        return groupName;
    }

    public void setGroupName(CharSequence groupName) {
        this.groupName = groupName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
