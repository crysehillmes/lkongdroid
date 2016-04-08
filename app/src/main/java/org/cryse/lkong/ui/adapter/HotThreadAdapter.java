package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.util.ATEUtil;
import com.amulyakhare.textdrawable.TextDrawable;

import org.cryse.lkong.R;
import org.cryse.lkong.model.HotThreadModel;
import org.cryse.lkong.utils.ThemeUtils;
import org.cryse.widget.recyclerview.RecyclerViewHolder;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HotThreadAdapter extends SimpleRecyclerViewAdapter<HotThreadModel> {
    private String mATEKey;
    private int mAccentColor;
    private int mAccentTextColor;

    public HotThreadAdapter(Context context, String ateKey, List<HotThreadModel> mItemList) {
        super(context, mItemList);
        this.mATEKey = ateKey;
        this.mAccentColor = ThemeUtils.accentColor(mContext);
        this.mAccentTextColor = ATEUtil.isColorLight(mAccentColor) ? Color.BLACK : Color.WHITE;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hot_thread, parent, false);
        return new ViewHolder(v, mATEKey);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        HotThreadModel hotThreadModel = getItem(position);
        viewHolder.titleTextView.setText(hotThreadModel.subject);
        TextDrawable drawable = TextDrawable.builder(mContext)
                .beginConfig()
                .textColor(mAccentTextColor)
                .endConfig()
                // use buildRect(String, int) for literal color value
                .buildRound(Integer.toString(position + 1), mAccentColor);
        viewHolder.iconTextView.setImageDrawable(drawable);
    }

    public static class ViewHolder extends RecyclerViewHolder {
        @Bind(R.id.item_hot_thread_imageview_rank)
        ImageView iconTextView;
        @Bind(R.id.item_hot_thread_textview_subject)
        TextView titleTextView;
        public ViewHolder(View itemView, String ateKey) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            // ATE.apply(itemView, ateKey);
        }
    }
}
