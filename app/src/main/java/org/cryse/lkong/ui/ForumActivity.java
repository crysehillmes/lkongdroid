package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.logic.ThreadListType;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.model.converter.ModelConverter;
import org.cryse.lkong.presenter.ForumPresenter;
import org.cryse.lkong.ui.adapter.ThreadListAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.ForumView;
import org.cryse.lkong.widget.FloatingActionButtonEx;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ForumActivity extends AbstractThemeableActivity implements ForumView {
    public static final String LOG_TAG = ForumActivity.class.getName();
    private static final String FORUM_PINNED_KEY = "forum_pinned";
    private AtomicBoolean isNoMore = new AtomicBoolean(false);
    private AtomicBoolean isLoading = new AtomicBoolean(false);
    private AtomicBoolean isLoadingMore = new AtomicBoolean(false);
    private long mLastItemSortKey = -1;
    @Inject
    ForumPresenter mPresenter;

    @Inject
    AndroidNavigation mAndroidNavigation;

    @Inject
    UserAccountManager mUserAccountManager;

    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.activity_forum_thread_list_recyclerview)
    SuperRecyclerView mThreadCollectionView;
    @InjectView(R.id.fab)
    FloatingActionButtonEx mFab;

    View mHeaderView;
    Spinner mListTypeSpinner;
    MenuItem mPinForumMenuItem;
    SwitchCompat mPinForumSwitch;
    MenuItem mChangeThemeMenuItem;
    ThreadListAdapter mCollectionAdapter;

    List<ThreadModel> mItemList = new ArrayList<ThreadModel>();

    private long mForumId = -1;
    private String mForumName = "";
    private String mForumDescription = "";
    private int mCurrentListType = ThreadListType.TYPE_SORT_BY_REPLY;
    private Boolean mIsForumPinned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_thread_list);
        ButterKnife.inject(this);
        setUpToolbar(mToolbar);
        initRecyclerView();
        setUpHeaderView();
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_FORUM_ID) && intent.hasExtra(DataContract.BUNDLE_FORUM_NAME)) {
            mForumId = intent.getLongExtra(DataContract.BUNDLE_FORUM_ID, -1);
            mForumName = intent.getStringExtra(DataContract.BUNDLE_FORUM_NAME);
            mForumDescription = intent.getStringExtra(DataContract.BUNDLE_FORUM_DESCRIPTION);
        }
        if(mForumId == -1 || TextUtils.isEmpty(mForumName))
            throw new IllegalStateException("ForumActivity missing extra in intent.");
        setTitle(mForumName);


    }
    private void initRecyclerView() {
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        int statusBarSize = UIUtils.calculateStatusBarSize(this);
        mThreadCollectionView.getSwipeToRefresh().setProgressViewOffset(true, statusBarSize, actionBarSize * 2);
        mThreadCollectionView.setItemAnimator(new DefaultItemAnimator());
        mThreadCollectionView.setLayoutManager(new LinearLayoutManager(this));
        mCollectionAdapter = new ThreadListAdapter(this, mItemList);
        mThreadCollectionView.setAdapter(mCollectionAdapter);

        mThreadCollectionView.setRefreshListener(() -> getPresenter().loadThreadList(mForumId, mCurrentListType, false));
        mThreadCollectionView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!isNoMore.get() && !isLoadingMore.get() && mLastItemSortKey != -1) {
                getPresenter().loadThreadList(mForumId, mLastItemSortKey, mCurrentListType, true);
            } else {
                mThreadCollectionView.setLoadingMore(false);
                mThreadCollectionView.hideMoreProgress();
            }
        });
        mCollectionAdapter.setOnThreadItemClickListener(new ThreadListAdapter.OnThreadItemClickListener() {
            @Override
            public void onProfileAreaClick(View view, int position, long uid) {
                int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
                if (itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    ThreadModel model = mCollectionAdapter.getItem(itemIndex);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mAndroidNavigation.openActivityForUserProfile(ForumActivity.this, startingLocation, model.getUid());
                }
            }

            @Override
            public void onItemThreadClick(View view, int adapterPosition) {
                int itemIndex = adapterPosition - mCollectionAdapter.getHeaderViewCount();
                if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    ThreadModel item = mCollectionAdapter.getItem(itemIndex);
                    String idString = item.getId().substring(7);
                    long tid = Long.parseLong(idString);
                    mAndroidNavigation.openActivityForPostListByThreadId(ForumActivity.this, tid);
                }
            }
        });
        mFab.attachToSuperRecyclerView(mThreadCollectionView);
        mFab.setOnClickListener(view -> {
            if (mUserAccountManager.isSignedIn()) {
                mAndroidNavigation.openActivityForNewThread(this, mForumId, mForumName);
            } else {
                mAndroidNavigation.navigateToSignInActivity(this, false);
            }
        });
        setColorToViews(getThemeEngine().getPrimaryColor(this), getThemeEngine().getPrimaryDarkColor(this));

        mThreadCollectionView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isActivityDestroyed())
                        Glide.with(ForumActivity.this).resumeRequests();
                } else {
                    Glide.with(ForumActivity.this).pauseRequests();
                }
            }
        });
    }

    private void setUpHeaderView() {
        mHeaderView = getLayoutInflater().inflate(R.layout.layout_forum_header, null);
        mListTypeSpinner = (Spinner) mHeaderView.findViewById(R.id.layout_forum_header_spinner_list_type);

        String[] listTypeNames = getResources().getStringArray(R.array.thread_list_type_arrays);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listTypeNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mListTypeSpinner.setAdapter(dataAdapter);
        RecyclerView.LayoutParams headerLP = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeaderView.setLayoutParams(headerLP);
        mCollectionAdapter.addHeaderView(mHeaderView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DataContract.REQUEST_ID_NEW_THREAD) {
            if(data != null && data.hasExtra(DataContract.BUNDLE_THREAD_ID)) {
                long tid = data.getLongExtra(DataContract.BUNDLE_THREAD_ID, 0);
                Intent intent = new Intent(this, PostListActivity.class);
                intent.putExtra(DataContract.BUNDLE_THREAD_ID, tid);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(DataContract.BUNDLE_CONTENT_LIST_STORE)) {
            ArrayList<ThreadModel> list = savedInstanceState.getParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE);
            mCollectionAdapter.addAll(list);
            if(savedInstanceState.containsKey(FORUM_PINNED_KEY))
                mIsForumPinned = savedInstanceState.getBoolean(FORUM_PINNED_KEY);
            mForumId = savedInstanceState.getLong(DataContract.BUNDLE_FORUM_ID);
            mForumName = savedInstanceState.getString(DataContract.BUNDLE_FORUM_NAME);
            mForumDescription = savedInstanceState.getString(DataContract.BUNDLE_FORUM_DESCRIPTION);
            mLastItemSortKey = savedInstanceState.getLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY);
            mCurrentListType = savedInstanceState.getInt(DataContract.BUNDLE_THREAD_LIST_TYPE);
            mListTypeSpinner.setSelection(mCurrentListType);
        } else {
            mThreadCollectionView.getSwipeToRefresh().measure(1,1);
            mThreadCollectionView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().isForumPinned(mUserAccountManager.getCurrentUserId(), mForumId);
            getPresenter().loadThreadList(mForumId, mCurrentListType, false);
        }
        mListTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int currentListType = mCurrentListType;
                mCurrentListType = position;
                if(currentListType != mCurrentListType) {
                    getPresenter().loadThreadList(mForumId, mCurrentListType, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof ThemeColorChangedEvent) {
            setColorToViews(((ThemeColorChangedEvent) event).getNewPrimaryColor(), ((ThemeColorChangedEvent) event).getNewPrimaryDarkColor());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mIsForumPinned != null)
            outState.putBoolean(FORUM_PINNED_KEY, mIsForumPinned);
        outState.putLong(DataContract.BUNDLE_FORUM_ID, mForumId);
        outState.putString(DataContract.BUNDLE_FORUM_NAME, mForumName);
        outState.putString(DataContract.BUNDLE_FORUM_DESCRIPTION, mForumDescription);
        outState.putLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY, mLastItemSortKey);
        outState.putInt(DataContract.BUNDLE_THREAD_LIST_TYPE, mCurrentListType);
        outState.putParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE, mCollectionAdapter.getItemArrayList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forum, menu);
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        mPinForumMenuItem = menu.findItem(R.id.action_forum_pin_to_home);
        if(mPinForumMenuItem != null)
            mPinForumSwitch = (SwitchCompat) mPinForumMenuItem.getActionView().findViewById(android.R.id.checkbox);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mChangeThemeMenuItem != null) {
            if (isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_light_theme);
            else
                mChangeThemeMenuItem.setTitle(R.string.action_dark_theme);
        }
        if(mIsForumPinned != null && mPinForumSwitch != null) {
            mPinForumMenuItem.setVisible(true);
            mPinForumSwitch.setVisibility(View.VISIBLE);
            if(mIsForumPinned) {
                /*mPinForumMenuItem.setIcon(R.drawable.ic_action_unpin_forum);
                mPinForumMenuItem.setTitle(R.string.action_unpin_from_home);*/
                mPinForumSwitch.setText(R.string.action_forum_followed);
            }
            else {
                /*mPinForumMenuItem.setIcon(R.drawable.ic_action_pin_forum);
                mPinForumMenuItem.setTitle(R.string.action_pin_to_home);*/
                mPinForumSwitch.setText(R.string.action_forum_not_followed);
            }
            mPinForumSwitch.setOnCheckedChangeListener(null);
            mPinForumSwitch.setChecked(mIsForumPinned);
            mPinForumSwitch.setOnCheckedChangeListener(mOnPinForumCheckedChangeListener);
        } else {
            mPinForumMenuItem.setVisible(false);
            mPinForumSwitch.setVisibility(View.INVISIBLE);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivityWithTransition();
                return true;
            case R.id.action_change_theme:
                setNightMode(!isNightMode());
                return true;
            case R.id.action_forum_pin_to_home:

                return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().isForumPinned(mUserAccountManager.getCurrentUserId(), mForumId);
    }

    @Override
    protected void onPause() {
        super.onPause();
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
    }

    @Override
    public void showThreadList(List<ThreadModel> threadList, boolean isLoadMore) {
        if(isLoadMore) {
            if (threadList.size() == 0) isNoMore.set(true);
            mCollectionAdapter.addAll(threadList);
        } else {
            isNoMore.set(false);
            mCollectionAdapter.replaceWith(threadList);
            /*if (getResources().getBoolean(R.bool.isTablet)) {
                //isLoadingMore = true;
                loadMore(mCurrentListPageNumber);
            }*/
        }
        if(mCollectionAdapter.getItemCount() - mCollectionAdapter.getHeaderViewCount() > 0 ) {
            ThreadModel lastItem = mCollectionAdapter.getItem(mCollectionAdapter.getItemCount() - 1 - mCollectionAdapter.getHeaderViewCount());
            mLastItemSortKey = lastItem.getSortKey();
        } else {
            mLastItemSortKey = -1;
        }
    }

    @Override
    public void checkPinnedStatusDone(boolean isPinned) {
        mIsForumPinned = isPinned;
        invalidateOptionsMenu();
    }

    @Override
     public void setLoading(Boolean value) {
        isLoading.set(value);
        mThreadCollectionView.getSwipeToRefresh().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return isLoading.get();
    }

    @Override
    public void setLoadingMore(boolean value) {
        isLoadingMore.set(value);
        mThreadCollectionView.setLoadingMore(value);
        if(value)
            mThreadCollectionView.showMoreProgress();
        else
            mThreadCollectionView.hideMoreProgress();
    }

    @Override
    public boolean isLoadingMore() {
        return isLoadingMore.get();
    }

    public ForumPresenter getPresenter() {
        return mPresenter;
    }

    private void setColorToViews(int primaryColor, int primaryDarkColor) {
        mFab.setColorNormal(primaryColor);
        mFab.setColorPressed(primaryDarkColor);
    }

    private void pinForum() {
        getPresenter().pinForum(
                mUserAccountManager.getAuthObject(),
                mForumId,
                mForumName,
                ModelConverter.fidToForumIconUrl(mForumId)
        );
    }

    private void unpinForum() {
        getPresenter().unpinForum(
                mUserAccountManager.getAuthObject(), mForumId);
    }

    private CompoundButton.OnCheckedChangeListener mOnPinForumCheckedChangeListener = (buttonView, isChecked) -> {
        if (isChecked)
            pinForum();
        else
            unpinForum();
    };
}
