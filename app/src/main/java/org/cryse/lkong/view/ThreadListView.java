package org.cryse.lkong.view;

import org.cryse.lkong.model.ThreadModel;

import java.util.List;

public interface ThreadListView extends ContentViewEx {
    public void showThreadList(List<ThreadModel> threadList, boolean isLoadMore);
}
