package org.cryse.widget.recyclerview;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;

public interface RecyclerViewAdapter<ItemType> {
    void addAll(Collection<ItemType> items);

    void addAll(int position, Collection<ItemType> items);

    void add(ItemType item) ;

    void add(int position, ItemType item);

    void remove(int position);

    void replaceWith(Collection<ItemType> items);

    void clear();

    ItemType getItem(int position);

    int getItemCount();

    void setOnItemClickListener(RecyclerViewOnItemClickListener listener);

    void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener listener);

    RecyclerView.Adapter adapter();

    ArrayList<ItemType> getItemArrayList();
}
