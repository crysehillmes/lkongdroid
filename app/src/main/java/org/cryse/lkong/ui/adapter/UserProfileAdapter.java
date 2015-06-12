package org.cryse.lkong.ui.adapter;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.ui.listener.OnItemProfileAreaClickListener;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.ColorUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UserProfileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_PROFILE_HEADER = 0;
    public static final int TYPE_TIMELINE_ITEM = 2;
    public static final int TYPE_THREAD_ITEM = 4;

    public static final int LIST_ALL = 0;
    public static final int LIST_THREADS = 1;
    public static final int LIST_DIGEST = 2;

    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final int MAX_PHOTO_ANIMATION_DELAY = 600;

    private static final int MIN_ITEMS_COUNT = 1;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();


    private final Context mContext;
    private final Picasso mPicasso;
    private final int mSelfAvatarSize;
    private int mColorAccent;

    private final String mProfilePhoto;
    private final List<Object> mItemList;
    private UserInfoModel mUserInfo;
    private boolean mLockedAnimations = false;
    private long mProfileHeaderAnimationStartTime = 0;
    private int mLastAnimatedItem = 0;
    private int mPrimaryColor;
    private final String mTodayPrefix;
    private final String mImageTaskTag;
    private final int mAvatarSize;
    private int mCurrentListType = 0;
    private TabLayout.OnTabSelectedListener mOnTabListener;
    private CircleTransform mCircleTransform = new CircleTransform();
    private List<String> mOptionTabTitles;
    private OnProfileItemClickListener mOnProfileItemClickListener;

    int mTabColorNormal = 0;
    int mTabColorSelected = 0;

    public UserProfileAdapter(Context context, Picasso picasso, String profilePhoto, int primaryColor, String imgTaskTag, List<Object> itemList) {
        this.mContext = context;
        this.mPicasso = picasso;
        this.mSelfAvatarSize = context.getResources().getDimensionPixelSize(R.dimen.size_avatar_user_profile);
        this.mProfilePhoto = profilePhoto;
        this.mItemList = itemList;
        this.mPrimaryColor = primaryColor;
        this.mTodayPrefix = context.getString(R.string.datetime_today);
        this.mImageTaskTag = imgTaskTag;
        this.mAvatarSize = UIUtils.getDefaultAvatarSize(context);
        this.mColorAccent = ColorUtils.getColorFromAttr(context, R.attr.colorAccent);
        this.mOptionTabTitles = new ArrayList<String>();
        Collections.addAll(this.mOptionTabTitles, context.getResources().getStringArray(R.array.string_array_user_profile_tabs));
        this.mTabColorNormal = mContext.getResources().getColor(R.color.text_color_secondary_dark);
        this.mTabColorSelected = mContext.getResources().getColor(R.color.text_color_primary_dark);
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
            final View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_profile_header, parent, false);
            return new ProfileHeaderViewHolder(view);
        } else if (TYPE_TIMELINE_ITEM == viewType) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_timeline, parent, false);
            return new TimelineAdapter.ViewHolder(view, mOnProfileItemClickListener);
        } else if(TYPE_THREAD_ITEM == viewType) {
            final View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item_thread, parent, false);
            return new ThreadListAdapter.ViewHolder(view, mOnProfileItemClickListener);
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
            bindTimeline((TimelineAdapter.ViewHolder) holder, position, (TimelineModel) item);
        } else if (TYPE_THREAD_ITEM == viewType && item != null && item instanceof ThreadModel) {
            bindThread((ThreadListAdapter.ViewHolder)holder, position, (ThreadModel) item);
        }
    }

    private void bindProfileHeader(final ProfileHeaderViewHolder holder) {
        holder.itemView.setBackgroundColor(mPrimaryColor);
        Picasso.with(mContext)
                .load(mProfilePhoto)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .resize(mSelfAvatarSize, mSelfAvatarSize)
                .centerCrop()
                .transform(new CircleTransform())
                .into(holder.ivUserProfilePhoto);
        // Set user info values
        if(mUserInfo != null) {
            holder.mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    switch (position) {
                        case 0:
                            mCurrentListType = LIST_ALL;
                            break;
                        case 1:
                            mCurrentListType = LIST_THREADS;
                            break;
                        case 2:
                            mCurrentListType = LIST_DIGEST;
                            break;
                    }
                    if (mOnTabListener != null)
                        mOnTabListener.onTabSelected(tab);

                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            holder.mTabLayout.setBackgroundColor(mPrimaryColor);
            for(String title : mOptionTabTitles) {
                holder.mTabLayout.setTabTextColors(mTabColorNormal, mTabColorSelected);
                holder.mTabLayout.addTab(holder.mTabLayout.newTab().setText(title));
            }
            holder.userNameTextView.setText(mUserInfo.getUserName());
            holder.extraInfoTextView.setText("");
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
        holder.mTabLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                holder.mTabLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                animateUserProfileOptions(holder);
                return false;
            }
        });
    }

    private void bindTimeline(final TimelineAdapter.ViewHolder holder, int position, TimelineModel timelineModel) {
        TimelineAdapter.bindTimelineItem(
                mContext,
                mPicasso,
                mTodayPrefix,
                mImageTaskTag,
                mAvatarSize,
                mCircleTransform,
                holder,
                timelineModel
        );
        // animateTimeline(holder);
        if (mLastAnimatedItem < position) mLastAnimatedItem = position;
    }

    private void bindThread(final ThreadListAdapter.ViewHolder holder, int position, ThreadModel threadModel) {
        ThreadListAdapter.bindThreadModel(
                mContext,
                mPicasso,
                mTodayPrefix,
                mImageTaskTag,
                mAvatarSize,
                mColorAccent,
                mCircleTransform,
                holder,
                threadModel);
        // animateThread(holder);
        if (mLastAnimatedItem < position) mLastAnimatedItem = position;
    }

    private void animateUserProfileHeader(ProfileHeaderViewHolder viewHolder) {
        if (!mLockedAnimations) {
            mProfileHeaderAnimationStartTime = System.currentTimeMillis();

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
        if (!mLockedAnimations) {
            viewHolder.mTabLayout.setTranslationY(-viewHolder.mTabLayout.getHeight());
            viewHolder.mTabLayout.setAlpha(0f);

            viewHolder.mTabLayout.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
            viewHolder.mTabLayout.animate().alpha(1f).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
        }
    }

    private void animateTimeline(TimelineAdapter.ViewHolder viewHolder) {
        if (!mLockedAnimations) {
            if (mLastAnimatedItem == viewHolder.getAdapterPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = mProfileHeaderAnimationStartTime + MAX_PHOTO_ANIMATION_DELAY - System.currentTimeMillis();
            if (mProfileHeaderAnimationStartTime == 0) {
                animationDelay = viewHolder.getAdapterPosition() * 30 + MAX_PHOTO_ANIMATION_DELAY;
            } else if (animationDelay < 0) {
                animationDelay = viewHolder.getAdapterPosition() * 30;
            } else {
                animationDelay += viewHolder.getAdapterPosition() * 30;
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

    private void animateThread(ThreadListAdapter.ViewHolder viewHolder) {
        if (!mLockedAnimations) {
            if (mLastAnimatedItem == viewHolder.getAdapterPosition()) {
                setLockedAnimations(true);
            }

            long animationDelay = mProfileHeaderAnimationStartTime + MAX_PHOTO_ANIMATION_DELAY - System.currentTimeMillis();
            if (mProfileHeaderAnimationStartTime == 0) {
                animationDelay = viewHolder.getAdapterPosition() * 30 + MAX_PHOTO_ANIMATION_DELAY;
            } else if (animationDelay < 0) {
                animationDelay = viewHolder.getAdapterPosition() * 30;
            } else {
                animationDelay += viewHolder.getAdapterPosition() * 30;
            }

            viewHolder.mRootView.setScaleY(0);
            viewHolder.mRootView.setScaleX(0);
            viewHolder.mRootView.animate()
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

        @InjectView(R.id.recyclerview_item_profile_header_tablayout)
        TabLayout mTabLayout;
        public ProfileHeaderViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.mLockedAnimations = lockedAnimations;
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

    public void setOnTabListener(TabLayout.OnTabSelectedListener listener) {
        this.mOnTabListener = listener;
    }

    public int getCurrentListType() {
        return mCurrentListType;
    }

    public long getLastSortKey() {
        int itemCount = mItemList.size();
        if(itemCount > 0) {
            Object objItem = mItemList.get(itemCount - 1);
            if(objItem instanceof TimelineModel) {
                return ((TimelineModel) objItem).getSortKey();
            } else if(objItem instanceof ThreadModel) {
                return ((ThreadModel) objItem).getSortKey();
            }
        }
        return -1;
    }

    public Object getItem(int position) {
        int headerCount = mUserInfo == null ? 0 : 1;
        int index = position - headerCount;
        if(index >= 0 && mItemList.size() > 0)
            return mItemList.get(index);
        return null;
    }

    public void setOnItemProfileImageClickListener(OnProfileItemClickListener onProfileItemClickListener) {
        this.mOnProfileItemClickListener = onProfileItemClickListener;
    }

    public interface OnProfileItemClickListener extends TimelineAdapter.OnTimelineModelItemClickListener, ThreadListAdapter.OnThreadItemClickListener, OnItemProfileAreaClickListener {
    }
}

