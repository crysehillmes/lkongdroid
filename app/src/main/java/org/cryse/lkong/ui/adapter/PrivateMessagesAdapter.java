package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PrivateMessageModel;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

public class PrivateMessagesAdapter extends RecyclerViewBaseAdapter<PrivateMessageModel> {
    private static final String LOG_TAG = PrivateMessagesAdapter.class.getSimpleName();
    private static final int TYPE_SEND = ITEM_TYPE_ITEM_START + 1;
    private static final int TYPE_RECEIVE = ITEM_TYPE_ITEM_START + 2;

    private Picasso mPicasso;
    private final CircleTransform mCircleTransform = new CircleTransform();
    private final String mTodayPrefix;

    public PrivateMessagesAdapter(Context context, Picasso picasso, List<PrivateMessageModel> itemList) {
        super(context, itemList);
        this.mPicasso = picasso;
        this.mTodayPrefix = context.getString(R.string.datetime_today);
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
                mPicasso.load(model.getAvatarUrl())
                        .placeholder(R.drawable.ic_placeholder_avatar)
                        .error(R.drawable.ic_placeholder_avatar)
                        .fit()
                        .centerCrop()
                        .transform(mCircleTransform)
                        .into(viewHolder.mAvatarImageView);
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
        @Optional
        @InjectView(R.id.recyclerview_item_private_message_imageview_avatar)
        ImageView mAvatarImageView;
        @InjectView(R.id.recyclerview_item_private_message_relativelayout_message_container)
        RelativeLayout mMessageContainer;
        @InjectView(R.id.recyclerview_item_private_message_textview_message)
        TextView mMessageTextView;
        @InjectView(R.id.recyclerview_item_private_message_textview_dateline)
        TextView mDatelineTextView;

        public PrivateMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
            mMessageTextView.setMovementMethod(
                    LinkMovementMethod.getInstance()
            );
        }
    }
}
