package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.ColorUtils;
import org.cryse.utils.DateFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class NoticePrivateChatsCollectionAdapter extends RecyclerViewBaseAdapter<PrivateChatModel> {
    public static final String PRIVATE_CHATS_PICASSO_TAG = "PRIVATE_CHATS_PICASSO_TAG";
    private CircleTransform mCircleTransform;
    private final String mTodayPrefix;
    private int mColorAccent;
    private final int mAvatarSize;
    private String mPicassoTag;
    public NoticePrivateChatsCollectionAdapter(Context context, List<PrivateChatModel> items) {
        super(context, items);
        this.mTodayPrefix = getString(R.string.datetime_today);
        this.mColorAccent = ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mPicassoTag = PRIVATE_CHATS_PICASSO_TAG;
        this.mCircleTransform = new CircleTransform(getContext());
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_private_chat, parent, false);
        return new ViewHolder(v);
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
                viewHolder.mDatelineTextView.setText(DateFormatUtils.formatDateDividByToday(
                        model.getDateline(),
                        mTodayPrefix,
                        getContext().getResources().getConfiguration().locale));
                Glide
                        .with(getContext())
                        .load(model.getTargetUserAvatar())
                        .error(R.drawable.ic_placeholder_avatar)
                        .placeholder(R.drawable.ic_placeholder_avatar)
                        .override(mAvatarSize, mAvatarSize)
                        .transform(mCircleTransform)
                        .into(viewHolder.mAvatarImageView);
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

        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
}
