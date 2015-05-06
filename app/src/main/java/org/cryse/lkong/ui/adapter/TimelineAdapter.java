package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
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

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.ui.listener.OnItemProfileAreaClickListener;
import org.cryse.lkong.ui.listener.OnTimelineItemClickListener;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.lkong.utils.ConnectionUtils;
import org.cryse.lkong.utils.SimpleImageGetter;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.htmltextview.HtmlTagHandler;
import org.cryse.lkong.utils.htmltextview.HtmlTextUtils;
import org.cryse.utils.ColorUtils;
import org.cryse.utils.DateFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TimelineAdapter extends RecyclerViewBaseAdapter<TimelineModel> {
    private static final String LOG_TAG = TimelineAdapter.class.getName();
    private final String mTodayPrefix;
    private final String mImageTaskTag;
    private final int mAvatarSize;
    private CircleTransform mCircleTransform = new CircleTransform();
    private Picasso mPicasso;
    private OnTimelineModelItemClickListener mOnTimelineModelItemClickListener;
    public TimelineAdapter(Context context, List<TimelineModel> items, Picasso picasso, String imgTaskTag) {
        super(context, items);
        mTodayPrefix = getString(R.string.datetime_today);
        mPicasso = picasso;
        mImageTaskTag = imgTaskTag;
        mAvatarSize = UIUtils.getDefaultAvatarSize(context);
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_timeline, parent, false);
        return new ViewHolder(v, mOnTimelineModelItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        //super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof TimelineModel) {
                TimelineModel timelineModel = (TimelineModel)item;
                bindTimelineItem(
                        getContext(),
                        mPicasso,
                        mTodayPrefix,
                        mImageTaskTag,
                        mAvatarSize,
                        mCircleTransform,
                        viewHolder,
                        timelineModel
                );
            }
        }
    }

    public static void bindTimelineItem(
            Context context,
            Picasso picasso,
            String todayPrefix,
            String imageTaskTag,
            int avatarSize,
            CircleTransform circleTransform,
            ViewHolder viewHolder,
            TimelineModel timelineModel) {

        SpannableStringBuilder mainPrefixSpannable = new SpannableStringBuilder();
        String mainContent;
        if(timelineModel.isQuote()) {
            // 回复某一条回复
            viewHolder.mSecondaryContainer.setVisibility(View.VISIBLE);

            SpannableStringBuilder spanText = new SpannableStringBuilder();
            String secondaryText = context.getString(R.string.format_timeline_reply_to_reply, timelineModel.getReplyQuote().getPosterName(), timelineModel.getSubject());
            spanText.append(secondaryText);
            if(!TextUtils.isEmpty(timelineModel.getReplyQuote().getPosterName())) {
                int nameStart = secondaryText.indexOf(timelineModel.getReplyQuote().getPosterName());
                int nameEnd = nameStart + timelineModel.getReplyQuote().getPosterName().length();
                spanText.setSpan(new StyleSpan(Typeface.BOLD), nameStart, nameEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            viewHolder.mSecondaryMessageTextView.setText(spanText);

            viewHolder.mThirdMessageTextView.setText(timelineModel.getReplyQuote().getPosterMessage());
            mainContent = timelineModel.getReplyQuote().getMessage();
        } else if(!timelineModel.isQuote() && !timelineModel.isThread()) {
            // 回复某一主题
            viewHolder.mSecondaryContainer.setVisibility(View.VISIBLE);
            SpannableStringBuilder spanText = new SpannableStringBuilder();
            String secondaryText = context.getString(R.string.format_timeline_reply_to_thread, timelineModel.getThreadAuthor());
            spanText.append(secondaryText);
            if(!TextUtils.isEmpty(timelineModel.getThreadAuthor())) {
                int nameStart = secondaryText.indexOf(timelineModel.getThreadAuthor());
                int nameEnd = nameStart + timelineModel.getThreadAuthor().length();
                spanText.setSpan(new StyleSpan(Typeface.BOLD), nameStart, nameEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            }
            viewHolder.mSecondaryMessageTextView.setText(spanText);
            viewHolder.mThirdMessageTextView.setText(timelineModel.getSubject());
            mainContent = timelineModel.getMessage();
        } else if(timelineModel.isThread()) {
            // 用户自己发布主题
            viewHolder.mSecondaryContainer.setVisibility(View.GONE);
            String createInfo = context.getString(R.string.format_timeline_create_thread, timelineModel.getSubject());
            mainPrefixSpannable.append(createInfo);
            mainPrefixSpannable.setSpan(new ForegroundColorSpan(ColorUtils.getColorFromAttr(context, R.attr.theme_text_color_secondary)),
                    0,
                    createInfo.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mainPrefixSpannable.append('\n');
            mainContent = timelineModel.getMessage();
        } else {
            // 其他
            viewHolder.mSecondaryContainer.setVisibility(View.GONE);
            mainContent = timelineModel.getMessage();
        }

        SimpleImageGetter imageGetter = new SimpleImageGetter(context, ConnectionUtils.IMAGE_DOWNLOAD_ALWAYS)
                .setEmoticonSize((int)UIUtils.getSpDimensionPixelSize(context, R.dimen.text_size_body1))
                .setPlaceHolder(R.drawable.image_placeholder)
                .setError(R.drawable.image_placeholder);
        Spanned spannedText = HtmlTextUtils.htmlToSpanned(mainContent, imageGetter, new HtmlTagHandler());
        SpannableStringBuilder mainSpannable = new SpannableStringBuilder();
        mainSpannable.append(mainPrefixSpannable).append(spannedText);
        viewHolder.mMessageTextView.setText(mainSpannable);

        viewHolder.mAuthorTextView.setText(timelineModel.getUserName());
        viewHolder.mDatelineTextView.setText(DateFormatUtils.formatFullDateDividByToday(timelineModel.getDateline(), todayPrefix));
        picasso
                .load(ModelConverter.uidToAvatarUrl(timelineModel.getUserId()))
                .tag(imageTaskTag)
                .error(R.drawable.ic_placeholder_avatar)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .resize(avatarSize, avatarSize)
                .transform(circleTransform)
                .into(viewHolder.mAuthorAvatarImageView);
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case
        @InjectView(R.id.recyclerview_item_timeline_cardview_root_container)
        CardView mRootCardView;
        @InjectView(R.id.recyclerview_item_timeline_textview_author_name)
        TextView mAuthorTextView;
        @InjectView(R.id.recyclerview_item_timeline_textview_dateline)
        TextView mDatelineTextView;
        @InjectView(R.id.recyclerview_item_timeline_textview_message)
        TextView mMessageTextView;
        @InjectView(R.id.recyclerview_item_timeline_imageview_author_avatar)
        ImageView mAuthorAvatarImageView;


        @InjectView(R.id.secondary_message_container)
        RelativeLayout mSecondaryContainer;
        @InjectView(R.id.recyclerview_item_timeline_secondary_message)
        TextView mSecondaryMessageTextView;
        @InjectView(R.id.recyclerview_item_timeline_third_message)
        TextView mThirdMessageTextView;


        OnTimelineModelItemClickListener mOnTimelineModelItemClickListener;
        public ViewHolder(View v, OnTimelineModelItemClickListener onTimelineModelItemClickListener) {
            super(v);
            ButterKnife.inject(this, v);
            mOnTimelineModelItemClickListener = onTimelineModelItemClickListener;
            mAuthorAvatarImageView.setOnClickListener(view -> {
                if(mOnTimelineModelItemClickListener != null) {
                    mOnTimelineModelItemClickListener.onProfileAreaClick(view, getAdapterPosition(), 0l);
                }
            });
            itemView.setOnClickListener(v1 -> {
                if(mOnTimelineModelItemClickListener != null) {
                    mOnTimelineModelItemClickListener.onTimelineItemClick(v1, getAdapterPosition());
                }
            });
        }
    }

    public void setOnTimelineModelItemClickListener(OnTimelineModelItemClickListener onTimelineModelItemClickListener) {
        this.mOnTimelineModelItemClickListener = onTimelineModelItemClickListener;
    }

    public interface OnTimelineModelItemClickListener extends OnItemProfileAreaClickListener, OnTimelineItemClickListener {

    }
}
