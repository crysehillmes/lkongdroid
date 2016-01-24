package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class NoticePrivateChatsCollectionAdapter extends RecyclerViewBaseAdapter<PrivateChatModel> {
    private String mATEKey;
    private CircleTransform mCircleTransform;
    private final String mTodayPrefix;
    private final int mAvatarSize;
    private int mAvatarLoadPolicy;
    public NoticePrivateChatsCollectionAdapter(Context context, String ateKey, List<PrivateChatModel> items, int avatarLoadPolicy) {
        super(context, items);
        this.mATEKey = ateKey;
        this.mTodayPrefix = getString(R.string.text_datetime_today);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mCircleTransform = new CircleTransform(getContext());
        this.mAvatarLoadPolicy = avatarLoadPolicy;
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_private_chat, parent, false);
        return new ViewHolder(v, mATEKey);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if(holder instanceof ViewHolder) {
            ViewHolder viewHolder = (ViewHolder)holder;
            Object item = getObjectItem(position);
            if(item instanceof PrivateChatModel) {
                PrivateChatModel model = (PrivateChatModel)item;

                viewHolder.mChatMessageTextView.setText(model.getMessage());
                viewHolder.mUserNameTextView.setText(model.getTargetUserName());
                viewHolder.mDatelineTextView.setText(TimeFormatUtils.formatDateDividByToday(
                        model.getDateline(),
                        mTodayPrefix,
                        getContext().getResources().getConfiguration().locale));
                ImageLoader.loadAvatar(
                        getContext(),
                        viewHolder.mAvatarImageView,
                        model.getTargetUserAvatar(),
                        mAvatarSize,
                        mCircleTransform,
                        mAvatarLoadPolicy
                );
            }
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        @Bind(R.id.recyclerview_item_private_chat_imageview_icon)
        public ImageView mAvatarImageView;
        @Bind(R.id.recyclerview_item_private_chat_textview_message)
        public TextView mChatMessageTextView;
        @Bind(R.id.recyclerview_item_private_chat_textview_username)
        public TextView mUserNameTextView;
        @Bind(R.id.recyclerview_item_private_chat_textview_dateline)
        public TextView mDatelineTextView;

        public ViewHolder(View v, String ateKey) {
            super(v);
            ButterKnife.bind(this, v);
            ATE.apply(itemView, ateKey);
        }
    }
}
