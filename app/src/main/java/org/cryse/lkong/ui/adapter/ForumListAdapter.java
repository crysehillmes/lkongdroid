package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.utils.NumberFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ForumListAdapter extends RecyclerViewBaseAdapter<ForumModel>{
    private Picasso mPicasso;
    public ForumListAdapter(Context context, Picasso picasso, List<ForumModel> mItemList) {
        super(context, mItemList);
        mPicasso = picasso;
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_forum_detail, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof ForumModel) {
                ForumModel forumModel = (ForumModel)item;
                viewHolder.mForumTitleTextView.setText(forumModel.getName());
                String todayPostsCount;
                String threadsCount;
                if(getContext().getResources().getConfiguration().locale.getLanguage().equalsIgnoreCase("zh")) {
                    todayPostsCount  = NumberFormatUtils.numberToTenKiloString(
                            forumModel.getTodayPosts(),
                            getString(R.string.format_unit_ten_kilo),
                            false,
                            true
                    );
                    threadsCount = NumberFormatUtils.numberToTenKiloString(
                            forumModel.getThreads(),
                            getString(R.string.format_unit_ten_kilo),
                            false,
                            true
                    );
                } else {
                    todayPostsCount  = NumberFormatUtils.numberToKiloString(
                            forumModel.getTodayPosts(),
                            getString(R.string.format_unit_kilo),
                            false,
                            true
                    );
                    threadsCount = NumberFormatUtils.numberToKiloString(
                            forumModel.getThreads(),
                            getString(R.string.format_unit_kilo),
                            false,
                            true
                    );
                }

                String secondaryInfo = getString(R.string.format_forum_item_summary, threadsCount, todayPostsCount);
                // String todayPosts = getString(R.string.format_forum_item_todayposts, forumModel.getTodayPosts());
                viewHolder.mForumSecondaryTextView.setText(secondaryInfo);



                mPicasso
                        .load(forumModel.getIcon())
                        .placeholder(R.drawable.image_placeholder)
                        .error(R.drawable.image_placeholder)
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
        @InjectView(R.id.recyclerview_item_forum_textview_secondary)
        public TextView mForumSecondaryTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
