package org.cryse.lkong.logic.restservice.model;

import java.util.List;

public class ForumNameList {
    private List<ForumListItem> forumlist;

    public ForumNameList() {
    }

    public ForumNameList(List<ForumListItem> forumlist) {
        this.forumlist = forumlist;
    }

    public List<ForumListItem> getForumlist() {
        return forumlist;
    }

    public void setForumlist(List<ForumListItem> forumlist) {
        this.forumlist = forumlist;
    }
}
