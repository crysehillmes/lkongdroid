package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.utils.htmltextview.HtmlTextView;
import org.cryse.utils.DateFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostListAdapter extends RecyclerViewBaseAdapter<PostListAdapter.ViewHolder, PostModel> {
    private final String mTodayPrefix;
    public PostListAdapter(Context context, List<PostModel> mItemList) {
        super(context, mItemList);
        mTodayPrefix = getString(R.string.datetime_today);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_post, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        PostModel item = getItem(position);
        holder.mMessageTextView.setHtmlFromString(item.getMessage(), false);
        holder.mAuthorTextView.setText(item.getAuthorName());
        holder.mDatelineTextView.setText(DateFormatUtils.formatFullDateDividByToday(item.getDateline(), mTodayPrefix));
        holder.mOrdinalTextView.setText(getString(R.string.format_post_ordinal, item.getOrdinal()));
        Picasso.with(getContext())
                .load(ModelConverter.uidToAvatarUrl(item.getAuthorId()))
                .error(R.drawable.ic_default_avatar)
                .placeholder(R.drawable.ic_default_avatar)
                .into(holder.mAuthorAvatarImageView);
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case
        @InjectView(R.id.recyclerview_item_post_textview_author_name)
        TextView mAuthorTextView;
        @InjectView(R.id.recyclerview_item_post_textview_dateline)
        TextView mDatelineTextView;
        @InjectView(R.id.recyclerview_item_post_textview_ordinal)
        TextView mOrdinalTextView;
        @InjectView(R.id.recyclerview_item_post_textview_message)
        HtmlTextView mMessageTextView;
        @InjectView(R.id.recyclerview_item_post_imageview_author_avatar)
        ImageView mAuthorAvatarImageView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
