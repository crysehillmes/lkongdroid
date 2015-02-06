package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.utils.DateFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ThreadListAdapter extends RecyclerViewBaseAdapter<ThreadModel> {
    private final String mTodayPrefix;
    public ThreadListAdapter(Context context, List<ThreadModel> mItemList) {
        super(context, mItemList);
        mTodayPrefix = getString(R.string.datetime_today);
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_thread, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof ThreadModel) {
                ThreadModel threadModel = (ThreadModel)item;

                viewHolder.mThreadTitleTextView.setText(android.text.Html.fromHtml(threadModel.getSubject()));
                viewHolder.mThreadSecondaryTextView.setText(threadModel.getUserName());
                viewHolder.mNotice1TextView.setText(Integer.toString(threadModel.getReplyCount()));
                viewHolder.mNotice2TextView.setText(DateFormatUtils.formatDateDividByToday(threadModel.getDateline(), mTodayPrefix));
                Picasso.with(getContext())
                        .load(threadModel.getUserIcon())
                        .error(R.drawable.ic_default_avatar)
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(viewHolder.mThreadIconImageView);
            }
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case

        @InjectView(R.id.recyclerview_item_thread_imageview_icon)
        public ImageView mThreadIconImageView;
        @InjectView(R.id.recyclerview_item_thread_textview_title)
        public TextView mThreadTitleTextView;
        @InjectView(R.id.recyclerview_item_thread_textview_secondary)
        public TextView mThreadSecondaryTextView;
        @InjectView(R.id.recyclerview_item_thread_textview_notice1)
        public TextView mNotice1TextView;
        @InjectView(R.id.recyclerview_item_thread_textview_notice2)
        public TextView mNotice2TextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
