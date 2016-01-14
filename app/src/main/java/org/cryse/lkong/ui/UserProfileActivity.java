package org.cryse.lkong.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.logic.request.GetDataItemLocationRequest;
import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.presenter.UserProfilePresenter;
import org.cryse.lkong.ui.common.AbstractSwipeBackActivity;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.SubscriptionUtils;
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
    @Bind(R.id.tablayout)
    TabLayout mTabLayout;
    @Bind(R.id.activity_profile_viewpager)
    ViewPager mViewPager;

    @Bind(R.id.activity_profile_imageview_avatar)
    ImageView mAvatarImageView;
    @Bind(R.id.activity_profile_textview_user_name)
    TextView mUserNameTextView;
    @Bind(R.id.activity_profile_textview_user_extra0)
    TextView mUserExtra0TextView;
    @Bind(R.id.activity_profile_textview_user_extra1)
    TextView mUserExtra1TextView;

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

    @Bind(R.id.activity_profile_header_container_follow)
    FrameLayout mFollowContainer;
    @Bind(R.id.activity_profile_header_button_follow)
    Button mFollowButton;

    /*MenuItem mFollowUserMenuItem;*/

    private UserDataFragmentPagerAdapter mViewPagerAdapter;

    private boolean mLockedAnimations = false;
    private long mProfileHeaderAnimationStartTime = 0;
    private long mUid;
    private Subscription mGetUserIdSubscription;

    private boolean isLoading = false;
    private String mUserAvatarUrl;
    private UserInfoModel mUserModelInfo;
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
        mAppBarLayout.setTargetElevation(0f);
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
        }/*
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);*/
        setupAnimations();
        ATE.apply(mTabLayout, mATEKey);
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
        if(mUid > 0) {
            loadUserProfile();
        }
    }

    private void loadUserProfile() {
        getPresenter().getUserProfile(mUserAccountManager.getAuthObject(), mUid, mUid == mUserAccountManager.getCurrentUserId());
        mUserAvatarUrl = ModelConverter.uidToAvatarUrl(mUid);
        initViewPager();
        checkFollowStatus();
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.size_avatar_user_profile);
        Glide.with(this).load(mUserAvatarUrl)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .override(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransform(this))
                .into(mAvatarImageView);
        mFollowButton.setOnClickListener(view -> {
            if (mIsUserFollowed)
                getPresenter().unfollowUser(mUserAccountManager.getAuthObject(), mUid);
            else
                getPresenter().followUser(mUserAccountManager.getAuthObject(), mUid);
        });
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    private void initViewPager() {
        mViewPagerAdapter = new UserDataFragmentPagerAdapter(this, getSupportFragmentManager(), mUid);
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mViewPager.getCurrentItem() == 0) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = displaymetrics.widthPixels;
                    getSwipeBackLayout().setEnableGesture(true);
                    getSwipeBackLayout().setEdgeSize(width);
                } else {
                    getSwipeBackLayout().setEnableGesture(false);
                    getSwipeBackLayout().setEdgeSize(0);
                }
            }
        });
        mTabLayout.setupWithViewPager(mViewPager);
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
        mTabLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mTabLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                animateUserProfileOptions();
                return false;
            }
        });
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof ThemeColorChangedEvent) {
            mTabLayout.setBackgroundColor(((ThemeColorChangedEvent) event).getNewPrimaryColor());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        // mFollowUserMenuItem = menu.findItem(R.id.action_user_profile_follow);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivityWithTransition();
                return true;
            /*case R.id.action_user_profile_follow:
                if(mIsUserFollowed)
                    getPresenter().unfollowUser(mUserAccountManager.getAuthObject(), mUid);
                else
                    getPresenter().followUser(mUserAccountManager.getAuthObject(), mUid);
                return true;*/
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
        mUserModelInfo = userInfoModel;
        mUserNameTextView.setText(buildUserName(mUserModelInfo.getUserName(), mUserModelInfo.getGender()));
        if(!TextUtils.isEmpty(mUserModelInfo.getCustomStatus())) {
            mUserExtra0TextView.setVisibility(View.VISIBLE);
            mUserExtra0TextView.setText(mUserModelInfo.getCustomStatus());
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
                getUserStatsText(mUserModelInfo.getFansCount(),
                        getString(R.string.profile_header_followers),
                        statsTextSize)
        );
        mUserFollowingCountTextView.setText(
                getUserStatsText(mUserModelInfo.getFollowCount(),
                        getString(R.string.profile_header_following),
                        statsTextSize)
        );
        mUserThreadCountTextView.setText(
                getUserStatsText(mUserModelInfo.getThreads(),
                        getString(R.string.profile_header_threads),
                        statsTextSize)
        );
        mUserPostCountTextView.setText(
                getUserStatsText(mUserModelInfo.getPosts(),
                        getString(R.string.profile_header_posts),
                        statsTextSize)
        );
        Fragment fragment = mViewPagerAdapter.getItem(0);
        if(fragment instanceof UserExtraDetailFragment) {
            ((UserExtraDetailFragment) fragment).setUserInfo(mUserModelInfo);
        }
    }

    @Override
    public void onCheckFollowStatusComplete(boolean isFollowed) {
        mIsUserFollowed = isFollowed;
        if(mFollowContainer != null) {
            if(mIsUserFollowed != null) {
                mFollowContainer.setVisibility(View.VISIBLE);
                if(mIsUserFollowed) {
                    mFollowButton.setText(R.string.action_user_profile_unfollow);
                }
                else {
                    mFollowButton.setText(R.string.action_user_profile_follow);
                }
            } else {
                mFollowContainer.setVisibility(View.GONE);
            }
        }
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
            //mAppBarLayout.setTranslationY(-mAppBarLayout.getHeight());
            mProfileHeaderAnimationStartTime = System.currentTimeMillis();

            //mHeaderRootView.setTranslationY(-mHeaderRootView.getHeight());
            //mAvatarImageView.setTranslationY(-mAvatarImageView.getHeight());
            mHeaderDetailView.setTranslationY(-mHeaderDetailView.getHeight());
            mHeaderStatsView.setAlpha(0);

            //mAppBarLayout.animate().translationY(0).setDuration(900).setInterpolator(INTERPOLATOR);
            //mHeaderRootView.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
            //mAvatarImageView.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
            mHeaderDetailView.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
            mHeaderStatsView.animate().alpha(1).setDuration(200).setStartDelay(400).setInterpolator(INTERPOLATOR).start();
            /*ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(mAppBarLayout, "elevation", 0.0f, getResources().getDimensionPixelSize(R.dimen.toolbar_elevation));
            //设置插值器
            objectAnimator.setStartDelay(400);
            objectAnimator.setInterpolator(INTERPOLATOR);
            objectAnimator.setDuration(500);
            objectAnimator.start();*/
        }
    }

    private void animateUserProfileOptions() {
        if (!mLockedAnimations) {
            mTabLayout.setTranslationY(-mTabLayout.getHeight());
            mTabLayout.setAlpha(0f);

            mTabLayout.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
            mTabLayout.animate().alpha(1f).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
        }
    }

    static class UserDataFragmentPagerAdapter extends FragmentStatePagerAdapter {
        private List<String> mTabTitles = new ArrayList<>();
        private List<Fragment> mFragments = new ArrayList<>();
        private long mUserId;
        UserDataFragmentPagerAdapter(Context context, FragmentManager fm, long uid) {
            super(fm);
            Collections.addAll(mTabTitles, context.getResources().getStringArray(R.array.string_array_user_profile_tabs));
            mUserId = uid;
            mFragments.add(UserExtraDetailFragment.newInstance());
            mFragments.add(UserProfileTimelineFragment.newInstance(mUserId));
            mFragments.add(UserProfileThreadsFragment.newInstance(mUserId, false));
            mFragments.add(UserProfileThreadsFragment.newInstance(mUserId, true));
        }

        @Override
        public Fragment getItem(int i) {
            return mFragments.get(i);
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitles.get(position);
        }
    }

    private void startPrivateChat() {
        if(mUserModelInfo != null)
            mNavigation.openActivityForPrivateMessage(this, mUid, mUserModelInfo.getUserName());
    }

    private void checkFollowStatus() {
        if(mUserAccountManager.getCurrentUserId() != mUid) {
            getPresenter().isUserFollowed(mUserAccountManager.getCurrentUserId(), mUid);
        }
    }
}
