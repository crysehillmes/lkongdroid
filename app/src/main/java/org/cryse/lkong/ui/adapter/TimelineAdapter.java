package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;

import org.cryse.lkong.R;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.ui.listener.OnItemProfileAreaClickListener;
import org.cryse.lkong.ui.listener.OnItemTimelineClickListener;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.SimpleImageGetter;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.htmltextview.HtmlTagHandler;
import org.cryse.lkong.utils.htmltextview.HtmlTextUtils;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class TimelineAdapter extends SimpleRecyclerViewAdapter<TimelineModel> {
    private static final String LOG_TAG = TimelineAdapter.class.getName();
    private static final int TYPE_THREAD = 0;
    private static final int TYPE_REPLY = 1;
    private final String mTodayPrefix;
    private final int mAvatarSize;
    private CircleTransform mCircleTransform;
    private SimpleImageGetter mImageGetter;
    private int mAvatarLoadPolicy;
    private int mTextColorSecondary;
    private String mATEKey;
    private OnTimelineModelItemClickListener mOnTimelineModelItemClickListener;

    public TimelineAdapter(Context context, List<TimelineModel> items, int avatarLoadPolicy, String ateKey) {
        super(context, items);
        this.mTodayPrefix = mContext.getString(R.string.text_datetime_today);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mCircleTransform = new CircleTransform(context);
        this.mAvatarLoadPolicy = avatarLoadPolicy;
        this.mATEKey = ateKey;
        this.mTextColorSecondary = Config.textColorPrimary(context, mATEKey);

        this.mImageGetter = new SimpleImageGetter(mContext, ImageLoader.IMAGE_LOAD_ALWAYS)
                .setEmoticonSize((int)UIUtils.getSpDimensionPixelSize(mContext, R.dimen.text_size_body1)*2)
                .setPlaceHolder(R.drawable.placeholder_loading)
                .setError(R.drawable.placeholder_error);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case TYPE_REPLY:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_timeline, parent, false);
                break;
            case TYPE_THREAD:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_timeline_thread, parent, false);
                break;
        }
        return new ViewHolder(view, mATEKey, mOnTimelineModelItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder)holder;
        TimelineModel item = getItem(position);
        if(item.isQuote() || !item.isThread()) {
            bindReplyItem(viewHolder, item);
        } else if(item.isThread()) {
            bindThreadItem(viewHolder, item);
        } else {
            bindThreadItem(viewHolder, item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        TimelineModel item = getItem(position);
        if(item.isQuote() || !item.isThread()) {
            return TYPE_REPLY;
        } else if(item.isThread()) {
            return TYPE_THREAD;
        } else {
            return TYPE_THREAD;
        }
    }

    public void bindThreadItem(ViewHolder holder, TimelineModel item) {
        // 用户发布主题
        SpannableStringBuilder mainPrefixSpannable = new SpannableStringBuilder();
        String mainContent;
        String createInfo = mContext.getString(R.string.format_timeline_create_thread, item.getSubject());
        mainPrefixSpannable.append(createInfo);
        mainPrefixSpannable.setSpan(new ForegroundColorSpan(mTextColorSecondary),
                0,
                createInfo.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mainPrefixSpannable.setSpan(new StyleSpan(Typeface.BOLD),
                0,
                createInfo.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mainPrefixSpannable.append('\n');
        mainContent = item.getMessage();

        Spanned spannedText = HtmlTextUtils.htmlToSpanned(mainContent, mImageGetter, new HtmlTagHandler());
        SpannableStringBuilder mainSpannable = new SpannableStringBuilder();
        mainSpannable.append(mainPrefixSpannable).append(spannedText);
        holder.mMessageTextView.setText(mainSpannable);

        holder.mAuthorTextView.setText(item.getUserName());
        holder.mDatelineTextView.setText(TimeFormatUtils.formatFullDateDividByToday(item.getDateline(), mTodayPrefix, mContext.getResources().getConfiguration().locale));
        ImageLoader.loadAvatar(
                mContext,
                holder.mAuthorAvatarImageView,
                ModelConverter.uidToAvatarUrl(item.getUserId()),
                mAvatarSize,
                mCircleTransform,
                mAvatarLoadPolicy
        );
    }

    public void bindReplyItem(ViewHolder holder, TimelineModel item) {
        // 用户发布主题
        if(holder.mSecondaryContainer != null && holder.mSecondaryMessageTextView != null && holder.mThirdMessageTextView != null) {
            SpannableStringBuilder mainPrefixSpannable = new SpannableStringBuilder();
            String mainContent;
            if(item.isQuote()) {
                // 回复某一条回复
                holder.mSecondaryContainer.setVisibility(View.VISIBLE);

                SpannableStringBuilder spanText = new SpannableStringBuilder();
                String secondaryText = mContext.getString(R.string.format_timeline_reply_to_reply, item.getReplyQuote().getPosterName(), item.getSubject());
                spanText.append(secondaryText);
                if(!TextUtils.isEmpty(item.getReplyQuote().getPosterName())) {
                    int nameStart = secondaryText.indexOf(item.getReplyQuote().getPosterName());
                    int nameEnd = nameStart + item.getReplyQuote().getPosterName().length();
                    spanText.setSpan(new StyleSpan(Typeface.BOLD), nameStart, nameEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                holder.mSecondaryMessageTextView.setText(spanText);

                holder.mThirdMessageTextView.setText(HtmlTextUtils.htmlToSpanned(item.getReplyQuote().getPosterMessage(), mImageGetter, new HtmlTagHandler()));
                mainContent = item.getReplyQuote().getMessage();
            } else { // else if(!item.isThread()) {
                // 回复某一主题
                holder.mSecondaryContainer.setVisibility(View.VISIBLE);
                SpannableStringBuilder spanText = new SpannableStringBuilder();
                String secondaryText = mContext.getString(R.string.format_timeline_reply_to_thread, item.getThreadAuthor());
                spanText.append(secondaryText);
                if(!TextUtils.isEmpty(item.getThreadAuthor())) {
                    int nameStart = secondaryText.indexOf(item.getThreadAuthor());
                    int nameEnd = nameStart + item.getThreadAuthor().length();
                    spanText.setSpan(new StyleSpan(Typeface.BOLD), nameStart, nameEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                holder.mSecondaryMessageTextView.setText(spanText);
                holder.mThirdMessageTextView.setText(item.getSubject());
                mainContent = item.getMessage();
            }

            Spanned spannedText = HtmlTextUtils.htmlToSpanned(mainContent, mImageGetter, new HtmlTagHandler());
            SpannableStringBuilder mainSpannable = new SpannableStringBuilder();
            mainSpannable.append(mainPrefixSpannable).append(spannedText);
            holder.mMessageTextView.setText(mainSpannable);

            holder.mAuthorTextView.setText(item.getUserName());
            holder.mDatelineTextView.setText(TimeFormatUtils.formatFullDateDividByToday(item.getDateline(), mTodayPrefix, mContext.getResources().getConfiguration().locale));
            ImageLoader.loadAvatar(
                    mContext,
                    holder.mAuthorAvatarImageView,
                    ModelConverter.uidToAvatarUrl(item.getUserId()),
                    mAvatarSize,
                    mCircleTransform,
                    mAvatarLoadPolicy
            );
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.recyclerview_item_timeline_cardview_root_container)
        CardView mRootCardView;
        @Bind(R.id.recyclerview_item_timeline_textview_author_name)
        TextView mAuthorTextView;
        @Bind(R.id.recyclerview_item_timeline_textview_dateline)
        TextView mDatelineTextView;
        @Bind(R.id.recyclerview_item_timeline_textview_message)
        TextView mMessageTextView;
        @Bind(R.id.recyclerview_item_timeline_imageview_author_avatar)
        ImageView mAuthorAvatarImageView;


        @Nullable
        @Bind(R.id.secondary_message_container)
        RelativeLayout mSecondaryContainer;
        @Nullable
        @Bind(R.id.recyclerview_item_timeline_secondary_message)
        TextView mSecondaryMessageTextView;
        @Nullable
        @Bind(R.id.recyclerview_item_timeline_third_message)
        TextView mThirdMessageTextView;


        OnTimelineModelItemClickListener mOnTimelineModelItemClickListener;
        public ViewHolder(View v, String ateKey, OnTimelineModelItemClickListener onTimelineModelItemClickListener) {
            super(v);
            ButterKnife.bind(this, v);
            mOnTimelineModelItemClickListener = onTimelineModelItemClickListener;
            mAuthorAvatarImageView.setOnClickListener(view -> {
                if(mOnTimelineModelItemClickListener != null) {
                    mOnTimelineModelItemClickListener.onProfileAreaClick(view, getAdapterPosition(), 0l);
                }
            });
            itemView.setOnClickListener(v1 -> {
                if(mOnTimelineModelItemClickListener != null) {
                    mOnTimelineModelItemClickListener.onItemTimelineClick(v1, getAdapterPosition());
                }
            });
            // ATE.apply(itemView, ateKey);
            // mRootCardView.setCardBackgroundColor(Config.textColorPrimaryInverse(v.getContext(), ateKey));
        }
    }

    public void setOnTimelineModelItemClickListener(OnTimelineModelItemClickListener onTimelineModelItemClickListener) {
        this.mOnTimelineModelItemClickListener = onTimelineModelItemClickListener;
    }

    public interface OnTimelineModelItemClickListener extends OnItemProfileAreaClickListener, OnItemTimelineClickListener {

    }
}
