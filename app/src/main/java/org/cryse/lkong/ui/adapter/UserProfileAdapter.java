package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.ColorUtils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_PROFILE_HEADER = 0;
    public static final int TYPE_TIMELINE_ITEM = 2;
    public static final int TYPE_THREAD_ITEM = 4;

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final int MAX_PHOTO_ANIMATION_DELAY = 600;

    private static final int MIN_ITEMS_COUNT = 1;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();


    private final Context context;
    private final Picasso mPicasso;
    private final int avatarSize;
    private int mColorAccent;

    private final String profilePhoto;
    private final List<Object> mItemList;
    private UserInfoModel mUserInfo;
    private boolean lockedAnimations = false;
    private long profileHeaderAnimationStartTime = 0;
    private int lastAnimatedItem = 0;
    private int mPrimaryColor;
    private final String mTodayPrefix;
    private final String mImageTaskTag;
    private final int mAvatarSize;
    private CircleTransform mCircleTransform = new CircleTransform();
    public UserProfileAdapter(Context context, Picasso picasso, String profilePhoto, int primaryColor, String imgTaskTag, List<Object> itemList) {
        this.context = context;
        this.mPicasso = picasso;
        this.avatarSize = context.getResources().getDimensionPixelSize(R.dimen.size_avatar_user_profile);
        this.profilePhoto = profilePhoto;
        this.mItemList = itemList;
        this.mPrimaryColor = primaryColor;
        this.mTodayPrefix = context.getString(R.string.datetime_today);
        this.mImageTaskTag = imgTaskTag;
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mColorAccent = ColorUtils.getColorFromAttr(context, R.attr.colorAccent);
    }

    public void setPrimaryColor(int primaryColor) {
        this.mPrimaryColor = primaryColor;
    }

    public void setUserInfo(UserInfoModel userInfo) {
        this.mUserInfo = userInfo;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        int headerCount = mUserInfo == null ? 0 : 1;
        int index = position - headerCount;
        if (position == 0) {
            return TYPE_PROFILE_HEADER;
        } else if(mItemList.get(index) instanceof TimelineModel) {
            return TYPE_TIMELINE_ITEM;
        } else if(mItemList.get(index) instanceof ThreadModel){
            return TYPE_THREAD_ITEM;
        } else {
            throw new IllegalStateException("Unknown item type.");
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_PROFILE_HEADER == viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_profile_header, parent, false);
            return new ProfileHeaderViewHolder(view);
        } else if (TYPE_TIMELINE_ITEM == viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_timeline, parent, false);
            return new TimelineAdapter.ViewHolder(view);
        } else if(TYPE_THREAD_ITEM == viewType) {
            final View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_item_thread, parent, false);
            return new ThreadListAdapter.ViewHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        int headerCount = mUserInfo == null ? 0 : 1;
        int index = position - headerCount;
        Object item = mItemList.size() > 0 && index >= 0 ? mItemList.get(index) : null;
        if (TYPE_PROFILE_HEADER == viewType) {
            bindProfileHeader((ProfileHeaderViewHolder) holder);
        } else if (TYPE_TIMELINE_ITEM == viewType && item != null && item instanceof TimelineModel) {
            bindPhoto((TimelineAdapter.ViewHolder) holder, position, (TimelineModel) item);
        } else if (TYPE_THREAD_ITEM == viewType && item != null && item instanceof ThreadModel) {
            bindThread((ThreadListAdapter.ViewHolder)holder, position, (ThreadModel) item);
        }
    }

    private void bindProfileHeader(final ProfileHeaderViewHolder holder) {
        holder.itemView.setBackgroundColor(mPrimaryColor);
        Picasso.with(context)
                .load(profilePhoto)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransform())
                .into(holder.ivUserProfilePhoto);
        // Set user info values
        if(mUserInfo != null) {
            holder.userNameTextView.setText(mUserInfo.getUserName());
            holder.statusTextView.setText(mUserInfo.getCustomStatus());
            holder.followerCountTextView.setText(Integer.toString(mUserInfo.getFansCount()));
            holder.followingCountTextView.setText(Integer.toString(mUserInfo.getFollowCount()));
            holder.threadCountTextView.setText(Integer.toString(mUserInfo.getThreads()));
            holder.postCountTextView.setText(Integer.toString(mUserInfo.getPosts()));
        }
        // Animate
        holder.vUserProfileRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.vUserProfileRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                animateUserProfileHeader(holder);
                return false;
            }
        });
        holder.vButtons.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.vButtons.getViewTreeObserver().removeOnPreDrawListener(this);
                holder.vUnderline.getLayoutParams().width = holder.btnGrid.getWidth();
                holder.vUnderline.requestLayout();
                animateUserProfileOptions(holder);
                return false;
            }
        });
    }

    private void bindPhoto(final TimelineAdapter.ViewHolder holder, int position, TimelineModel timelineModel) {
        TimelineAdapter.bindTimelineItem(
                context,
                mPicasso,
                mTodayPrefix,
                mImageTaskTag,
                mAvatarSize,
                mCircleTransform,
                holder,
                timelineModel
        );
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void bindThread(final ThreadListAdapter.ViewHolder holder, int position, ThreadModel threadModel) {
        ThreadListAdapter.bindThreadModel(
                context,
                mPicasso,
                mTodayPrefix,
                mImageTaskTag,
                mAvatarSize,
                mColorAccent,
                mCircleTransform,
                holder,
                threadModel);
        if (lastAnimatedItem < position) lastAnimatedItem = position;
    }

    private void animateUserProfileHeader(ProfileHeaderViewHolder viewHolder) {
        if (!lockedAnimations) {
            profileHeaderAnimationStartTime = System.currentTimeMillis();

            viewHolder.vUserProfileRoot.setTranslationY(-viewHolder.vUserProfileRoot.getHeight());
            viewHolder.ivUserProfilePhoto.setTranslationY(-viewHolder.ivUserProfilePhoto.getHeight());
            viewHolder.vUserDetails.setTranslationY(-viewHolder.vUserDetails.getHeight());
            viewHolder.vUserStats.setAlpha(0);

            viewHolder.vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
            viewHolder.ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
            viewHolder.vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
            viewHolder.vUserStats.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
        }
    }

    private void animateUserProfileOptions(ProfileHeaderViewHolder viewHolder) {
        if (!lockedAnimations) {
            viewHolder.vButtons.setTranslationY(-viewHolder.vButtons.getHeight());
            viewHolder.vButtons.setAlpha(0f);
            viewHolder.vUnderline.setScaleX(0);

            viewHolder.vButtons.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
            viewHolder.vButtons.animate().alpha(1f).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
            viewHolder.vUnderline.animate().scaleX(1).setDuration(200).setStartDelay(USER_OPTIONS_ANIMATION_DELAY + 300).setInterpolator(INTERPOLATOR).start();
        }
    }

    private void animatePhoto(TimelineAdapter.ViewHolder viewHolder) {
        if (!lockedAnimations) {
            if (lastAnimatedItem == viewHolder.getPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = profileHeaderAnimationStartTime + MAX_PHOTO_ANIMATION_DELAY - System.currentTimeMillis();
            if (profileHeaderAnimationStartTime == 0) {
                animationDelay = viewHolder.getPosition() * 30 + MAX_PHOTO_ANIMATION_DELAY;
            } else if (animationDelay < 0) {
                animationDelay = viewHolder.getPosition() * 30;
            } else {
                animationDelay += viewHolder.getPosition() * 30;
            }

            viewHolder.mRootCardView.setScaleY(0);
            viewHolder.mRootCardView.setScaleX(0);
            viewHolder.mRootCardView.animate()
                    .scaleY(1)
                    .scaleX(1)
                    .setDuration(200)
                    .setInterpolator(INTERPOLATOR)
                    .setStartDelay(animationDelay)
                    .start();
        }
    }

    @Override
    public int getItemCount() {
        if(mUserInfo == null)
            return 0;
        return MIN_ITEMS_COUNT + mItemList.size();
    }

    static class ProfileHeaderViewHolder extends RecyclerView.ViewHolder {
        @InjectView(R.id.ivUserProfilePhoto)
        ImageView ivUserProfilePhoto;
        @InjectView(R.id.vUserDetails)
        View vUserDetails;
        @InjectView(R.id.btnFollow)
        Button btnFollow;
        @InjectView(R.id.vUserStats)
        View vUserStats;
        @InjectView(R.id.vUserProfileRoot)
        View vUserProfileRoot;


        @InjectView(R.id.recyclerview_item_profile_header_user_name_textview)
        TextView userNameTextView;
        @InjectView(R.id.recyclerview_item_profile_header_extra_info_textview)
        TextView extraInfoTextView;
        @InjectView(R.id.recyclerview_item_profile_header_status_textview)
        TextView statusTextView;
        @InjectView(R.id.recyclerview_item_profile_header_follower_count_textview)
        TextView followerCountTextView;
        @InjectView(R.id.recyclerview_item_profile_header_following_count_textview)
        TextView followingCountTextView;
        @InjectView(R.id.recyclerview_item_profile_header_thread_count_textview)
        TextView threadCountTextView;
        @InjectView(R.id.recyclerview_item_profile_header_post_count_textview)
        TextView postCountTextView;


        @InjectView(R.id.btnGrid)
        ImageButton btnGrid;
        @InjectView(R.id.btnList)
        ImageButton btnList;
        @InjectView(R.id.btnMap)
        ImageButton btnMap;
        @InjectView(R.id.btnTagged)
        ImageButton btnComments;
        @InjectView(R.id.vUnderline)
        View vUnderline;
        @InjectView(R.id.vButtons)
        View vButtons;
        public ProfileHeaderViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    public void clear() {
        int headerCount = mUserInfo == null ? 0 : 1;
        int itemCount = mItemList.size();
        mItemList.clear();
        notifyItemRangeRemoved(headerCount, itemCount);
    }

    public void addAll(List items) {
        int headerCount = mUserInfo == null ? 0 : 1;
        int itemCount = mItemList.size();
        mItemList.addAll(items);
        notifyItemRangeInserted(headerCount + itemCount, items.size());
    }
}

