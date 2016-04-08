package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.ui.listener.OnItemProfileAreaClickListener;
import org.cryse.lkong.ui.listener.OnItemThreadClickListener;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.ThemeUtils;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.widget.recyclerview.RecyclerViewHolder;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class ThreadListAdapter extends SimpleRecyclerViewAdapter<ThreadModel> {
    private String mATEKey;
    private final String mTodayPrefix;
    private int mColorAccent;
    private final int mAvatarSize;
    private int mAvatarLoadPolicy;

    OnThreadItemClickListener mOnThreadItemClickListener;
    private CircleTransform mCircleTransform;
    public ThreadListAdapter(Context context, String ateKey, List<ThreadModel> mItemList, int avatarLoadPolicy) {
        super(context, mItemList);
        this.mATEKey = ateKey;
        this.mTodayPrefix = mContext.getString(R.string.text_datetime_today);
        this.mColorAccent = ThemeUtils.accentColor(context);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mCircleTransform = new CircleTransform(mContext);
        this.mAvatarLoadPolicy = avatarLoadPolicy;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_thread, parent, false);
        return new ViewHolder(v, mATEKey, mOnThreadItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        ThreadModel threadModel = getItem(position);
        bindThreadModel(viewHolder, threadModel);
    }

    public void bindThreadModel(ViewHolder viewHolder, ThreadModel threadModel) {
        SpannableStringBuilder spannableTitle = new SpannableStringBuilder();
        if(threadModel.isDigest()) {
            String digestIndicator = mContext.getString(R.string.indicator_thread_digest);
            spannableTitle.append(digestIndicator);
            spannableTitle.setSpan(new ForegroundColorSpan(mColorAccent), 0, digestIndicator.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        spannableTitle.append(threadModel.getSubject());
        viewHolder.mThreadTitleTextView.setText(spannableTitle);
        viewHolder.mThreadSecondaryTextView.setText(threadModel.getUserName());
        viewHolder.mNotice1TextView.setText(Integer.toString(threadModel.getReplyCount()));
        viewHolder.mNotice2TextView.setText(TimeFormatUtils.formatDateDividByToday(
                threadModel.getDateline(),
                mTodayPrefix,
                mContext.getResources().getConfiguration().locale));
        ImageLoader.loadAvatar(
                mContext,
                viewHolder.mThreadIconImageView,
                threadModel.getUserIcon(),
                mAvatarSize,
                mCircleTransform,
                mAvatarLoadPolicy
        );
    }

    public void setOnThreadItemClickListener(OnThreadItemClickListener listener) {
        this.mOnThreadItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case

        @Bind(R.id.recyclerview_item_thread_relative_layout_root)
        RelativeLayout mRootView;
        @Bind(R.id.recyclerview_item_thread_imageview_icon)
        public ImageView mThreadIconImageView;
        @Bind(R.id.recyclerview_item_thread_textview_title)
        public TextView mThreadTitleTextView;
        @Bind(R.id.recyclerview_item_thread_textview_secondary)
        public TextView mThreadSecondaryTextView;
        @Bind(R.id.recyclerview_item_thread_textview_notice1)
        public TextView mNotice1TextView;
        @Bind(R.id.recyclerview_item_thread_textview_notice2)
        public TextView mNotice2TextView;

        OnThreadItemClickListener mOnThreadItemClickListener;
        public ViewHolder(View v, String ateKey, OnThreadItemClickListener listener) {
            super(v);
            ButterKnife.bind(this, v);
            mOnThreadItemClickListener = listener;
            mThreadIconImageView.setOnClickListener(view -> {
                if(mOnThreadItemClickListener != null) {
                    mOnThreadItemClickListener.onProfileAreaClick(view, getAdapterPosition(), 0);
                }
            });
            itemView.setOnClickListener(view -> {
                if(mOnThreadItemClickListener != null) {
                    mOnThreadItemClickListener.onItemThreadClick(view, getAdapterPosition());
                }
            });
            // ATE.apply(itemView, ateKey);
        }
    }

    public interface OnThreadItemClickListener extends OnItemThreadClickListener, OnItemProfileAreaClickListener {

    }
}
