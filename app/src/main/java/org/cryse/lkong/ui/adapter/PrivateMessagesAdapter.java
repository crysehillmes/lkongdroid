package org.cryse.lkong.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PrivateMessageModel;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class PrivateMessagesAdapter extends RecyclerViewBaseAdapter<PrivateMessageModel> {
    private static final String LOG_TAG = PrivateMessagesAdapter.class.getSimpleName();
    private static final int TYPE_SEND = ITEM_TYPE_ITEM_START + 1;
    private static final int TYPE_RECEIVE = ITEM_TYPE_ITEM_START + 2;

    private Fragment mParentFragment;
    private final CircleTransform mCircleTransform;
    private final String mTodayPrefix;
    private final int mAvatarSize;

    public PrivateMessagesAdapter(Fragment parentFragment, List<PrivateMessageModel> itemList) {
        super(parentFragment.getContext(), itemList);
        this.mParentFragment = parentFragment;
        this.mTodayPrefix = getContext().getString(R.string.datetime_today);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(parentFragment.getContext());
        this.mCircleTransform = new CircleTransform(getContext());
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int headerCount = getHeaderViewCount();
        if (position >= headerCount && position < headerCount + mObjectList.getItemCount()) {
            PrivateMessageViewHolder viewHolder = (PrivateMessageViewHolder) holder;
            PrivateMessageModel model = getItem(position - getHeaderViewCount());
            viewHolder.mMessageTextView.setText(Html.fromHtml(model.getMessage()));
            viewHolder.mDatelineTextView.setText(TimeFormatUtils.getTimeAgo(getContext(), model.getDateline().getTime()));
            if(viewHolder.mAvatarImageView != null) {
                ImageLoader.loadAvatar(
                        mParentFragment,
                        viewHolder.mAvatarImageView,
                        model.getAvatarUrl(),
                        mAvatarSize,
                        mCircleTransform,
                        ImageLoader.IMAGE_LOAD_AVATAR_ALWAYS
                );
            }
        }

    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SEND:
                view = inflater.inflate(R.layout.recyclerview_item_private_message_item_send, parent, false);
                return new PrivateMessageViewHolder(view);
            case TYPE_RECEIVE:
            default:
                view = inflater.inflate(R.layout.recyclerview_item_private_message_item_receive, parent, false);
                return new PrivateMessageViewHolder(view);
        }
    }

    @Override
    public int onGetItemViewItemType(int position) {
        PrivateMessageModel model = getItem(position - getHeaderViewCount());
        if(model.getMessageFromId() == 0) {
            // Self
            return TYPE_SEND;
        } else {
            // Target
            return TYPE_RECEIVE;
        }
    }

    public static class PrivateMessageViewHolder extends RecyclerViewHolder {
        @Nullable
        @Bind(R.id.recyclerview_item_private_message_imageview_avatar)
        ImageView mAvatarImageView;
        @Bind(R.id.recyclerview_item_private_message_relativelayout_message_container)
        RelativeLayout mMessageContainer;
        @Bind(R.id.recyclerview_item_private_message_textview_message)
        TextView mMessageTextView;
        @Bind(R.id.recyclerview_item_private_message_textview_dateline)
        TextView mDatelineTextView;

        public PrivateMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mMessageTextView.setMovementMethod(
                    LinkMovementMethod.getInstance()
            );
        }
    }
}
