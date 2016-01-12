package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;

import org.cryse.lkong.R;
import org.cryse.lkong.model.BrowseHistory;
import org.cryse.lkong.ui.listener.OnItemThreadClickListener;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class BrowseHistoryAdapter extends RecyclerViewBaseAdapter<BrowseHistory> {
    private String mATEKey;
    private final String mTodayPrefix;

    OnBrowseHistoryItemClickListener mOnBrowseHistoryItemClickListener;

    public BrowseHistoryAdapter(Context context, String ateKey, List<BrowseHistory> mItemList) {
        super(context, mItemList);
        this.mATEKey = ateKey;
        this.mTodayPrefix = getString(R.string.datetime_today);
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_browse_history, parent, false);
        return new ViewHolder(v, mATEKey, mOnBrowseHistoryItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof BrowseHistory) {
                BrowseHistory historyItem = (BrowseHistory)item;

                SpannableStringBuilder spannableTitle = new SpannableStringBuilder();
                viewHolder.mTitleTextView.setText(historyItem.getThreadTitle());
                viewHolder.mSecondaryTextView.setText(historyItem.getThreadAuthorName());
                viewHolder.mSecondaryTextView.setText(historyItem.getForumTitle() + " - " + historyItem.getThreadAuthorName());
                viewHolder.mTimeTextView.setText(TimeFormatUtils.formatDateDividByToday(
                        historyItem.getLastReadTimeDate(),
                        mTodayPrefix,
                        getContext().getResources().getConfiguration().locale));
            }
        }
    }

    public void setOnBrowseHistoryItemClickListener(OnBrowseHistoryItemClickListener listener) {
        this.mOnBrowseHistoryItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case

        @Bind(R.id.recyclerview_item_browse_history_relative_layout_root)
        RelativeLayout mRootView;
        @Bind(R.id.recyclerview_item_browse_history_textview_title)
        public TextView mTitleTextView;
        @Bind(R.id.recyclerview_item_browse_history_textview_secondary)
        public TextView mSecondaryTextView;
        @Bind(R.id.recyclerview_item_browse_history_textview_time)
        public TextView mTimeTextView;

        OnBrowseHistoryItemClickListener mOnThreadItemClickListener;
        public ViewHolder(View v, String ateKey, OnBrowseHistoryItemClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            mOnThreadItemClickListener = listener;
            itemView.setOnClickListener(view -> {
                if(mOnThreadItemClickListener != null) {
                    mOnThreadItemClickListener.onItemThreadClick(view, getAdapterPosition());
                }
            });
            ATE.apply(itemView, ateKey);
        }
    }

    public interface OnBrowseHistoryItemClickListener extends OnItemThreadClickListener {

    }
}
