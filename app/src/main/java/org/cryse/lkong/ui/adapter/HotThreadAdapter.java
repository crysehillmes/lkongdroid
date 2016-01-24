package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.util.Util;
import com.amulyakhare.textdrawable.TextDrawable;

import org.cryse.lkong.R;
import org.cryse.lkong.model.HotThreadModel;
import org.cryse.lkong.model.SearchUserItem;
import org.cryse.lkong.ui.listener.OnItemProfileAreaClickListener;
import org.cryse.lkong.ui.listener.OnItemThreadClickListener;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.ThemeUtils;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HotThreadAdapter extends RecyclerViewBaseAdapter<HotThreadModel> {
    private String mATEKey;
    private int mAccentColor;
    private int mAccentTextColor;

    public HotThreadAdapter(Context context, String ateKey, List<HotThreadModel> mItemList) {
        super(context, mItemList);
        this.mATEKey = ateKey;
        this.mAccentColor = ThemeUtils.accentColor(mContext);
        this.mAccentTextColor = Util.isColorLight(mAccentColor) ? Color.BLACK : Color.WHITE;
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hot_thread, parent, false);
        return new ViewHolder(v, mATEKey);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof HotThreadModel) {
                HotThreadModel hotThreadModel = (HotThreadModel)item;
                viewHolder.titleTextView.setText(hotThreadModel.subject);
                TextDrawable drawable = TextDrawable.builder(getContext())
                        .beginConfig()
                        .textColor(mAccentTextColor)
                        .endConfig()
                        // use buildRect(String, int) for literal color value
                        .buildRound(Integer.toString(position + 1), mAccentColor);
                viewHolder.iconTextView.setImageDrawable(drawable);
            }
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        @Bind(R.id.item_hot_thread_imageview_rank)
        ImageView iconTextView;
        @Bind(R.id.item_hot_thread_textview_subject)
        TextView titleTextView;
        public ViewHolder(View itemView, String ateKey) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ATE.apply(itemView, ateKey);
        }
    }
}
