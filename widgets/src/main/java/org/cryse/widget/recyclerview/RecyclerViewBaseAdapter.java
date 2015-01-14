package org.cryse.widget.recyclerview;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class RecyclerViewBaseAdapter<S> extends RecyclerView.Adapter<RecyclerViewHolder> {
    protected Context mContext;
    protected HeaderFootList<S> mObjectList;
    protected RecyclerViewOnItemClickListener mOnItemClickListener;
    protected RecyclerViewOnItemLongClickListener mOnItemLongClickListener;

    public static final int ITEM_TYPE_HEADER_START = 200;
    public static final int ITEM_TYPE_FOOTER_START = 400;
    public static final int ITEM_TYPE_ITEM_START = 800;
    public static final int TYPE_OFFSET = 100;

    public RecyclerViewBaseAdapter(Context context, List<S> items) {
        this.mContext = context;
        this.mObjectList = new HeaderFootList<S>(items);
    }

    public void addAll(Collection<S> items) {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        int currentItemCount = mObjectList.getItemCount();
        mObjectList.getItemList().addAll(items);
        notifyItemRangeInserted(currentHeaderCount + currentItemCount, items.size());
    }

    public void addAll(int position, Collection<S> items) {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        int currentItemCount = mObjectList.getItemCount();
        if(position > currentItemCount)
            throw new IndexOutOfBoundsException();
        else
            mObjectList.getItemList().addAll(position, items);
        notifyItemRangeInserted(currentHeaderCount + position, items.size());
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType >= ITEM_TYPE_HEADER_START && viewType < ITEM_TYPE_HEADER_START + TYPE_OFFSET) {
            return onCreateHeaderViewHolder(parent, viewType);
        } else if(viewType >= ITEM_TYPE_FOOTER_START && viewType < ITEM_TYPE_FOOTER_START + TYPE_OFFSET) {
            return onCreateFooterViewHolder(parent, viewType);
        } else if(viewType >= ITEM_TYPE_ITEM_START && viewType < ITEM_TYPE_ITEM_START + TYPE_OFFSET) {
            return onCreateItemViewHolder(parent, viewType);
        } else {
            throw new IllegalArgumentException("Unknown viewType.");
        }
    }

    @Override
    public int getItemViewType(int position) {
        int ret = mObjectList.indexIn(position);
        if( ret == HeaderFootList.IN_HEADER_LIST)
            return onGetHeaderViewItemType(position);
        else if(ret == HeaderFootList.IN_FOOTER_LIST)
            return onGetFooterViewItemType(position);
        else if(ret == HeaderFootList.IN_ITEM_LIST)
            return onGetItemViewItemType(position);
        else
            throw new IndexOutOfBoundsException();
        // return super.getItemViewType(position);
    }

    public void add(S item) {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        int currentItemCount = mObjectList.getItemCount();
        mObjectList.getItemList().add(item);
        notifyItemInserted(currentHeaderCount + currentItemCount);
    }

    public void add(int position, S item) {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        int currentItemCount = mObjectList.getItemCount();
        if(position > currentItemCount)
            throw new IndexOutOfBoundsException();
        else
            mObjectList.getItemList().add(position, item);
        notifyItemInserted(currentHeaderCount + position);
    }

    public void remove(int position){
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        if(position >= 0 && position < mObjectList.getItemCount()) {
            mObjectList.getItemList().remove(position);
            notifyItemRemoved(currentHeaderCount + position);
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    public void replaceWith(Collection<S> items) {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        int oldCount = mObjectList.getItemCount();
        int newCount = items.size();
        int delCount = oldCount - newCount;
        mObjectList.getItemList().clear();
        mObjectList.getItemList().addAll(items);
        if(delCount > 0) {
            notifyItemRangeChanged(0 + currentHeaderCount, newCount);
            notifyItemRangeRemoved(newCount + currentHeaderCount, delCount);
        } else if(delCount < 0) {
            notifyItemRangeChanged(0 + currentHeaderCount, oldCount);
            notifyItemRangeInserted(oldCount + currentHeaderCount, - delCount);
        } else {
            notifyItemRangeChanged(0 + currentHeaderCount, newCount);
        }
    }

    public void addHeaderView(View headerView) {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        mObjectList.getHeaderViewList().add(headerView);
        notifyItemInserted(currentHeaderCount);
    }

    public void removeHeaderView(View headerView) {
        int removeIndex = mObjectList.getHeaderViewList().indexOf(headerView);
        mObjectList.getHeaderViewList().remove(headerView);
        notifyItemRemoved(removeIndex);
    }

    public void addFooterView(View footerView) {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        int currentItemCount = mObjectList.getItemCount();
        int currentFooterCount = mObjectList.getFooterViewCount();
        mObjectList.getFooterViewList().add(footerView);
        notifyItemInserted(currentHeaderCount + currentItemCount + currentFooterCount);
    }

    public void removeFooterView(View footerView) {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        int currentItemCount = mObjectList.getItemCount();
        int removeIndex = mObjectList.getFooterViewList().indexOf(footerView);
        int removePosition = removeIndex + currentHeaderCount + currentItemCount;
        mObjectList.getFooterViewList().remove(footerView);
        notifyItemRemoved(removePosition);
    }

    public void clear() {
        int currentHeaderCount = mObjectList.getHeaderViewCount();
        int itemCount = mObjectList.getItemList().size();
        mObjectList.getItemList().clear();
        notifyItemRangeRemoved(0 + currentHeaderCount, itemCount);
    }

    @Override
    public int getItemCount() {
        return mObjectList.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public Object getObjectItem(int position) {
        return mObjectList.get(position);
    }

    public S getItem(int position) {
        return mObjectList.getItemList().get(position);
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
        return mObjectList.getItemList();
    }

    public ArrayList<S> getItemArrayList() {
        List<S> itemList = mObjectList.getItemList();
        if(itemList instanceof ArrayList)
            return (ArrayList<S>)itemList;
        else {
            ArrayList<S> arrayList = new ArrayList<S>(itemList.size());
            arrayList.addAll(itemList);
            return arrayList;
        }

    }

    public void setOnItemClickListener(RecyclerViewOnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
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


    public int onGetHeaderViewItemType(int position) {
        return ITEM_TYPE_HEADER_START + position;
    }
    public int onGetFooterViewItemType(int position) {
        return ITEM_TYPE_FOOTER_START + position - mObjectList.getHeaderViewCount() - mObjectList.getItemCount();
    }
    public int onGetItemViewItemType(int position) {
        return ITEM_TYPE_ITEM_START;
    }

    public HeaderViewHolder onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        return new HeaderViewHolder(mObjectList.getHeaderViewList().get(viewType - ITEM_TYPE_HEADER_START));
    }
    public FooterViewHolder onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return new FooterViewHolder(mObjectList.getFooterViewList().get(viewType - ITEM_TYPE_FOOTER_START));
    }
    public abstract RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType);

    public static class HeaderViewHolder extends RecyclerViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public static class FooterViewHolder extends RecyclerViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }


}
