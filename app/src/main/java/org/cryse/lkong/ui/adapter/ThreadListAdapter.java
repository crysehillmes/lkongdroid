package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.ColorUtils;
import org.cryse.utils.DateFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ThreadListAdapter extends RecyclerViewBaseAdapter<ThreadModel> {
    public static final String THREAD_PICASSO_TAG = "picasso_thread_list_adapter";
    private final String mTodayPrefix;
    private int mColorAccent;
    private final int mAvatarSize;
    private CircleTransform mCircleTransform = new CircleTransform();
    public ThreadListAdapter(Context context, List<ThreadModel> mItemList) {
        super(context, mItemList);
        mTodayPrefix = getString(R.string.datetime_today);
        mColorAccent = ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent);
        mAvatarSize = UIUtils.getDefaultAvatarSize(context);
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

                SpannableStringBuilder spannableTitle = new SpannableStringBuilder();
                if(threadModel.isDigest()) {
                    String digestIndicator = getString(R.string.indicator_thread_digest);
                    spannableTitle.append(digestIndicator);
                    spannableTitle.setSpan(new ForegroundColorSpan(mColorAccent), 0, digestIndicator.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                spannableTitle.append(threadModel.getSubject());
                viewHolder.mThreadTitleTextView.setText(spannableTitle);
                viewHolder.mThreadSecondaryTextView.setText(threadModel.getUserName());
                viewHolder.mNotice1TextView.setText(Integer.toString(threadModel.getReplyCount()));
                viewHolder.mNotice2TextView.setText(DateFormatUtils.formatDateDividByToday(threadModel.getDateline(), mTodayPrefix));
                Picasso.with(getContext())
                        .load(threadModel.getUserIcon())
                        .tag(THREAD_PICASSO_TAG)
                        .error(R.drawable.ic_default_avatar)
                        .placeholder(R.drawable.ic_default_avatar)
                        .resize(mAvatarSize, mAvatarSize)
                        .transform(mCircleTransform)
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
