package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.widget.PostItemView;
import org.cryse.utils.ColorUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class PostListAdapter extends RecyclerViewBaseAdapter<PostModel> {
    private static final String LOG_TAG = PostListAdapter.class.getName();
    public static final String POST_PICASSO_TAG = "picasso_post_list_adapter";
    private final String mTodayPrefix;
    private OnItemButtonClickListener mOnItemButtonClickListener;
    private PostItemView.OnSpanClickListener mOnSpanClickListener;
    private long mUserId;
    private int mMaxImageWidth;
    private int mImageDownloadPolicy;
    private final CircleTransform mCircleTransform;
    private final int mAvatarSize;
    private boolean mShouldShowImages;
    private int mAccentColor;

    public PostListAdapter(Context context, List<PostModel> mItemList, long userId, int imageDownloadPolicy) {
        super(context, mItemList);
        mTodayPrefix = getString(R.string.datetime_today);
        mMaxImageWidth = UIUtils.dp2px(context, 128f);
        mUserId = userId;
        mImageDownloadPolicy = imageDownloadPolicy;
        mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        mShouldShowImages = LKongApplication.get(mContext).getNetworkPolicyManager().shouldDownloadImage(mImageDownloadPolicy);
        mAccentColor = ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent);
        mCircleTransform = new CircleTransform(context);
    }

    public void setImageDownloadPolicy(int imageDownloadPolicy) {
        mImageDownloadPolicy = imageDownloadPolicy;
        mShouldShowImages = LKongApplication.get(mContext).getNetworkPolicyManager().shouldDownloadImage(mImageDownloadPolicy);
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
        return new ViewHolder(v, mOnItemButtonClickListener, mOnSpanClickListener);
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

            if (postModel.getRateScore() != 0) {
                viewHolder.mRateTextView.setVisibility(View.VISIBLE);
                viewHolder.mRateTextView.setText("+ " + postModel.getRateScore());
            } else {
                viewHolder.mRateTextView.setVisibility(View.INVISIBLE);
                viewHolder.mRateTextView.setText("");
            }

            if (postModel.getAuthorId() == mUserId) {
                viewHolder.mEditButton.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mEditButton.setVisibility(View.INVISIBLE);
            }

            if (postModel.getAuthorId() == mUserId) {
                viewHolder.mEditButton.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mEditButton.setVisibility(View.INVISIBLE);
            }

            Glide.with(getContext()).load(postModel.getAuthorAvatar())
                    .error(R.drawable.ic_placeholder_avatar)
                    .placeholder(R.drawable.ic_placeholder_avatar)
                    .override(mAvatarSize, mAvatarSize)
                    .transform(mCircleTransform)
                    .into(viewHolder.mAvatarImageView);
        }

    }

    public void setUserId(long userId) {
        if(userId != mUserId) {
            this.mUserId = userId;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.recyclerview_item_post_view_item)
        PostItemView mPostItemView;
        @Bind(R.id.recyclerview_item_post_imageview_avatar)
        ImageView mAvatarImageView;
        @Bind(R.id.recyclerview_item_post_button_rate)
        ImageButton mRateButton;
        @Bind(R.id.recyclerview_item_post_button_share)
        ImageButton mShareButton;
        @Bind(R.id.recyclerview_item_post_textview_rate)
        TextView mRateTextView;
        @Bind(R.id.recyclerview_item_post_button_edit)
        ImageButton mEditButton;
        @Bind(R.id.recyclerview_item_post_button_replay)
        ImageButton mReplyButton;

        OnItemButtonClickListener mOnItemButtonClickListener;
        public ViewHolder(View itemView, OnItemButtonClickListener onItemReplyClickListener, PostItemView.OnSpanClickListener mOnSpanClickListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mOnItemButtonClickListener = onItemReplyClickListener;
            View.OnClickListener clickListener = view -> {
                int adapterPosition = getAdapterPosition();
                if(mOnItemButtonClickListener != null) {
                    switch (view.getId()) {
                        case R.id.recyclerview_item_post_button_replay:
                            mOnItemButtonClickListener.onReplyClick(view, adapterPosition);
                            break;
                        case R.id.recyclerview_item_post_button_rate:
                            mOnItemButtonClickListener.onRateClick(view, adapterPosition);
                            break;
                        case R.id.recyclerview_item_post_textview_rate:
                            mOnItemButtonClickListener.onRateTextClick(view, adapterPosition);
                            break;
                        case R.id.recyclerview_item_post_button_edit:
                            mOnItemButtonClickListener.onEditClick(view, adapterPosition);
                            break;
                        case R.id.recyclerview_item_post_button_share:
                            mOnItemButtonClickListener.onShareClick(view, adapterPosition);
                            break;
                        case R.id.recyclerview_item_post_imageview_avatar:
                            mOnItemButtonClickListener.onProfileImageClick(view, adapterPosition);
                            break;
                    }
                }
            };
            mReplyButton.setOnClickListener(clickListener);
            mRateButton.setOnClickListener(clickListener);
            mEditButton.setOnClickListener(clickListener);
            mAvatarImageView.setOnClickListener(clickListener);
            mRateTextView.setOnClickListener(clickListener);
            mShareButton.setOnClickListener(clickListener);
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
