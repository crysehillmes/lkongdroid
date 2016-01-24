package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;

import org.cryse.lkong.R;
import org.cryse.lkong.model.NoticeRateModel;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class NoticeRateCollectionAdapter extends RecyclerViewBaseAdapter<NoticeRateModel> {
    private String mATEKey;
    public NoticeRateCollectionAdapter(Context context, String ateKey, List<NoticeRateModel> items) {
        super(context, items);
        this.mATEKey = ateKey;
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice_rate, parent, false);
        return new ViewHolder(v, mATEKey);
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
        @Bind(R.id.recyclerview_item_notice_rate_cardview_root_container)
        public CardView mRootCardView;
        @Bind(R.id.recyclerview_item_notice_textview_message)
        public TextView mNoticeMessageTextView;

        public ViewHolder(View v, String ateKey) {
            super(v);
            ButterKnife.bind(this, v);
            ATE.apply(itemView, ateKey);
            mRootCardView.setCardBackgroundColor(Config.textColorPrimaryInverse(itemView.getContext(), ateKey));
        }
    }
}
