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
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class ThreadListAdapter extends RecyclerViewBaseAdapter<ThreadModel> {
    public static final String THREAD_PICASSO_TAG = "picasso_thread_list_adapter";
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
        this.mTodayPrefix = getString(R.string.text_datetime_today);
        this.mColorAccent = ThemeUtils.accentColor(context);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mCircleTransform = new CircleTransform(mContext);
        this.mAvatarLoadPolicy = avatarLoadPolicy;
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_thread, parent, false);
        return new ViewHolder(v, mATEKey, mOnThreadItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof ThreadModel) {
                ThreadModel threadModel = (ThreadModel)item;
                bindThreadModel(getContext(),
                        mTodayPrefix,
                        THREAD_PICASSO_TAG,
                        mAvatarSize,
                        mColorAccent,
                        mCircleTransform,
                        viewHolder,
                        threadModel,
                        mAvatarLoadPolicy);
            }
        }
    }

    public static void bindThreadModel(Context context,
                                       String todayPrefix,
                                       String imageTaskTag,
                                       int avatarSize,
                                       int colorAccent,
                                       CircleTransform circleTransform,
                                       ViewHolder viewHolder,
                                       ThreadModel threadModel,
                                       int avatarLoadPolicy) {
        SpannableStringBuilder spannableTitle = new SpannableStringBuilder();
        if(threadModel.isDigest()) {
            String digestIndicator = context.getString(R.string.indicator_thread_digest);
            spannableTitle.append(digestIndicator);
            spannableTitle.setSpan(new ForegroundColorSpan(colorAccent), 0, digestIndicator.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        spannableTitle.append(threadModel.getSubject());
        viewHolder.mThreadTitleTextView.setText(spannableTitle);
        viewHolder.mThreadSecondaryTextView.setText(threadModel.getUserName());
        viewHolder.mNotice1TextView.setText(Integer.toString(threadModel.getReplyCount()));
        viewHolder.mNotice2TextView.setText(TimeFormatUtils.formatDateDividByToday(
                threadModel.getDateline(),
                todayPrefix,
                context.getResources().getConfiguration().locale));
        ImageLoader.loadAvatar(
                context,
                viewHolder.mThreadIconImageView,
                threadModel.getUserIcon(),
                avatarSize,
                circleTransform,
                avatarLoadPolicy
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
            ATE.apply(itemView, ateKey);
        }
    }

    public interface OnThreadItemClickListener extends OnItemThreadClickListener, OnItemProfileAreaClickListener {

    }
}
