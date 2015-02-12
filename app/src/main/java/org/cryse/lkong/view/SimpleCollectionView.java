package org.cryse.lkong.view;

import java.util.List;

public interface SimpleCollectionView<ItemType> extends ContentViewEx {
    public void showSimpleData(List<ItemType> items, boolean loadMore);
}

