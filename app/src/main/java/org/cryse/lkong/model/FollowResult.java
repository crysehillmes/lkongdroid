package org.cryse.lkong.model;

public class FollowResult {
    public static final int ACTION_FOLLOW = 1;
    public static final int ACTION_UNFOLLOW = 2;
    public static final int TYPE_FORUM = 111;
    public static final int TYPE_THREAD = 112;
    public static final int TYPE_USER = 113;
    public static final int TYPE_BLACKLIST = 114;

    private int action;
    private int type;
    private long id;
    private boolean ok;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
