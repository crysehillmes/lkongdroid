package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.Config;

import org.cryse.lkong.R;
import org.cryse.lkong.model.NoticeModel;
import org.cryse.lkong.utils.htmltextview.HtmlTagHandler;
import org.cryse.lkong.utils.htmltextview.HtmlTextUtils;
import org.cryse.widget.recyclerview.RecyclerViewHolder;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class NoticeCollectionAdapter extends SimpleRecyclerViewAdapter<NoticeModel> {
    private String mATEKey;
    public NoticeCollectionAdapter(Context context, String ateKey, List<NoticeModel> items) {
        super(context, items);
        this.mATEKey = ateKey;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notice, parent, false);
        return new ViewHolder(v, mATEKey);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        NoticeModel noticeModel = getItem(position);

        Spanned spannedText = HtmlTextUtils.htmlToSpanned(noticeModel.getNoticeNote(), null, new HtmlTagHandler());
        viewHolder.mNoticeMessageTextView.setText(spannedText);
    }

    public static class ViewHolder extends RecyclerViewHolder {
        @Bind(R.id.recyclerview_item_notice_cardview_root_container)
        public CardView mRootCardView;
        @Bind(R.id.recyclerview_item_notice_textview_message)
        public TextView mNoticeMessageTextView;

        public ViewHolder(View v, String ateKey) {
            super(v);
            ButterKnife.bind(this, v);
            // ATE.apply(itemView, ateKey);
            mRootCardView.setCardBackgroundColor(Config.textColorPrimaryInverse(itemView.getContext(), ateKey));
        }
    }
}
