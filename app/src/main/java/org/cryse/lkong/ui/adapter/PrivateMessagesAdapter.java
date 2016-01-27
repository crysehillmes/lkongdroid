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

import com.afollestad.appthemeengine.ATE;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PrivateMessageModel;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewHolder;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class PrivateMessagesAdapter extends SimpleRecyclerViewAdapter<PrivateMessageModel> {
    private static final String LOG_TAG = PrivateMessagesAdapter.class.getSimpleName();
    private static final int TYPE_SEND = 1;
    private static final int TYPE_RECEIVE = 2;

    private String mATEKey;
    private Fragment mParentFragment;
    private final CircleTransform mCircleTransform;
    private final String mTodayPrefix;
    private final int mAvatarSize;
    private int mAvatarLoadPolicy;

    public PrivateMessagesAdapter(Fragment parentFragment, String ateKey, List<PrivateMessageModel> itemList, int avatarLoadPolicy) {
        super(parentFragment.getContext(), itemList);
        this.mATEKey = ateKey;
        this.mParentFragment = parentFragment;
        this.mTodayPrefix = mContext.getString(R.string.text_datetime_today);
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(parentFragment.getContext());
        this.mCircleTransform = new CircleTransform(mContext);
        this.mAvatarLoadPolicy = avatarLoadPolicy;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SEND:
                view = inflater.inflate(R.layout.item_private_message_item_send, parent, false);
                return new PrivateMessageViewHolder(view, mATEKey);
            case TYPE_RECEIVE:
            default:
                view = inflater.inflate(R.layout.item_private_message_item_receive, parent, false);
                return new PrivateMessageViewHolder(view, mATEKey);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        PrivateMessageViewHolder viewHolder = (PrivateMessageViewHolder) holder;
        PrivateMessageModel model = getItem(position);
        viewHolder.mMessageTextView.setText(Html.fromHtml(model.getMessage()));
        viewHolder.mDatelineTextView.setText(TimeFormatUtils.getTimeAgo(mContext, model.getDateline().getTime()));
        if (viewHolder.mAvatarImageView != null) {
            ImageLoader.loadAvatar(
                    mParentFragment,
                    viewHolder.mAvatarImageView,
                    model.getAvatarUrl(),
                    mAvatarSize,
                    mCircleTransform,
                    mAvatarLoadPolicy
            );
        }

    }

    @Override
    public int getItemViewType(int position) {
        PrivateMessageModel model = getItem(position);
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

        public PrivateMessageViewHolder(View itemView, String ateKey) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            ATE.apply(itemView, ateKey);
            mMessageTextView.setMovementMethod(
                    LinkMovementMethod.getInstance()
            );
        }
    }
}
