package org.cryse.lkong.ui;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import org.cryse.lkong.ui.common.AbstractFragment;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.ThemeUtils;
import org.cryse.lkong.utils.TimeFormatUtils;
import org.cryse.lkong.utils.transformation.CircleTransform;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.view.UserProfileView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserProfileFragment extends AbstractFragment implements /*RevealBackgroundView.OnStateChangeListener, */UserProfileView {
    private static final String LOG_TAG = UserProfileFragment.class.getName();
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final String ARG_USER_INFO = "arg_user_info";
    private static final String ARG_USER_NAME = "arg_user_name";

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
    @Bind(R.id.fragment_user_detail_cardview_else)
    CardView mElseCardView;


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
    MenuItem mBlockUserMenuItem;
    MenuItem mPMMenuItem;

    private long mUid;
    private String mUserName;
    private Subscription mGetUserIdSubscription;

    private boolean isLoading = false;
    private String mUserAvatarUrl;
    private UserInfoModel mUserInfo;
    private Boolean mIsUserFollowed = null;
    private Boolean mIsUserBlocked = null;

    public static UserProfileFragment newInstance(Bundle args) {
        UserProfileFragment fragment = new UserProfileFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle arguments = getArguments();
        if(arguments.containsKey(DataContract.BUNDLE_USER_ID)) {
            mUid = arguments.getLong(DataContract.BUNDLE_USER_ID, 0l);
            if(mUid == 0)
                throw new IllegalArgumentException("Must set uid in intent.");
        } else if(arguments.containsKey(DataContract.BUNDLE_USER_NAME)) {
            mUid = -1;
            mUserName = arguments.getString(DataContract.BUNDLE_USER_NAME);
        } else {
            throw new IllegalArgumentException();
        }
        if(savedInstanceState != null && savedInstanceState.containsKey(ARG_USER_INFO)) {
            this.mUserInfo = (UserInfoModel) savedInstanceState.getSerializable(ARG_USER_INFO);
            if(savedInstanceState.containsKey(ARG_USER_NAME))
                this.mUserName = savedInstanceState.getString(ARG_USER_NAME);
            this.mUid = savedInstanceState.getLong(DataContract.BUNDLE_USER_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_profile, container, false);
        ButterKnife.bind(this, contentView);
        setUpToolbar(mToolbar);
        setColors();
        setClickListeners();
        if(mUserInfo != null) {
            setAvatar();
            onLoadUserProfileComplete(this.mUserInfo);
        } else {
            if(mUid > 0) {
                loadUserProfile();
            } else {
                getUserIdFromName(mUserName);
            }
        }
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAppCompatActivity().setTitle("");
    }

    protected void setUpToolbar(Toolbar toolbar) {
        getAppCompatActivity().setSupportActionBar(toolbar);
        ActionBar actionBar = getAppCompatActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            Drawable drawable = ResourcesCompat.getDrawable(
                    getResources(),
                    R.drawable.ic_arrow_backward,
                    null
            );
            ThemeUtils.setTint(
                    drawable,
                    Config.textColorPrimaryInverse(getActivity(), mATEKey)
            );
            actionBar.setHomeAsUpIndicator(drawable);
        }
    }

    private void setColors() {
        mIntroductionCardView.setCardBackgroundColor(Config.textColorPrimaryInverse(getActivity(), mATEKey));
        mElseCardView.setCardBackgroundColor(Config.textColorPrimaryInverse(getActivity(), mATEKey));
    }

    private void setClickListeners() {
        mUserFollowerCountTextView.setOnClickListener(view -> {
            UserProfileActivity activity = (UserProfileActivity) getActivity();
            if(mUserInfo != null) {
                activity.goToFollowerFragment(mUid, mUserInfo.getUserName());
            }
        });
        mUserFollowingCountTextView.setOnClickListener(view -> {
            UserProfileActivity activity = (UserProfileActivity) getActivity();
            if(mUserInfo != null) {
                activity.goToFollowingFragment(mUid, mUserInfo.getUserName());
            }
        });
        mUserThreadCountTextView.setOnClickListener(view -> {
            UserProfileActivity activity = (UserProfileActivity) getActivity();
            if (mUserInfo != null) {
                activity.goToThreadFragment(mUid, mUserInfo.getUserName());
            }
        });
        mUserPostCountTextView.setOnClickListener(view -> {
            /*UserProfileActivity activity = (UserProfileActivity) getActivity();
            activity.goToFollowerFragment(mUid);*/
        });
        mAllActivitiesLayout.setOnClickListener(view -> {
            UserProfileActivity activity = (UserProfileActivity) getActivity();
            if (mUserInfo != null) {
                activity.goToAllActivitiesFragment(mUid, mUserInfo.getUserName());
            }
        });
        mDigestsLayout.setOnClickListener(view -> {
            UserProfileActivity activity = (UserProfileActivity) getActivity();
            if (mUserInfo != null) {
                activity.goToDigestsFragment(mUid, mUserInfo.getUserName());
            }
        });
    }

    private void setAvatar() {
        mUserAvatarUrl = ModelConverter.uidToAvatarUrl(mUid);
        int avatarSize = getResources().getDimensionPixelSize(R.dimen.size_avatar_user_profile);
        Glide.with(this).load(mUserAvatarUrl)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .override(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransform(getActivity()))
                .into(mAvatarImageView);
    }

    private void loadUserProfile() {
        setAvatar();
        getPresenter().getUserProfile(mUserAccountManager.getAuthObject(), mUid, mUid == mUserAccountManager.getCurrentUserId());
        checkFollowStatus();
        checkBlockStatus();
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_user_profile, menu);
        mFollowUserMenuItem = menu.findItem(R.id.action_user_profile_follow);
        mBlockUserMenuItem = menu.findItem(R.id.action_user_profile_block);
        mPMMenuItem = menu.findItem(R.id.action_user_profile_pm);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        boolean isSelf = mUserAccountManager.getCurrentUserId() == mUid;
        if(mPMMenuItem != null && isSelf) {
            mPMMenuItem.setVisible(false);
        }
        if(mFollowUserMenuItem != null) {
            if(isSelf) {
                mFollowUserMenuItem.setVisible(false);
            } else {
                if(mIsUserFollowed !=null) {
                    mFollowUserMenuItem.setVisible(true);
                    if(mIsUserFollowed) {
                        mFollowUserMenuItem.setTitle(R.string.action_user_profile_unfollow);
                    } else {
                        mFollowUserMenuItem.setTitle(R.string.action_user_profile_follow);
                    }
                } else {
                    mFollowUserMenuItem.setVisible(false);
                }
            }
        }
        if(mBlockUserMenuItem != null) {
            if(isSelf) {
                mBlockUserMenuItem.setVisible(false);
            } else {
                if(mIsUserBlocked !=null) {
                    mBlockUserMenuItem.setVisible(true);
                    if(mIsUserBlocked) {
                        mBlockUserMenuItem.setTitle(R.string.action_user_profile_unblock);
                    } else {
                        mBlockUserMenuItem.setTitle(R.string.action_user_profile_block);
                    }
                } else {
                    mBlockUserMenuItem.setVisible(false);
                }
            }
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSwipeBackActivity().closeActivityWithTransition();
                return true;
            case R.id.action_user_profile_follow:
                getPresenter().followUser(mUserAccountManager.getAuthObject(), mUid, !mIsUserFollowed);
                return true;
            case R.id.action_user_profile_block:
                getPresenter().blockUser(mUserAccountManager.getAuthObject(), mUid, !mIsUserBlocked);
                return true;
            case R.id.action_user_profile_pm:
                startPrivateChat();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        checkFollowStatus();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPresenter().unbindView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
        SubscriptionUtils.checkAndUnsubscribe(mGetUserIdSubscription);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ARG_USER_INFO, mUserInfo);
        if(!TextUtils.isEmpty(mUserName))
            outState.putString(ARG_USER_NAME, mUserName);
        outState.putLong(DataContract.BUNDLE_USER_ID, mUid);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
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
        int statsTextSize = getResources().getDimensionPixelSize(R.dimen.text_size_caption);
        mUserFollowerCountTextView.setText(
                getUserStatsText(mUserInfo.getFansCount(),
                        getString(R.string.text_profile_header_followers),
                        statsTextSize)
        );
        mUserFollowingCountTextView.setText(
                getUserStatsText(mUserInfo.getFollowCount(),
                        getString(R.string.text_profile_header_following),
                        statsTextSize)
        );
        mUserThreadCountTextView.setText(
                getUserStatsText(mUserInfo.getThreads(),
                        getString(R.string.text_profile_header_threads),
                        statsTextSize)
        );
        mUserPostCountTextView.setText(
                getUserStatsText(mUserInfo.getPosts(),
                        getString(R.string.text_profile_header_posts),
                        statsTextSize)
        );
        getAppCompatActivity().setTitle("");
        displayUserInfo();
    }

    @Override
    public void onCheckFollowStatusComplete(boolean isFollowed) {
        mIsUserFollowed = isFollowed;
        getAppCompatActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCheckBlockStatusComplete(boolean isBlocked) {
        mIsUserBlocked = isBlocked;
        getAppCompatActivity().invalidateOptionsMenu();
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
                            return;
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
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.dialog_title_error)
                            .content(R.string.dialog_content_cannot_get_user_profile)
                            .show();
                    getSwipeBackActivity().finish();
                }, () -> {

                });
    }

    public void displayUserInfo() {
        if(mUserInfo == null) {
            mIntroductionCardView.setVisibility(View.GONE);
            return;
        }

        // Display Introduction Info
        if(!TextUtils.isEmpty(mUserInfo.getSigHtml())) {
            mIntroductionCardView.setVisibility(View.VISIBLE);
            mIntroductionTextView.setText(mUserInfo.getSigHtml());
        } else {
            mIntroductionCardView.setVisibility(View.GONE);
        }

        // Display Wealth Info
        mCrystalTextView.setText(String.format("%d", mUserInfo.getDragonCrystal()));
        if(mUserInfo.getUid() == mUserInfo.getMe()) {
            mCoinTextView.setText(String.format("%d", mUserInfo.getDragonMoney()));
        } else {
            mCoinTextView.setVisibility(View.GONE);
            mCoinImageView.setVisibility(View.GONE);
        }

        // Display Punch Info
        mCurrentContinuousPunchDaysTextView.setText(String.format("%d", mUserInfo.getCurrentContinuousPunch()));
        mLongestContinuousPunchDaysTextView.setText(String.format("%d", mUserInfo.getLongestContinuousPunch()));
        mTotalPunchDaysTextView.setText(String.format("%d", mUserInfo.getTotalPunchCount()));
        mLastPunchTimeTextView.setText(mUserInfo.getTotalPunchCount() > 0 ? TimeFormatUtils.formatDate(getActivity(), mUserInfo.getLastPunchTime(), false) : "N/A");

        // Display Registration Time
        mRegistrationTextView.setText(TimeFormatUtils.formatDate(getActivity(), mUserInfo.getRegDate(), true));
    }

    private void startPrivateChat() {
        if(mUserInfo != null)
            mNavigation.openActivityForPrivateMessage(getActivity(), mUid, mUserInfo.getUserName());
    }

    private void checkFollowStatus() {
        if(mUserAccountManager.getCurrentUserId() != mUid) {
            getPresenter().isUserFollowed(mUserAccountManager.getAuthObject(), mUid);
        }
    }

    private void checkBlockStatus() {
        if(mUserAccountManager.getCurrentUserId() != mUid) {
            getPresenter().isUserBlocked(mUserAccountManager.getAuthObject(), mUid);
        }
    }
}
