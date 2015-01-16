package org.cryse.lkong.logic.restservice.model;

import java.util.List;

public class LKForumNameList {
    private List<LKForumListItem> forumlist;

    public LKForumNameList() {
    }

    public LKForumNameList(List<LKForumListItem> forumlist) {
        this.forumlist = forumlist;
    }

    public List<LKForumListItem> getForumlist() {
        return forumlist;
    }

    public void setForumlist(List<LKForumListItem> forumlist) {
        this.forumlist = forumlist;
    }
}
