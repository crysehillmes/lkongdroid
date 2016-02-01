package org.cryse.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class SimpleRecyclerViewAdapter<ItemType> extends RecyclerView.Adapter<RecyclerViewHolder> implements RecyclerViewAdapter<ItemType> {
    public static final int DEFAULT_ITEM_CAPACITY = 20;
    protected Context mContext;
    protected List<ItemType> mItemList;
    protected RecyclerViewOnItemClickListener mOnItemClickListener;
    protected RecyclerViewOnItemLongClickListener mOnItemLongClickListener;

    public SimpleRecyclerViewAdapter(Context context, List<ItemType> items) {
        this.mContext = context;
        this.mItemList = items;
    }

    public void addAll(Collection<ItemType> items) {
        addAll(mItemList.size(), items);
    }

    public void addAll(int position, Collection<ItemType> items) {
        mItemList.addAll(position, items);
        notifyItemRangeInserted(position, items.size());
    }

    public void add(ItemType item) {
        add(mItemList.size(), item);
    }

    public void add(int position, ItemType item) {
        mItemList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if(position >= 0 && position < mItemList.size()) {
            mItemList.remove(position);
            notifyItemRemoved(position);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void rangeRemove(int start, int end) {
        if(end > start && start >= 0 && end <= getItemCount()) {
            mItemList.subList(start, end).clear();
            notifyItemRangeRemoved(start, end - start);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void replaceWith(Collection<ItemType> items) {
        int currentSize = mItemList.size();
        int newSize = mItemList.size();
        int count = Math.max(currentSize, newSize);
        mItemList.clear();
        mItemList.addAll(items);
        notifyItemRangeRemoved(0, count);
    }

    public void clear() {
        int itemCount = mItemList.size();
        mItemList.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public ItemType getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public ArrayList<ItemType> getItemArrayList() {
        return (ArrayList<ItemType>) mItemList;
    }

    public void setOnItemClickListener(RecyclerViewOnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            int position = (int)view.getTag();
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(view, position, getItemId(position));
            }
        }
    };

    View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            int position = (int)view.getTag();
            if (mOnItemLongClickListener != null) {
                return mOnItemLongClickListener.onItemLongClick(view, position, getItemId(position));
            }
            return false;
        }
    };

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        if(holder.getOnClickView() != null) {
            holder.getOnClickView().setTag(position);
            holder.getOnClickView().setOnClickListener(mOnClickListener);
            holder.getOnClickView().setOnLongClickListener(mOnLongClickListener);
        }
    }

    public RecyclerView.Adapter adapter() {
        return this;
    }
}
