package org.cryse.lkong.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PostModel;

import java.util.List;

public class PostRateAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<PostModel.PostRate> mItems;

    public PostRateAdapter(Context context, List<PostModel.PostRate> items) {
        this.mContext = context;
        this.mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public PostModel.PostRate getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PostRateViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.listview_item_post_rate, null);
            viewHolder = new PostRateViewHolder(convertView, position);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (PostRateViewHolder) convertView.getTag();
        }
        PostModel.PostRate rateItem = getItem(position);
        viewHolder.userNameTextView.setText(rateItem.getUserName());
        String scoreText = Integer.toString(rateItem.getScore()) + " ";
        if (rateItem.getExtCredits() == 2) {
            scoreText = scoreText + "龙币";
        } else if (rateItem.getExtCredits() == 3) {
            scoreText = scoreText + "龙晶";
        }
        viewHolder.scoreTextView.setText(scoreText);
        if(TextUtils.isEmpty(rateItem.getReason())) {
            viewHolder.reasonTextView.setVisibility(View.GONE);
        } else {
            viewHolder.reasonTextView.setText(rateItem.getReason());
        }
        return convertView;
    }

    static class PostRateViewHolder {
        View itemView;
        TextView userNameTextView;
        TextView scoreTextView;
        TextView reasonTextView;
        int position;

        PostRateViewHolder(View view, int position) {
            this.position = position;
            this.itemView = view;
            this.userNameTextView = (TextView) this.itemView.findViewById(R.id.listview_item_post_rate_textview_username);
            this.scoreTextView = (TextView) this.itemView.findViewById(R.id.listview_item_post_rate_textview_score);
            this.reasonTextView = (TextView) this.itemView.findViewById(R.id.listview_item_post_rate_textview_reason);
        }
    }
}