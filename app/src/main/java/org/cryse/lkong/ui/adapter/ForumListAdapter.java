package org.cryse.lkong.ui.adapter;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.utils.NumberFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class ForumListAdapter extends RecyclerViewBaseAdapter<ForumModel> {
    Fragment mParentFragment;
    private String mATEKey;
    public ForumListAdapter(Fragment fragment, String ateKey, List<ForumModel> mItemList) {
        super(fragment.getContext(), mItemList);
        this.mParentFragment = fragment;
        this.mATEKey = ateKey;
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forum_detail, parent, false);
        return new ViewHolder(v, mATEKey);
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

                RequestManager glide = mParentFragment == null ? Glide.with(getContext()) : Glide.with(mParentFragment);
                glide
                        .load(forumModel.getIcon())
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
        @Bind(R.id.recyclerview_item_forum_textview_secondary)
        public TextView mForumSecondaryTextView;

        public ViewHolder(View v, String ateKey) {
            super(v);
            ButterKnife.bind(this, v);
            ATE.apply(itemView, ateKey);
        }
    }
}
