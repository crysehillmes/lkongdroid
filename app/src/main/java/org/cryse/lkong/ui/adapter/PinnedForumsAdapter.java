package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.data.model.PinnedForumEntity;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PinnedForumsAdapter extends RecyclerViewBaseAdapter<PinnedForumEntity>{
    private Picasso mPicasso;
    public PinnedForumsAdapter(Context context, Picasso picasso, List<PinnedForumEntity> mItemList) {
        super(context, mItemList);
        mPicasso = picasso;
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
            if(item instanceof PinnedForumEntity) {
                PinnedForumEntity forumModel = (PinnedForumEntity)item;

                viewHolder.mForumTitleTextView.setText(forumModel.getForumName());
                /*viewHolder.mForumSecondaryTextView.setText(getString(R.string.format_forum_item_threads_todayposts, forumModel.getThreads(), forumModel.getTodayPosts()));*/
                mPicasso
                        .load(forumModel.getForumIcon())
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
                        .noFade()
                        .into(viewHolder.mForumIconImageView);
            }
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case

        @InjectView(R.id.recyclerview_item_forum_imageview_icon)
        public ImageView mForumIconImageView;
        @InjectView(R.id.recyclerview_item_forum_textview_title)
        public TextView mForumTitleTextView;
        /*@InjectView(R.id.recyclerview_item_forum_textview_secondary)
        public TextView mForumSecondaryTextView;*/

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
