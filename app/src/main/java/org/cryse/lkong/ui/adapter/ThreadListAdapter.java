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

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.ui.listener.OnItemProfileAreaClickListener;
import org.cryse.lkong.ui.listener.OnItemThreadClickListener;
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
    private String mPicassoTag;

    OnThreadItemClickListener mOnThreadItemClickListener;
    private CircleTransform mCircleTransform;
    public ThreadListAdapter(Context context, List<ThreadModel> mItemList) {
        this(context, mItemList, THREAD_PICASSO_TAG);
    }

    public ThreadListAdapter(Context context, List<ThreadModel> mItemList, String picassoTag) {
        super(context, mItemList);
        this.mTodayPrefix = getString(R.string.datetime_today);
        this.mColorAccent = ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mPicassoTag = picassoTag;
        this.mCircleTransform = new CircleTransform(mContext);
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_thread, parent, false);
        return new ViewHolder(v, mOnThreadItemClickListener);
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
                        threadModel);
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
                                       ThreadModel threadModel) {
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
        viewHolder.mNotice2TextView.setText(DateFormatUtils.formatDateDividByToday(threadModel.getDateline(), todayPrefix));
        Glide.with(context)
                .load(threadModel.getUserIcon())
                .error(R.drawable.ic_placeholder_avatar)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .override(avatarSize, avatarSize)
                .transform(circleTransform)
                .into(viewHolder.mThreadIconImageView);
    }

    public void setOnThreadItemClickListener(OnThreadItemClickListener listener) {
        this.mOnThreadItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case

        @InjectView(R.id.recyclerview_item_thread_relative_layout_root)
        RelativeLayout mRootView;
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

        OnThreadItemClickListener mOnThreadItemClickListener;
        public ViewHolder(View v, OnThreadItemClickListener listener) {
            super(v);
            ButterKnife.inject(this, v);
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
        }
    }

    public interface OnThreadItemClickListener extends OnItemThreadClickListener, OnItemProfileAreaClickListener {

    }
}
