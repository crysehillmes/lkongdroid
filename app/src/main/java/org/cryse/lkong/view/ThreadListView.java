package org.cryse.lkong.view;

import org.cryse.lkong.model.ForumThreadModel;

import java.util.List;

public interface ThreadListView extends ContentViewEx {
    public void showThreadList(List<ForumThreadModel> threadList, boolean isLoadMore);
}
