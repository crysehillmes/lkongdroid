package org.cryse.lkong.view;


public interface BrowseHistoryView<ItemType> extends SimpleCollectionView<ItemType>, CheckNoticeCountView {
    void onClearBrowseHistory();
}
