package org.cryse.lkong.modules.search;

import org.cryse.lkong.model.SearchDataSet;
import org.cryse.lkong.modules.base.ContentViewEx;

public interface SearchForumView extends ContentViewEx {
    void onSearchDone(SearchDataSet dataSet, boolean isLoadingMore);
    void onSearchFailed(int errorCode, Throwable throwable);
}
