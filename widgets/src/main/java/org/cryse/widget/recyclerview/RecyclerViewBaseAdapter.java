package org.cryse.widget.recyclerview;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collection;
import java.util.List;

public abstract class RecyclerViewBaseAdapter<T extends RecyclerViewHolder, S> extends RecyclerView.Adapter<T> {
    public static final int LAST_POSITION = -1 ;
    protected Context mContext;
    protected List<S> mItemList;
    protected RecyclerViewOnItemClickListener mOnItemClickListener;
    protected RecyclerViewOnItemLongClickListener mOnItemLongClickListener;

    public RecyclerViewBaseAdapter(Context context, List<S> items) {
        this.mContext = context;
        this.mItemList = items;
    }

    public void addAll(Collection<S> items) {
        addAll(LAST_POSITION, items);
    }

    public void addAll(int position, Collection<S> items) {
        position = position == LAST_POSITION ? getItemCount() : position;
        mItemList.addAll(position, items);

        notifyItemRangeInserted(position, mItemList.size());
    }

    public void add(S item) {
        add(LAST_POSITION, item);
    }

    public void add(int position, S item) {
        position = position == LAST_POSITION ? getItemCount() : position;
        mItemList.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(int position){
        if (position == LAST_POSITION && getItemCount() > 0)
            position = getItemCount() - 1;

        if (position > LAST_POSITION && position < getItemCount()) {
            mItemList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void removeRange(int position, int count) {
        mItemList.subList(position, position + count).clear();
        notifyItemRangeRemoved(position, position + count - 1);
    }

    public void replaceWith(Collection<S> items) {
        int newCount = items.size();
        int oldCount = items.size();
        int delCount = oldCount - newCount;
        mItemList.clear();
        mItemList.addAll(items);
        if(delCount > 0)
            notifyItemRangeRemoved(newCount, delCount);
        notifyItemRangeChanged(0, newCount);
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

    public S getItem(int position) {
        return mItemList.get(position);
    }

    public String getString(@StringRes int id) {
        return mContext.getString(id);
    }

    public String getString(@StringRes int id, Object... args) {
        return mContext.getString(id, args);
    }

    public Context getContext() {
        return mContext;
    }

    public List<S> getItemList() {
        return mItemList;
    }

    public void setOnItemClickListener(RecyclerViewOnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        if(holder.getOnClickView() != null) {
            holder.getOnClickView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, position, getItemId(position));
                    }
                }
            });
            holder.getOnClickView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemLongClickListener != null) {
                        return mOnItemLongClickListener.onItemLongClick(view, position, getItemId(position));
                    }
                    return false;
                }
            });
        }
    }
}
