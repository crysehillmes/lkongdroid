package org.cryse.lkong.logic.restservice.model;

public class ForumListItem {
    private long fid;
    private String name;

    public ForumListItem() {
    }

    public ForumListItem(long fid, String name) {
        this.fid = fid;
        this.name = name;
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
