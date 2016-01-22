package org.cryse.lkong.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.logic.request.GetDataItemLocationRequest;
import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.presenter.UserProfilePresenter;
import org.cryse.lkong.ui.common.AbstractSwipeBackActivity;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.view.UserProfileView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserProfileActivity extends AbstractSwipeBackActivity implements /*RevealBackgroundView.OnStateChangeListener, */UserProfileView {
    private static final String LOG_TAG = UserProfileActivity.class.getName();
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final String LOAD_IMAGE_TASK_TAG = "user_profile_load_image_tag_";
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;

    AppNavigation mNavigation = new AppNavigation();
    @Inject
    UserProfilePresenter mPresenter;
    @Inject
    UserAccountManager mUserAccountManager;

    @Bind(R.id.appbarlayout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.collapseing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.activity_profile_imageview_avatar)
    ImageView mAvatarImageView;
    @Bind(R.id.activity_profile_textview_user_name)
    TextView mUserNameTextView;
    @Bind(R.id.activity_profile_textview_user_extra0)
    TextView mUserExtra0TextView;

    @Bind(R.id.activity_profile_textview_follower_count)
    TextView mUserFollowerCountTextView;
    @Bind(R.id.activity_profile_textview_following_count)
    TextView mUserFollowingCountTextView;
    @Bind(R.id.activity_profile_textview_thread_count)
    TextView mUserThreadCountTextView;
    @Bind(R.id.activity_profile_textview_post_count)
    TextView mUserPostCountTextView;

    @Bind(R.id.activity_profile_header_root)
    View mHeaderRootView;
    @Bind(R.id.activity_profile_header_detail)
    View mHeaderDetailView;
    @Bind(R.id.activity_profile_header_stats)
    View mHeaderStatsView;

    @Bind(R.id.fragment_user_detail_cardview_introduction)
    CardView mIntroductionCardView;


    @Bind(R.id.fragment_user_detail_textview_introduction)
    TextView mIntroductionTextView;

    @Bind(R.id.activity_profile_layout_all_activities)
    RelativeLayout mAllActivitiesLayout;
    @Bind(R.id.activity_profile_layout_digests)
    RelativeLayout mDigestsLayout;

    @Bind(R.id.activity_profile_imageview_crystal)
    ImageView mCrystalImageView;
    @Bind(R.id.activity_profile_textview_crystal)
    TextView mCrystalTextView;
    @Bind(R.id.activity_profile_imageview_coin)
    ImageView mCoinImageView;
    @Bind(R.id.activity_profile_textview_coin)
    TextView mCoinTextView;

    @Bind(R.id.activity_profile_textview_current_punch)
    TextView mCurrentContinuousPunchDaysTextView;
    @Bind(R.id.activity_profile_textview_longest_punch)
    TextView mLongestContinuousPunchDaysTextView;
    @Bind(R.id.activity_profile_textview_last_punch)
    TextView mLastPunchTimeTextView;
    @Bind(R.id.activity_profile_textview_total_punch)
    TextView mTotalPunchDaysTextView;
    @Bind(R.id.activity_profile_textview_registration_time)
    TextView mRegistrationTextView;


    MenuItem mFollowUserMenuItem;
    MenuItem mPMMenuItem;

    private UserDataFragmentPagerAdapter mViewPagerAdapter;

    private boolean mLockedAnimations = false;
    private long mProfileHeaderAnimationStartTime = 0;
    private long mUid;
    private Subscription mGetUserIdSubscription;

    private boolean isLoading = false;
    private String mUserAvatarUrl;
    private UserInfoModel mUserInfo;
    private ArrayList<Object> mItemList;
    private Boolean mIsUserFollowed = null;

    public static void startUserProfileFromLocation(Context startingContext, int[] startingLocation, long uid) {
        Intent intent = new Intent(startingContext, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(DataContract.BUNDLE_USER_ID, uid);
        startingContext.startActivity(intent);
    }

    public static void startUserProfileFromLocation(Context startingContext, int[] startingLocation, String userName) {
        Intent intent = new Intent(startingContext, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(DataContract.BUNDLE_USER_NAME, userName);
        startingContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        mItemList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        setUpToolbar(mToolbar);
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_USER_ID)) {
            mUid = getIntent().getLongExtra(DataContract.BUNDLE_USER_ID, 0l);
            if(mUid == 0)
                throw new IllegalArgumentException("Must set uid in intent.");
        } else if(intent.hasExtra(DataContract.BUNDLE_USER_NAME)) {
            mUid = -1;
            getUserIdFromName(intent.getStringExtra(DataContract.BUNDLE_USER_NAME));
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected void setUpToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        setColors();
        setClickListeners();
        if(mUid > 0) {
            loadUserProfile();
        }
    }

    private void setColors() {
        mIntroductionCardView.setCardBackgroundColor(Config.textColorPrimaryInverse(this, mATEKey));
    }

    private void setClickListeners() {
        mUserFollowerCountTextView.setOnClickListener(view -> {

        });
        mUserFollowingCountTextView.setOnClickListener(view -> {

        });
        mUserThreadCountTextView.setOnClickListener(view -> {

        });
        mUserPostCountTextView.setOnClickListener(view -> {

        });
        mAllActivitiesLayout.setOnClickListener(view -> {

        });
        mDigestsLayout.setOnClickListener(view -> {

        });
    }

    private void loadUserProfile() {
        getPresenter().getUserProfile(mUserAccountManager.getAuthObject(), mUid, mUid == mUserAccountManager.getCurrentUserId());
        mUserAvatarUrl = ModelConverter.uidToAvatarUrl(mUid);
        /*initViewPager();*/
        checkFollowStatus();
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.size_avatar_user_profile);
        Glide.with(this).load(mUserAvatarUrl)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .override(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransform(this))
                .into(mAvatarImageView);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    public void setupAnimations() {
        mHeaderRootView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mHeaderRootView.getViewTreeObserver().removeOnPreDrawListener(this);
                animateUserProfileHeader();
                return false;
            }
        });
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        mFollowUserMenuItem = menu.findItem(R.id.action_user_profile_follow);
        mPMMenuItem = menu.findItem(R.id.action_user_profile_pm);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mFollowUserMenuItem != null) {
            if(mUserAccountManager.getCurrentUserId() == mUid) {
                mFollowUserMenuItem.setVisible(false);
                mPMMenuItem.setVisible(false);
            } else {
                mFollowUserMenuItem.setVisible(true);
                if(mIsUserFollowed) {
                    mFollowUserMenuItem.setTitle(R.string.action_user_profile_unfollow);
                } else {
                    mFollowUserMenuItem.setTitle(R.string.action_user_profile_follow);
                }
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivityWithTransition();
                return true;
            case R.id.action_user_profile_follow:
                if(mIsUserFollowed)
                    getPresenter().unfollowUser(mUserAccountManager.getAuthObject(), mUid);
                else
                    getPresenter().followUser(mUserAccountManager.getAuthObject(), mUid);
                return true;
            case R.id.action_user_profile_pm:
                startPrivateChat();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFollowStatus();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().unbindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
        SubscriptionUtils.checkAndUnsubscribe(mGetUserIdSubscription);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    protected UserProfilePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onLoadUserProfileComplete(UserInfoModel userInfoModel) {
        mUserInfo = userInfoModel;
        mUserNameTextView.setText(buildUserName(mUserInfo.getUserName(), mUserInfo.getGender()));
        if(!TextUtils.isEmpty(mUserInfo.getCustomStatus())) {
            mUserExtra0TextView.setVisibility(View.VISIBLE);
            mUserExtra0TextView.setText(mUserInfo.getCustomStatus());
        } else {
            mUserExtra0TextView.setVisibility(View.GONE);
        }
        /*if(!TextUtils.isEmpty(mUserModelInfo.getCustomStatus())) {
            mUserExtra1TextView.setVisibility(View.VISIBLE);
            mUserExtra1TextView.setText(mUserModelInfo.getSigHtml());
        } else {
            mUserExtra1TextView.setVisibility(View.GONE);
        }*/
        int statsTextSize = getResources().getDimensionPixelSize(R.dimen.text_size_caption);
        mUserFollowerCountTextView.setText(
                getUserStatsText(mUserInfo.getFansCount(),
                        getString(R.string.profile_header_followers),
                        statsTextSize)
        );
        mUserFollowingCountTextView.setText(
                getUserStatsText(mUserInfo.getFollowCount(),
                        getString(R.string.profile_header_following),
                        statsTextSize)
        );
        mUserThreadCountTextView.setText(
                getUserStatsText(mUserInfo.getThreads(),
                        getString(R.string.profile_header_threads),
                        statsTextSize)
        );
        mUserPostCountTextView.setText(
                getUserStatsText(mUserInfo.getPosts(),
                        getString(R.string.profile_header_posts),
                        statsTextSize)
        );
        displayUserInfo();
        /*Fragment fragment = mViewPagerAdapter.getItem(0);
        if(fragment instanceof UserExtraDetailFragment) {
            ((UserExtraDetailFragment) fragment).setUserInfo(mUserModelInfo);
        }*/
    }

    @Override
    public void onCheckFollowStatusComplete(boolean isFollowed) {
        mIsUserFollowed = isFollowed;
        invalidateOptionsMenu();
    }

    @Override
    public void setLoading(Boolean value) {
        isLoading = value;/*
        mProfileCollectionView.getSwipeToRefresh().setRefreshing(value);*/
    }

    @Override
    public Boolean isLoading() {
        return isLoading;
    }

    private CharSequence getUserStatsText(int value, String describeText, int describeSize) {
        String valueString = Integer.toString(value);
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(valueString).append("\n");
        int start = builder.length();
        int end = start + describeText.length();
        builder.append(describeText);
        builder.setSpan(new AbsoluteSizeSpan(describeSize), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    private CharSequence buildUserName(String userName, int gender) {
        if(gender != 1 && gender != 2) return userName;
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(userName).append("  ");
        int start = builder.length();
        int end = start + 1;
        if(gender == 1)
            builder.append("\u2642");
        else
            builder.append("\u2640");
        builder.setSpan(new ForegroundColorSpan(Color.WHITE), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.setSpan(new RelativeSizeSpan(0.8f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return builder;
    }

    private void getUserIdFromName(String name) {
        mGetUserIdSubscription = Observable.create(new Observable.OnSubscribe<Long>() {
            @Override
            public void call(Subscriber<? super Long> subscriber) {
                try {
                    GetDataItemLocationRequest request = new GetDataItemLocationRequest(mUserAccountManager.getAuthObject(), "name_" + name);
                    DataItemLocationModel model = request.execute();
                    if(model != null && model.isLoad()) {
                        String locationString = model.getLocation();
                        if(!TextUtils.isEmpty(locationString)) {
                            subscriber.onNext(Long.valueOf(locationString.substring(5)));
                            subscriber.onCompleted();
                        }
                    }
                    subscriber.onError(new Exception("Could not get user id"));
                } catch (Exception ex) {
                    subscriber.onError(ex);
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(result -> {
                    mUid = result;
                    loadUserProfile();
                }, error -> {
                    new MaterialDialog.Builder(this)
                            .title(R.string.dialog_title_error)
                            .content(R.string.dialog_content_cannot_get_user_profile)
                            .show();
                    finish();
                }, () -> {

                });
    }


    private void animateUserProfileHeader() {
        if (!mLockedAnimations) {
            mProfileHeaderAnimationStartTime = System.currentTimeMillis();

            mHeaderDetailView.setTranslationY(-mHeaderDetailView.getHeight());
            mHeaderStatsView.setAlpha(0);

            mHeaderDetailView.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
            mHeaderStatsView.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
        }
    }

    public void displayUserInfo() {
        if(mUserInfo == null) {
            mIntroductionCardView.setVisibility(View.GONE);
            return;
        }

        // Display Introduction Info
        if(!TextUtils.isEmpty(mUserInfo.getSigHtml())) {
            mIntroductionCardView.setVisibility(View.VISIBLE);
            String introduction = mUserInfo.getSigHtml();
            mIntroductionTextView.setText(introduction);
        } else {
            mIntroductionCardView.setVisibility(View.GONE);
        }

        // Display Wealth Info
        mCrystalTextView.setText(String.format("%d", mUserInfo.getDragonCrystal()));
        if(mUserInfo.getUid() == mUserInfo.getMe()) {
            String dragonMoney = getString(R.string.format_dragon_money, mUserInfo.getDragonMoney());
            mCoinTextView.setText(dragonMoney);
            mCoinTextView.setText(String.format("%d", mUserInfo.getDragonMoney()));
        } else {
            mCoinTextView.setVisibility(View.GONE);
            mCoinImageView.setVisibility(View.GONE);
        }

        // Display Punch Info
        mCurrentContinuousPunchDaysTextView.setText(String.format("%d", mUserInfo.getCurrentContinuousPunch()));
        mLongestContinuousPunchDaysTextView.setText(String.format("%d", mUserInfo.getLongestContinuousPunch()));
        mTotalPunchDaysTextView.setText(String.format("%d", mUserInfo.getTotalPunchCount()));
        mLastPunchTimeTextView.setText(mUserInfo.getTotalPunchCount() > 0 ? TimeFormatUtils.formatDate(this, mUserInfo.getLastPunchTime(), false) : "N/A");

        // Display Registration Time
        mRegistrationTextView.setText(TimeFormatUtils.formatDate(this, mUserInfo.getRegDate(), true));
    }

    static class UserDataFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private List<String> mTabTitles = new ArrayList<>();
        private List<Fragment> mFragments = new ArrayList<>();
        private long mUserId;
        UserDataFragmentPagerAdapter(Context context, FragmentManager fm, long uid) {
            super(fm);
            Collections.addAll(mTabTitles, context.getResources().getStringArray(R.array.string_array_user_profile_tabs));
            mUserId = uid;
            // mFragments.add(UserExtraDetailFragment.newInstance());
            mFragments.add(UserProfileTimelineFragment.newInstance(mUserId));
            mFragments.add(UserProfileThreadsFragment.newInstance(mUserId, false));
            mFragments.add(UserProfileThreadsFragment.newInstance(mUserId, true));
            mFragments.add(UserProfileUsersFragment.newInstance(mUserId, true));
            mFragments.add(UserProfileUsersFragment.newInstance(mUserId, false));
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles.get(position);
        }
    }

    private void startPrivateChat() {
        if(mUserInfo != null)
            mNavigation.openActivityForPrivateMessage(this, mUid, mUserInfo.getUserName());
    }

    private void checkFollowStatus() {
        if(mUserAccountManager.getCurrentUserId() != mUid) {
            getPresenter().isUserFollowed(mUserAccountManager.getCurrentUserId(), mUid);
        }
    }
}
