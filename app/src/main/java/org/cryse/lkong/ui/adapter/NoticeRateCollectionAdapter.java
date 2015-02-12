package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cryse.lkong.R;
import org.cryse.lkong.model.NoticeRateModel;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NoticeRateCollectionAdapter extends RecyclerViewBaseAdapter<NoticeRateModel> {
    public NoticeRateCollectionAdapter(Context context, List<NoticeRateModel> items) {
        super(context, items);
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_notice, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof NoticeRateModel) {
                NoticeRateModel noticeRateModel = (NoticeRateModel)item;

                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                String prefix = getString(R.string.format_note_rate_log_prefix);
                spannableStringBuilder.append(prefix);
                spannableStringBuilder.append(noticeRateModel.getMessage());

                String middle = getString(R.string.format_note_rate_log_middle, noticeRateModel.getUserName());
                spannableStringBuilder.append(middle);
                String suffix = getString(R.string.format_note_rate_log_suffix, noticeRateModel.getExtCredits(), noticeRateModel.getScore());
                spannableStringBuilder.append(suffix);
                spannableStringBuilder.append('\n').append(noticeRateModel.getReason());
                viewHolder.mNoticeMessageTextView.setText(spannableStringBuilder);
            }
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        @InjectView(R.id.recyclerview_item_notice_textview_message)
        public TextView mNoticeMessageTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
