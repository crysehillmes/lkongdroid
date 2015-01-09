package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.converter.ForumModel;
import org.cryse.utils.ColorUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ForumListAdapter extends RecyclerViewBaseAdapter<ForumListAdapter.ViewHolder, ForumModel>{
    public ForumListAdapter(Context context, List<ForumModel> mItemList) {
        super(context, mItemList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_forum, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ForumModel forumModel = getItem(position);

        holder.mForumTitleTextView.setText(forumModel.getName());
        holder.mForumSecondaryTextView.setText(getString(R.string.format_forum_item_threads_todayposts, forumModel.getThreads(), forumModel.getTodayPosts()));
        Picasso.with(getContext())
                .load(forumModel.getIcon())
                .placeholder(new ColorDrawable(ColorUtils.getColorFromAttr(getContext(), R.attr.theme_card_bg_color)))
                .error(new ColorDrawable(ColorUtils.getColorFromAttr(getContext(), R.attr.theme_card_bg_color)))
                .into(holder.mForumIconImageView);
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case

        @InjectView(R.id.recyclerview_item_forum_imageview_icon)
        public ImageView mForumIconImageView;
        @InjectView(R.id.recyclerview_item_forum_textview_title)
        public TextView mForumTitleTextView;
        @InjectView(R.id.recyclerview_item_forum_textview_secondary)
        public TextView mForumSecondaryTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
