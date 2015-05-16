package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.widget.PostItemView;
import org.cryse.utils.ColorUtils;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostListAdapter extends RecyclerViewBaseAdapter<PostModel> {
    private static final String LOG_TAG = PostListAdapter.class.getName();
    public static final String POST_PICASSO_TAG = "picasso_post_list_adapter";
    private final String mTodayPrefix;
    private OnItemButtonClickListener mOnItemButtonClickListener;
    private PostItemView.OnSpanClickListener mOnSpanClickListener;
    private long mUserId;
    private int mMaxImageWidth;
    private Picasso mPicasso;
    private int mImageDownloadPolicy;
    private final CircleTransform mCircleTransform = new CircleTransform();
    private final int mAvatarSize;
    private boolean mShouldShowImages;
    private int mAccentColor;

    public PostListAdapter(Context context, Picasso picasso, List<PostModel> mItemList, long userId, int imageDownloadPolicy) {
        super(context, mItemList);
        mPicasso = picasso;
        mTodayPrefix = getString(R.string.datetime_today);
        mMaxImageWidth = UIUtils.dp2px(context, 128f);
        mUserId = userId;
        mImageDownloadPolicy = imageDownloadPolicy;
        mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        mShouldShowImages = LKongApplication.get(mContext).getNetworkPolicyManager().shouldDownloadImage(mImageDownloadPolicy);
        mAccentColor = ColorUtils.getColorFromAttr(getContext(), R.attr.colorAccent);
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
                viewHolder.mRateButton.setText("+ " + postModel.getRateScore());
            } else {
                viewHolder.mRateButton.setText("");
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

            mPicasso.load(postModel.getAuthorAvatar())
                    .tag(POST_PICASSO_TAG)
                    .error(R.drawable.ic_placeholder_avatar)
                    .placeholder(R.drawable.ic_placeholder_avatar)
                    .resize(mAvatarSize, mAvatarSize)
                    .transform(mCircleTransform)
                    .noFade()
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
        @InjectView(R.id.recyclerview_item_post_view_item)
        PostItemView mPostItemView;
        @InjectView(R.id.recyclerview_item_post_imageview_avatar)
        ImageView mAvatarImageView;
        @InjectView(R.id.recyclerview_item_post_button_rate)
        Button mRateButton;
        @InjectView(R.id.recyclerview_item_post_button_edit)
        ImageButton mEditButton;
        @InjectView(R.id.recyclerview_item_post_button_replay)
        ImageButton mReplyButton;

        OnItemButtonClickListener mOnItemButtonClickListener;
        public ViewHolder(View v, OnItemButtonClickListener onItemReplyClickListener, PostItemView.OnSpanClickListener mOnSpanClickListener) {
            super(v);
            ButterKnife.inject(this, v);
            mOnItemButtonClickListener = onItemReplyClickListener;
            mReplyButton.setOnClickListener(view -> {
                if(mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onReplyClick(view, getAdapterPosition());
                }
            });
            mRateButton.setOnClickListener(view -> {
                if(mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onRateClick(view, getAdapterPosition());
                }
            });
            mEditButton.setOnClickListener(view -> {
                if(mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onEditClick(view, getAdapterPosition());
                }
            });
            mAvatarImageView.setOnClickListener(view -> {
                if(mOnItemButtonClickListener != null) {
                    mOnItemButtonClickListener.onProfileImageClick(view, getAdapterPosition());
                }
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
        void onReplyClick(View view, int position);
        void onEditClick(View view, int position);
        void onProfileImageClick(View view, int position);
    }
}
