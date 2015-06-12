package org.cryse.lkong.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import org.cryse.utils.ColorUtils;

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

    @InjectView(R.id.activity_profile_reveal_bg_layout)
    RevealBackgroundView mActivityRevealBackground;
    @InjectView(R.id.activity_profile_content_superlistview)
    SuperRecyclerView mProfileCollectionView;

    private UserProfileAdapter mProfileAdapter;

    private long mUid;

    private boolean isNoMore = false;
    private boolean isLoading = false;
    private boolean isLoadingMore = false;
    private String mUserAvatarUrl;
    private UserInfoModel mUserModelInfo;
    private ArrayList<Object> mItemList;

    public static void startUserProfileFromLocation(Context startingContext, int[] startingLocation, long uid) {
        Intent intent = new Intent(startingContext, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(DataContract.BUNDLE_USER_ID, uid);
        startingContext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        mItemList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setUpToolbar(R.id.toolbar, R.id.toolbar_shadow);
        ButterKnife.inject(this);
        ViewCompat.setElevation(getToolbar(), 0f);
        mActivityRevealBackground.setFillPaintColor(ColorUtils.getColorFromAttr(this, android.R.attr.colorBackground));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
        mProfileCollectionView.measure(0, 0);
        getPresenter().getUserProfile(mUserAccountManager.getAuthObject(), mUid, mUid == mUserAccountManager.getCurrentUserId());
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        mActivityRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            mActivityRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mActivityRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    mActivityRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            mActivityRevealBackground.setToFinishedFrame();
            mProfileAdapter.setLockedAnimations(true);
        }
    }

    private void setupUserProfileGrid() {
        UIUtils.InsetsValue insetsValue =  UIUtils.getInsets(this, mProfileCollectionView, false, false, true, getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
        mProfileCollectionView.setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());

        // final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mProfileCollectionView.setLayoutManager(new LinearLayoutManager(this));
        mProfileCollectionView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                mProfileAdapter.setLockedAnimations(true);
            }
        });
        mProfileCollectionView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isNoMore = false;
                int currentListType = mProfileAdapter.getCurrentListType();
                switch (currentListType) {
                    case UserProfileAdapter.LIST_ALL:
                        getPresenter().getUserAllData(mUserAccountManager.getAuthObject(), 0, mUid, false);
                        break;
                    case UserProfileAdapter.LIST_THREADS:
                        getPresenter().getUserThreads(mUserAccountManager.getAuthObject(), 0, mUid, false, false);
                        break;
                    case UserProfileAdapter.LIST_DIGEST:
                        getPresenter().getUserThreads(mUserAccountManager.getAuthObject(), 0, mUid, true, false);
                        break;
                }
            }
        });
        mProfileCollectionView.setOnMoreListener((overallItemsCount, itemsBeforeMore, maxLastVisiblePosition) -> {
            long lastSortKey = mProfileAdapter.getLastSortKey();
            if (!isNoMore && !isLoadingMore && lastSortKey != -1) {
                int currentListType = mProfileAdapter.getCurrentListType();
                switch (currentListType) {
                    case UserProfileAdapter.LIST_ALL:
                        getPresenter().getUserAllData(mUserAccountManager.getAuthObject(), lastSortKey, mUid, true);
                        break;
                    case UserProfileAdapter.LIST_THREADS:
                        getPresenter().getUserThreads(mUserAccountManager.getAuthObject(), lastSortKey, mUid, false, true);
                        break;
                    case UserProfileAdapter.LIST_DIGEST:
                        getPresenter().getUserThreads(mUserAccountManager.getAuthObject(), lastSortKey, mUid, true, true);
                        break;
                }
            } else {
                mProfileCollectionView.setLoadingMore(false);
                mProfileCollectionView.hideMoreProgress();
            }
        });
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {
            mProfileCollectionView.setVisibility(View.VISIBLE);
            mProfileAdapter = new UserProfileAdapter(
                    this,
                    mPicasso,
                    mUserAvatarUrl,
                    getThemeEngine().getPrimaryColor(this),
                    LOAD_IMAGE_TASK_TAG + Long.toString(mUid),
                    mItemList

            );
            mProfileAdapter.setOnItemProfileImageClickListener(new UserProfileAdapter.OnProfileItemClickListener() {
                @Override
                public void onItemTimelineClick(View view, int adapterPosition) {
                    Object object = mProfileAdapter.getItem(adapterPosition);
                    if (object != null && object instanceof TimelineModel) {
                        TimelineModel model = (TimelineModel) object;
                        mNavigation.openActivityForPostListByTimelineModel(UserProfileActivity.this, model);
                    }
                }

                @Override
                public void onItemThreadClick(View view, int adapterPosition) {
                    Object object = mProfileAdapter.getItem(adapterPosition);
                    if (object != null && object instanceof ThreadModel) {
                        ThreadModel model = (ThreadModel) object;
                        String idString = model.getId().substring(7);
                        long tid = Long.parseLong(idString);
                        mNavigation.openActivityForPostListByThreadId(UserProfileActivity.this, tid);
                    }
                }

                @Override
                public void onProfileAreaClick(View view, int position, long uid) {
                    Object object = mProfileAdapter.getItem(position);
                    if (object != null && object instanceof TimelineModel) {
                        TimelineModel model = (TimelineModel) object;
                        int[] startingLocation = new int[2];
                        view.getLocationOnScreen(startingLocation);
                        startingLocation[0] += view.getWidth() / 2;
                        mNavigation.openActivityForUserProfile(UserProfileActivity.this, startingLocation, model.getUserId());
                    } else if (object != null && object instanceof ThreadModel) {
                        ThreadModel model = (ThreadModel) object;
                        int[] startingLocation = new int[2];
                        view.getLocationOnScreen(startingLocation);
                        startingLocation[0] += view.getWidth() / 2;
                        mNavigation.openActivityForUserProfile(UserProfileActivity.this, startingLocation, model.getUid());
                    }
                }
            });
            mProfileAdapter.setOnTabListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    int position = tab.getPosition();
                    isNoMore = false;
                    switch (position) {
                        case 0:
                            getPresenter().getUserAllData(mUserAccountManager.getAuthObject(), 0, mUid, false);
                            break;
                        case 1:
                            getPresenter().getUserThreads(mUserAccountManager.getAuthObject(), 0, mUid, false, false);
                            break;
                        case 2:
                            getPresenter().getUserThreads(mUserAccountManager.getAuthObject(), 0, mUid, true, false);
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            mProfileCollectionView.setAdapter(mProfileAdapter);
            if(mUserModelInfo != null) mProfileAdapter.setUserInfo(mUserModelInfo);
            getPresenter().getUserAllData(mUserAccountManager.getAuthObject(), 0, mUid, false);
        } else {
            mProfileCollectionView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivityWithTransition();
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
        if(mProfileAdapter != null) {
            mProfileAdapter.setUserInfo(userInfoModel);
            getPresenter().getUserAllData(mUserAccountManager.getAuthObject(), 0, mUid, false);
        }
    }

    @Override
    public void onLoadUserProfileError(Throwable throwable, Object... extraInfo) {

    }

    @Override
    public void onLoadUserAllData(List<TimelineModel> items, boolean isLoadingMore) {
        if(mProfileAdapter != null) {
            if(items.size() == 0) {
                isNoMore = true;
                return;
            }
            if(!isLoadingMore) {
                mProfileAdapter.clear();
            }
            mProfileAdapter.addAll(items);
        }
    }

    @Override
    public void onLoadUserThreads(List<ThreadModel> items, boolean isDigest, boolean isLoadingMore) {
        if(mProfileAdapter != null) {
            if(items.size() == 0) {
                isNoMore = true;
                return;
            }
            if(!isLoadingMore) {
                mProfileAdapter.clear();
            }
            mProfileAdapter.addAll(items);
        }
    }

    @Override
    public void showToast(int text_value, int toastType) {

    }

    @Override
    protected int getAppTheme() {
        if(isNightMode())
            return R.style.LKongDroidTheme_Dark_Transparent;
        else
            return R.style.LKongDroidTheme_Light_Transparent;
    }

    @Override
    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    @Override
    public void setLoadingMore(boolean value) {
        isLoadingMore = value;
        mProfileCollectionView.setLoadingMore(value);
        if(value)
            mProfileCollectionView.showMoreProgress();
        else
            mProfileCollectionView.hideMoreProgress();
    }

    @Override
    public void setLoading(Boolean value) {
        isLoading = value;
        mProfileCollectionView.getSwipeToRefresh().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return isLoading;
    }
}
