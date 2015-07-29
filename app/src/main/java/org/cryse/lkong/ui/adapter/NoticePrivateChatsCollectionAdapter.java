package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.ColorUtils;
import org.cryse.utils.DateFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NoticePrivateChatsCollectionAdapter extends RecyclerViewBaseAdapter<PrivateChatModel> {
    public static final String PRIVATE_CHATS_PICASSO_TAG = "PRIVATE_CHATS_PICASSO_TAG";
    private CircleTransform mCircleTransform = new CircleTransform();
    private final String mTodayPrefix;
    private int mColorAccent;
    private final int mAvatarSize;
    Picasso mPicasso;
    private String mPicassoTag;
    public NoticePrivateChatsCollectionAdapter(Context context, Picasso picasso, List<PrivateChatModel> items) {
        super(context, items);
        this.mPicasso = picasso;
        this.mTodayPrefix = getString(R.string.datetime_today);
        this.mColorAccent = ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mPicassoTag = PRIVATE_CHATS_PICASSO_TAG;
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
                viewHolder.mUserNameTextView.setText(model.getUserName());
                viewHolder.mDatelineTextView.setText(DateFormatUtils.formatDateDividByToday(model.getDateline(), mTodayPrefix));
                mPicasso
                        .load(model.getTargetUserAvatar())
                        .tag(mPicassoTag)
                        .error(R.drawable.ic_placeholder_avatar)
                        .placeholder(R.drawable.ic_placeholder_avatar)
                        .resize(mAvatarSize, mAvatarSize)
                        .transform(mCircleTransform)
                        .into(viewHolder.mAvatarImageView);
            }
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        @InjectView(R.id.recyclerview_item_private_chat_imageview_icon)
        public ImageView mAvatarImageView;
        @InjectView(R.id.recyclerview_item_private_chat_textview_message)
        public TextView mChatMessageTextView;
        @InjectView(R.id.recyclerview_item_private_chat_textview_username)
        public TextView mUserNameTextView;
        @InjectView(R.id.recyclerview_item_private_chat_textview_dateline)
        public TextView mDatelineTextView;

        public ViewHolder(View v) {
            super(v);
            ButterKnife.inject(this, v);
        }
    }
}
