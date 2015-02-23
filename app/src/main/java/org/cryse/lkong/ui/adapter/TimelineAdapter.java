package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
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
    private final String mTodayPrefix;
    public TimelineAdapter(Context context, List<TimelineModel> items) {
        super(context, items);
        mTodayPrefix = getString(R.string.datetime_today);
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_timeline, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof TimelineModel) {
                TimelineModel timelineModel = (TimelineModel)item;

                SpannableStringBuilder mainPrefixSpannable = new SpannableStringBuilder();
                String mainContent;
                if(timelineModel.isQuote()) {
                    // 回复某一条回复
                    viewHolder.mSecondaryContainer.setVisibility(View.VISIBLE);

                    SpannableStringBuilder spanText = new SpannableStringBuilder();
                    String secondaryText = getString(R.string.format_timeline_reply_to_reply, timelineModel.getReplyQuote().getPosterName(), timelineModel.getSubject());
                    spanText.append(secondaryText);
                    int nameStart = secondaryText.indexOf(timelineModel.getReplyQuote().getPosterName());
                    int nameEnd = nameStart + timelineModel.getReplyQuote().getPosterName().length();
                    spanText.setSpan(new StyleSpan(Typeface.BOLD), nameStart, nameEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    viewHolder.mSecondaryMessageTextView.setText(spanText);

                    viewHolder.mThirdMessageTextView.setText(timelineModel.getReplyQuote().getPosterMessage());
                    mainContent = timelineModel.getReplyQuote().getMessage();
                } else if(!timelineModel.isQuote() && !timelineModel.isThread()) {
                    // 回复某一主题
                    viewHolder.mSecondaryContainer.setVisibility(View.VISIBLE);
                    SpannableStringBuilder spanText = new SpannableStringBuilder();
                    String secondaryText = getString(R.string.format_timeline_reply_to_thread, timelineModel.getThreadAuthor());
                    spanText.append(secondaryText);
                    int nameStart = secondaryText.indexOf(timelineModel.getThreadAuthor());
                    int nameEnd = nameStart + timelineModel.getThreadAuthor().length();
                    spanText.setSpan(new StyleSpan(Typeface.BOLD), nameStart, nameEnd, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    viewHolder.mSecondaryMessageTextView.setText(spanText);
                    viewHolder.mThirdMessageTextView.setText(timelineModel.getSubject());
                    mainContent = timelineModel.getMessage();
                } else if(timelineModel.isThread()) {
                    // 用户自己发布主题
                    viewHolder.mSecondaryContainer.setVisibility(View.GONE);
                    String createInfo = getString(R.string.format_timeline_create_thread, timelineModel.getSubject());
                    mainPrefixSpannable.append(createInfo);
                    mainPrefixSpannable.setSpan(new ForegroundColorSpan(ColorUtils.getColorFromAttr(getContext(), R.attr.theme_text_color_secondary)),
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

                SimpleImageGetter imageGetter = new SimpleImageGetter(getContext(), ConnectionUtils.IMAGE_DOWNLOAD_ALWAYS)
                        .setEmoticonSize(UIUtils.getSpDimensionPixelSize(getContext(), R.dimen.text_size_body1))
                        .setPlaceHolder(R.drawable.image_placeholder)
                        .setError(R.drawable.image_placeholder);
                Spanned spannedText = HtmlTextUtils.htmlToSpanned(mainContent, imageGetter, new HtmlTagHandler());
                SpannableStringBuilder mainSpannable = new SpannableStringBuilder();
                mainSpannable.append(mainPrefixSpannable).append(spannedText);
                viewHolder.mMessageTextView.setText(mainSpannable);

                viewHolder.mAuthorTextView.setText(timelineModel.getUserName());
                viewHolder.mDatelineTextView.setText(DateFormatUtils.formatFullDateDividByToday(timelineModel.getDateline(), mTodayPrefix));
                Picasso.with(getContext())
                        .load(ModelConverter.uidToAvatarUrl(timelineModel.getUserId()))
                        .error(R.drawable.ic_default_avatar)
                        .placeholder(R.drawable.ic_default_avatar)
                        .into(viewHolder.mAuthorAvatarImageView);
            }
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case
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
        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }


}
