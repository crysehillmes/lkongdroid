package org.cryse.widget.recyclerview;

import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HeaderFootList<ItemType>  {
    private List<View> mHeaderViewList;
    private List<ItemType>  mItemList;
    private List<View> mFooterViewList;
    public static final int IN_HEADER_LIST = 0;
    public static final int IN_ITEM_LIST = 1;
    public static final int IN_FOOTER_LIST = 2;

    public HeaderFootList(List<ItemType> itemList) {
        initHeaderFooterList(itemList);
    }

    public void initHeaderFooterList(List<ItemType> itemList) {
        this.mHeaderViewList = new ArrayList<View>();
        this.mFooterViewList = new ArrayList<View>();
        this.mItemList = itemList;
    }

    public int indexIn(int position) {
        if(position >= 0 && position < mHeaderViewList.size()) {
            Log.d("HEADERFOOTERLIST", String.format("INDEX %d IN HEADER", position));
            return IN_HEADER_LIST;
        } else if(position >= mHeaderViewList.size() && position < mHeaderViewList.size() + mItemList.size()) {
            Log.d("HEADERFOOTERLIST", String.format("INDEX %d IN ITEM", position));
            return IN_ITEM_LIST;
        } else if(position >= mHeaderViewList.size() + mItemList.size() && position < mHeaderViewList.size() + mItemList.size() + mFooterViewList.size()) {
            Log.d("HEADERFOOTERLIST", String.format("INDEX %d IN FOOTER", position));
            return IN_FOOTER_LIST;
        } else {
            throw new IndexOutOfBoundsException(String.format("size() is %d, but position is %d", getAllItemCount(), position));
        }
    }

    public Object get(int position) {
        Log.d("HEADERFOOTERLIST", String.format("size() is %d, but position is %d; headerCount = %d, itemCount=%d, footerCount=%d",
                getAllItemCount(),
                position,
                mHeaderViewList.size(),
                mItemList.size(),
                mFooterViewList.size()
        ));
        if(position >= 0 && position < mHeaderViewList.size()) {
            return mHeaderViewList.get(position);
        } else if(position >= mHeaderViewList.size() && position < mHeaderViewList.size() + mItemList.size()) {
            return mItemList.get(position - mHeaderViewList.size());
        } else if(position >= mHeaderViewList.size() + mItemList.size() && position < mHeaderViewList.size() + mItemList.size() + mFooterViewList.size()) {
            return mFooterViewList.get(position - mHeaderViewList.size() - mItemList.size());
        } else {
            throw new IndexOutOfBoundsException(String.format("size() is %d, but position is %d; headerCount = %d, itemCount=%d, footerCount=%d",
                    getAllItemCount(),
                    position,
                    mHeaderViewList.size(),
                    mItemList.size(),
                    mFooterViewList.size()
            ));
        }
    }

    public int size() {
        return getAllItemCount();
    }

    public int getAllItemCount() {
        return mHeaderViewList.size() + mItemList.size() + mFooterViewList.size();
    }

    public int getItemCount() {
        return mItemList.size();
    }

    public int getHeaderViewCount() {
        return mHeaderViewList.size();
    }

    public int getFooterViewCount() {
        return mFooterViewList.size();
    }

    public List<View> getHeaderViewList() {
        return mHeaderViewList;
    }

    public void setHeaderViewList(List<View> headerViewList) {
        this.mHeaderViewList = headerViewList;
    }

    public List<ItemType> getItemList() {
        return mItemList;
    }

    public void setItemList(ArrayList<ItemType> itemList) {
        this.mItemList = itemList;
    }

    public List<View> getFooterViewList() {
        return mFooterViewList;
    }

    public void setFooterViewList(List<View> footerViewList) {
        this.mFooterViewList = footerViewList;
    }
}
