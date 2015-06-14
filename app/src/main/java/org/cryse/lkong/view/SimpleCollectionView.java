package org.cryse.lkong.view;

import java.util.List;

public interface SimpleCollectionView<ItemType> extends ContentViewEx {
    void showSimpleData(List<ItemType> items, boolean loadMore);
}

