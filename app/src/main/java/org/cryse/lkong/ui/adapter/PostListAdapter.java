package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.utils.ImageLoader;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.widget.PostItemView;
import org.cryse.widget.recyclerview.RecyclerViewHolder;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class PostListAdapter extends SimpleRecyclerViewAdapter<PostModel> {
    private static final String LOG_TAG = PostListAdapter.class.getName();
    public static final String POST_PICASSO_TAG = "picasso_post_list_adapter";
    private OnItemButtonClickListener mOnItemButtonClickListener;
    private PostItemView.OnSpanClickListener mOnSpanClickListener;
    private String mATEKey;
    private long mUserId;
    private int mImageDownloadPolicy;
    private int mAvatarDownloadPolicy;
    private final CircleTransform mCircleTransform;
    private final int mAvatarSize;
    private boolean mShouldShowImages;

    public PostListAdapter(Context context, String ateKey, List<PostModel> mItemList, long userId, int imageDownloadPolicy, int avatarDownloadPolicy) {
        super(context, mItemList);
        mATEKey = ateKey;
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
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(v, mATEKey, mOnItemButtonClickListener, mOnSpanClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        ViewHolder viewHolder = (ViewHolder) holder;
        PostModel postModel = getItem(position);

        viewHolder.mPostItemView.setPostId(postModel.getPid());
        viewHolder.mPostItemView.setIdentityTag(Long.toString(postModel.getPid()));
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
        ImageLoader.loadAvatar(
                mContext,
                viewHolder.mAvatarImageView,
                postModel.getAuthorAvatar(),
                mAvatarSize,
                mCircleTransform,
                mAvatarDownloadPolicy
        );

    }

    public void setUserId(long userId) {
        if(userId != mUserId) {
            this.mUserId = userId;
            notifyDataSetChanged();
        }
    }

    public static class ViewHolder extends RecyclerViewHolder {
        // each data item is just a string in this case
        @Bind(R.id.recyclerview_item_post_cardview_root_container)
        CardView mRootCardView;
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
        public ViewHolder(View itemView, String ateKey, OnItemButtonClickListener onItemReplyClickListener, PostItemView.OnSpanClickListener mOnSpanClickListener) {
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
            // ATE.apply(itemView, ateKey);
            mRootCardView.setCardBackgroundColor(Config.textColorPrimaryInverse(itemView.getContext(), ateKey));
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
