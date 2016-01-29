package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;

import org.cryse.lkong.R;
import org.cryse.lkong.model.SearchUserItem;
import org.cryse.lkong.ui.listener.OnItemProfileAreaClickListener;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.widget.recyclerview.RecyclerViewHolder;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UsersAdapter extends SimpleRecyclerViewAdapter<SearchUserItem> {
    private String mATEKey;
    private final int mAvatarSize;
    private int mAvatarLoadPolicy;

    OnUserItemClickListener mOnUserItemClickListener;
    private CircleTransform mCircleTransform;
    public UsersAdapter(Context context, String ateKey, List<SearchUserItem> mItemList, int avatarLoadPolicy) {
        super(context, mItemList);
        this.mATEKey = ateKey;
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mCircleTransform = new CircleTransform(mContext);
        this.mAvatarLoadPolicy = avatarLoadPolicy;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_user, parent, false);
        return new ViewHolder(v, mATEKey, mOnUserItemClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        SearchUserItem userItem = getItem(position);
        viewHolder.nameTextView.setText(userItem.getUserName());
        viewHolder.signTextView.setText(userItem.getSignHtml());
        ImageLoader.loadAvatar(
                mContext,
                viewHolder.avatarImageView,
                userItem.getAvatarUrl(),
                mAvatarSize,
                mCircleTransform,
                mAvatarLoadPolicy
        );
    }

    public void setOnUserItemClickListener(OnUserItemClickListener listener) {
        this.mOnUserItemClickListener = listener;
    }

    public static class ViewHolder extends RecyclerViewHolder {
        private OnUserItemClickListener mOnUserItemClickListener;

        @Bind(R.id.recyclerview_item_search_user_icon)
        ImageView avatarImageView;
        @Bind(R.id.recyclerview_item_search_user_name)
        TextView nameTextView;
        @Bind(R.id.recyclerview_item_search_user_sign)
        TextView signTextView;
        public ViewHolder(View itemView, String ateKey, OnUserItemClickListener onUserItemClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mOnUserItemClickListener = onUserItemClickListener;
            itemView.setOnClickListener(view -> {
                if(mOnUserItemClickListener != null) {
                    mOnUserItemClickListener.onProfileAreaClick(view, getAdapterPosition(), 0);
                }
            });
            ATE.apply(itemView, ateKey);
        }
    }

    public interface OnUserItemClickListener extends OnItemProfileAreaClickListener {

    }
}
