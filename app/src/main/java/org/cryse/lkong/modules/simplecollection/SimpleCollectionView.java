package org.cryse.lkong.modules.simplecollection;

import org.cryse.lkong.modules.base.ContentViewEx;

import java.util.List;

public interface SimpleCollectionView<ItemType> extends ContentViewEx {
    void showSimpleData(List<ItemType> items, boolean loadMore);
}

