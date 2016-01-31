package org.cryse.lkong.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class FollowRecord extends RealmObject {
    public static final int TYPE_FORUM = 0;
    public static final int TYPE_THREAD = 1;
    public static final int TYPE_USER = 2;
    public static final int TYPE_BLACKLIST = 3;

    @PrimaryKey
    private String key;
    private int type;
    private int ordinal;
    private long targetId;
    private long userid;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public long getTargetId() {
        return targetId;
    }

    public void setTargetId(long targetId) {
        this.targetId = targetId;
    }

    public long getUserid() {
        return userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }
}
