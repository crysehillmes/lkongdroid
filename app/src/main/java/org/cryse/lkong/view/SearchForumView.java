package org.cryse.lkong.view;

import org.cryse.lkong.model.SearchDataSet;

public interface SearchForumView extends ContentViewEx {
    void onSearchDone(SearchDataSet dataSet, boolean isLoadingMore);
    void onSearchFailed(int errorCode, Throwable throwable);
}
