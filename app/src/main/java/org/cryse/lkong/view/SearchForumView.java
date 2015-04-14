package org.cryse.lkong.view;

import org.cryse.lkong.model.SearchDataSet;

public interface SearchForumView extends ContentView {
    public void onSearchDone(SearchDataSet dataSet);
    public void onSearchFailed(int errorCode, Throwable throwable);
}
