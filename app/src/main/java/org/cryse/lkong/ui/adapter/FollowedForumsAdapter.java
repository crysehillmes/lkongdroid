package org.cryse.lkong.ui.adapter;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.data.model.FollowedForum;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class FollowedForumsAdapter extends RecyclerViewBaseAdapter<FollowedForum>{
    private Fragment mParentFragment;
    public FollowedForumsAdapter(Fragment parentFragment, List<FollowedForum> mItemList) {
        super(parentFragment.getContext(), mItemList);
        this.mParentFragment = parentFragment;
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_forum, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof FollowedForum) {
                FollowedForum forumModel = (FollowedForum)item;

                viewHolder.mForumTitleTextView.setText(forumModel.getForumName());
                /*viewHolder.mForumSecondaryTextView.setText(getString(R.string.format_forum_item_threads_todayposts, forumModel.getThreads(), forumModel.getTodayPosts()));*/
                Glide
                        .with(mParentFragment)
                        .load(forumModel.getForumIcon())
                        .placeholder(R.drawable.placeholder_loading)
                        .error(R.drawable.placeholder_error)
                        .into(viewHolder.mForumIconImageView);
            }
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case

        @Bind(R.id.recyclerview_item_forum_imageview_icon)
        public ImageView mForumIconImageView;
        @Bind(R.id.recyclerview_item_forum_textview_title)
        public TextView mForumTitleTextView;
        /*@Bind(R.id.recyclerview_item_forum_textview_secondary)
        public TextView mForumSecondaryTextView;*/

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
