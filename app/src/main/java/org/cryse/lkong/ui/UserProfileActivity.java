package org.cryse.lkong.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.model.UserInfoModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.presenter.UserProfilePresenter;
import org.cryse.lkong.ui.adapter.UserProfileAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.UserProfileView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import io.github.froger.instamaterial.ui.view.RevealBackgroundView;

public class UserProfileActivity extends AbstractThemeableActivity implements RevealBackgroundView.OnStateChangeListener, UserProfileView {
    private static final String LOG_TAG = UserProfileActivity.class.getName();
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    private static final String LOAD_IMAGE_TASK_TAG = "user_profile_load_image_tag_";

    Picasso mPicasso;

    @Inject
    AndroidNavigation mNavigation;
    @Inject
    UserProfilePresenter mPresenter;
    @Inject
    UserAccountManager mUserAccountManager;

    @InjectView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;
    @InjectView(R.id.rvUserProfile)
    SuperRecyclerView rvUserProfile;

    private UserProfileAdapter userPhotosAdapter;

    private long mUid;
    private String mUserAvatarUrl;
    private UserInfoModel mUserModelInfo;
    private ArrayList<Object> mItemList;
    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity, long uid) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(DataContract.BUNDLE_USER_ID, uid);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        mItemList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        ButterKnife.inject(this);
        ViewCompat.setElevation(getToolbar(), 0f);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUid = getIntent().getLongExtra(DataContract.BUNDLE_USER_ID, 0l);
        if(mUid == 0l)
            throw new IllegalArgumentException("Must set uid in intent.");
        mUserAvatarUrl = ModelConverter.uidToAvatarUrl(mUid);
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getPresenter().getUserProfile(mUserAccountManager.getAuthObject(), mUid);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            userPhotosAdapter.setLockedAnimations(true);
        }
    }

    private void setupUserProfileGrid() {
        UIUtils.InsetsValue insetsValue =  UIUtils.getInsets(this, rvUserProfile, false, false, true, getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
        rvUserProfile.setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());

        // final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(new LinearLayoutManager(this));
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
        rvUserProfile.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getPresenter().getUserAllData(mUserAccountManager.getAuthObject(), 0, mUid, false);

            }
        });
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            rvUserProfile.setVisibility(View.VISIBLE);
            userPhotosAdapter = new UserProfileAdapter(
                    this,
                    mPicasso,
                    mUserAvatarUrl,
                    getThemeEngine().getPrimaryColor(this),
                    LOAD_IMAGE_TASK_TAG + Long.toString(mUid),
                    mItemList

            );
            rvUserProfile.setAdapter(userPhotosAdapter);
            if(mUserModelInfo != null) userPhotosAdapter.setUserInfo(mUserModelInfo);
        } else {
            rvUserProfile.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishCompat();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        mPicasso.shutdown();
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
        if(userPhotosAdapter != null) {
            userPhotosAdapter.setUserInfo(userInfoModel);
            getPresenter().getUserAllData(mUserAccountManager.getAuthObject(), 0, mUid, false);
        }
    }

    @Override
    public void onLoadUserProfileError(Throwable throwable, Object... extraInfo) {

    }

    @Override
    public void onLoadUserAllData(List<TimelineModel> items, boolean isLoadingMore) {
        if(userPhotosAdapter != null) {
            if(!isLoadingMore) {
                userPhotosAdapter.clear();
            }
            userPhotosAdapter.addAll(items);
        }
    }

    @Override
    public void onLoadUserThreads(List<ThreadModel> items, boolean isDigest, boolean isLoadingMore) {

    }

    @Override
    public void setLoading(Boolean value) {
        rvUserProfile.getSwipeToRefresh().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return null;
    }

    @Override
    public void showToast(int text_value, int toastType) {

    }

    @Override
    protected int getAppTheme() {
        if(isNightMode())
            return R.style.LKongDroidTheme_Dark_Translucent;
        else
            return R.style.LKongDroidTheme_Light_Translucent;
    }

    @Override
    public boolean isLoadingMore() {
        return false;
    }

    @Override
    public void setLoadingMore(boolean value) {

    }
}
