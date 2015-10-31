package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.widget.PostItemView;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class PostListAdapter extends RecyclerViewBaseAdapter<PostModel> {
    private static final String LOG_TAG = PostListAdapter.class.getName();
    public static final String POST_PICASSO_TAG = "picasso_post_list_adapter";
    private OnItemButtonClickListener mOnItemButtonClickListener;
    private PostItemView.OnSpanClickListener mOnSpanClickListener;
    private long mUserId;
    private int mImageDownloadPolicy;
    private int mAvatarDownloadPolicy;
    private final CircleTransform mCircleTransform;
    private final int mAvatarSize;
    private boolean mShouldShowImages;

    public PostListAdapter(Context context, List<PostModel> mItemList, long userId, int imageDownloadPolicy, int avatarDownloadPolicy) {
        super(context, mItemList);
        mUserId = userId;
        mImageDownloadPolicy = imageDownloadPolicy;
        mAvatarDownloadPolicy = avatarDownloadPolicy;
        mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        mShouldShowImages = ImageLoader.shouldDownloadImage(mImageDownloadPolicy);
        mCircleTransform = new CircleTransform(context);
    }

    public void setImageDownloadPolicy(int imageDownloadPolicy) {
        mImageDownloadPolicy = imageDownloadPolicy;
        mShouldShowImages = ImageLoader.shouldDownloadImage(mImageDownloadPolicy);
    }

    public void setAvatarDownloadPolicy(int avatarDownloadPolicy) {
        mAvatarDownloadPolicy = avatarDownloadPolicy;
    }

    public void setOnItemButtonClickListener(OnItemButtonClickListener onItemButtonClickListener) {
        this.mOnItemButtonClickListener = onItemButtonClickListener;
    }
    public void setOnSpanClickListener(PostItemView.OnSpanClickListener onSpanClickListener) {
        this.mOnSpanClickListener = onSpanClickListener;
    }

    @Override
    public RecyclerViewHolder onCreateItemViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item_post, parent, false);
        return new ViewHolder(this, v, mOnItemButtonClickListener, mOnSpanClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        int headerCount = getHeaderViewCount();
        if (position >= headerCount && position < headerCount + mObjectList.getItemCount()) {
            ViewHolder viewHolder = (ViewHolder) holder;
            PostModel postModel = getItem(position - headerCount);

            viewHolder.mPostItemView.setPostId(postModel.getPid());
            viewHolder.mPostItemView.setIdentityTag(Long.toString(postModel.getPid()));
            viewHolder.mPostItemView.setPicassoTag(POST_PICASSO_TAG);
            viewHolder.mPostItemView.setShowImages(mShouldShowImages);
            viewHolder.mPostItemView.setPostDisplayCache(postModel.getPostDisplayCache());
            viewHolder.mPostItemView.setOrdinal(Integer.toString(postModel.getOrdinal()));

            /*if (postModel.getRateScore() != 0) {
                viewHolder.mRateTextView.setVisibility(View.VISIBLE);
                viewHolder.mRateTextView.setText("+ " + postModel.getRateScore());
            } else {
                viewHolder.mRateTextView.setVisibility(View.INVISIBLE);
                viewHolder.mRateTextView.setText("");
            }*/
            ImageLoader.loadAvatar(
                    getContext(),
                    viewHolder.mAvatarImageView,
                    postModel.getAuthorAvatar(),
                    mAvatarSize,
                    mCircleTransform,
                    mAvatarDownloadPolicy
            );
        }

    }

    public void setUserId(long userId) {
        if(userId != mUserId) {
            this.mUserId = userId;
            notifyDataSetChanged();
        }
    }

    void buildPopup(final ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        int headerCount = getHeaderViewCount();
        if (position >= headerCount && position < headerCount + mObjectList.getItemCount()) {
            PostModel postModel = getItem(position - headerCount);
            PopupMenu p = new PopupMenu(mContext, viewHolder.mPopupButton);
            p.inflate(R.menu.menu_popup_post_item);
            final Menu menu = p.getMenu();
            if (postModel.getAuthorId() == mUserId) {
                menu.findItem(R.id.action_edit).setVisible(true);
            } else {
                menu.findItem(R.id.action_edit).setVisible(false);
            }

            if (postModel.getRateScore() != 0) {
                menu.findItem(R.id.action_rate_log).setVisible(true);
            } else {
                menu.findItem(R.id.action_rate_log).setVisible(false);
            }

            p.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                View menuButton = viewHolder.mPopupButton;
                if (mOnItemButtonClickListener != null) {
                    switch (item.getItemId()) {
                        case R.id.action_rate:
                            mOnItemButtonClickListener.onRateClick(menuButton, position);
                            break;
                        case R.id.action_rate_log:
                            mOnItemButtonClickListener.onRateTextClick(menuButton, position);
                            break;
                        case R.id.action_edit:
                            mOnItemButtonClickListener.onEditClick(menuButton, position);
                            break;
                        case R.id.action_share:
                            mOnItemButtonClickListener.onShareClick(menuButton, position);
                            break;
                        case R.id.action_reply:
                            mOnItemButtonClickListener.onReplyClick(menuButton, position);
                            break;
                    }
                }
                return true;
            });

            // Pop up!
            p.show();
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case
        WeakReference<PostListAdapter> mAdapter;
        @Bind(R.id.recyclerview_item_post_view_item)
        PostItemView mPostItemView;
        @Bind(R.id.recyclerview_item_post_imageview_avatar)
        ImageView mAvatarImageView;
        @Bind(R.id.recyclerview_item_post_button_overflow)
        ImageButton mPopupButton;

        OnItemButtonClickListener mOnItemButtonClickListener;
        public ViewHolder(PostListAdapter adapter, View itemView, OnItemButtonClickListener onItemReplyClickListener, PostItemView.OnSpanClickListener mOnSpanClickListener) {
            super(itemView);
            mAdapter = new WeakReference<PostListAdapter>(adapter);
            ButterKnife.bind(this, itemView);
            mOnItemButtonClickListener = onItemReplyClickListener;
            View.OnClickListener clickListener = view -> {
                int adapterPosition = getAdapterPosition();
                if(mOnItemButtonClickListener != null) {
                    switch (view.getId()) {
                        case R.id.recyclerview_item_post_imageview_avatar:
                            mOnItemButtonClickListener.onProfileImageClick(view, adapterPosition);
                            break;
                    }
                }
            };
            mAvatarImageView.setOnClickListener(clickListener);
            mPopupButton.setOnClickListener(view -> {
                if(mAdapter != null)
                    mAdapter.get().buildPopup(this);
            });
            if(mOnSpanClickListener != null)
                mPostItemView.setOnSpanClickListener(mOnSpanClickListener);
            mPostItemView.setLongClickable(true);
            mPostItemView.setOnTextLongPressedListener(view -> {
                if (mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onPostTextLongClick(view, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemButtonClickListener {
        void onPostTextLongClick(View view, int position);
        void onRateClick(View view, int position);
        void onRateTextClick(View view, int position);
        void onShareClick(View view, int position);
        void onReplyClick(View view, int position);
        void onEditClick(View view, int position);
        void onProfileImageClick(View view, int position);
    }
}
