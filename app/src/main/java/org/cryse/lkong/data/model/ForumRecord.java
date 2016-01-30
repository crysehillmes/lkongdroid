package org.cryse.lkong.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ForumRecord extends RealmObject {
    @PrimaryKey
    private String key;
    private int ordinal;
    private long forumid;
    private long userid;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public long getForumid() {
        return forumid;
    }

    public void setForumid(long forumid) {
        this.forumid = forumid;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }
}
