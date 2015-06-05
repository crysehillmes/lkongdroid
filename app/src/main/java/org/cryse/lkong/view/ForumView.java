package org.cryse.lkong.view;

import org.cryse.lkong.model.ThreadModel;

import java.util.List;

public interface ForumView extends ContentViewEx {
    void showThreadList(List<ThreadModel> threadList, boolean isLoadMore);
}
