package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ForumThreadModel;
import org.cryse.utils.DateFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ThreadListAdapter extends RecyclerViewBaseAdapter<ThreadListAdapter.ViewHolder, ForumThreadModel> {
    private final String mTodayPrefix;
    public ThreadListAdapter(Context context, List<ForumThreadModel> mItemList) {
        super(context, mItemList);
        mTodayPrefix = getString(R.string.datetime_today);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_thread, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ForumThreadModel item = getItem(position);


        holder.mThreadTitleTextView.setText(android.text.Html.fromHtml(item.getSubject()));
        holder.mThreadSecondaryTextView.setText(item.getUserName());
        holder.mNotice1TextView.setText(Integer.toString(item.getReplyCount()));
        holder.mNotice2TextView.setText(DateFormatUtils.formatDateDividByToday(item.getDateline(), mTodayPrefix));
        Picasso.with(getContext())
                .load(item.getUserIcon())
                .error(R.drawable.ic_default_avatar)
                .placeholder(R.drawable.ic_default_avatar)
                .into(holder.mThreadIconImageView);
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
