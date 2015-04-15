package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.logic.ThreadListType;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.presenter.ThreadListPresenter;
import org.cryse.lkong.ui.adapter.ThreadListAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.ThreadListView;
import org.cryse.lkong.widget.FloatingActionButtonEx;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ThreadListActivity extends AbstractThemeableActivity implements ThreadListView {
    public static final String LOG_TAG = ThreadListActivity.class.getName();
    private AtomicBoolean isNoMore = new AtomicBoolean(false);
    private AtomicBoolean isLoading = new AtomicBoolean(false);
    private AtomicBoolean isLoadingMore = new AtomicBoolean(false);
    private long mLastItemSortKey = -1;
    Picasso mPicasso;
    @Inject
    ThreadListPresenter mPresenter;

    @Inject
    AndroidNavigation mAndroidNavigation;

    @Inject
    UserAccountManager mUserAccountManager;

    @InjectView(R.id.activity_forum_thread_list_recyclerview)
    SuperRecyclerView mThreadCollectionView;
    @InjectView(R.id.fab)
    FloatingActionButtonEx mFab;

    View mHeaderView;
    View mTopPaddingHeaderView;
    Spinner mListTypeSpinner;

    ThreadListAdapter mCollectionAdapter;

    List<ThreadModel> mItemList = new ArrayList<ThreadModel>();

    private long mForumId = -1;
    private String mForumName = "";
    private String mForumDescription = "";
    private int mCurrentListType = ThreadListType.TYPE_SORT_BY_REPLY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_thread_list);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        ButterKnife.inject(this);
        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initRecyclerView();
        setUpHeaderView();
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_FORUM_ID) && intent.hasExtra(DataContract.BUNDLE_FORUM_NAME)) {
            mForumId = intent.getLongExtra(DataContract.BUNDLE_FORUM_ID, -1);
            mForumName = intent.getStringExtra(DataContract.BUNDLE_FORUM_NAME);
            mForumDescription = intent.getStringExtra(DataContract.BUNDLE_FORUM_DESCRIPTION);
        }
        if(mForumId == -1 || TextUtils.isEmpty(mForumName))
            throw new IllegalStateException("ThreadListActivity missing extra in intent.");
        setTitle(mForumName);


    }
    private void initRecyclerView() {
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        int statusBarSize = UIUtils.calculateStatusBarSize(this);
        mThreadCollectionView.getSwipeToRefresh().setProgressViewOffset(true, statusBarSize, actionBarSize * 2);
        mThreadCollectionView.setItemAnimator(new DefaultItemAnimator());
        mThreadCollectionView.setLayoutManager(new LinearLayoutManager(this));
        mCollectionAdapter = new ThreadListAdapter(this, mPicasso, mItemList);
        mThreadCollectionView.setAdapter(mCollectionAdapter);

        mTopPaddingHeaderView = getLayoutInflater().inflate(R.layout.layout_empty_recyclerview_top_padding, null);
        ((TextView)mTopPaddingHeaderView).setText(getString(R.string.text_load_prev_page));
        RecyclerView.LayoutParams topPaddingLP = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.calculateActionBarSize(this) + getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
        mTopPaddingHeaderView.setLayoutParams(topPaddingLP);
        mCollectionAdapter.addHeaderView(mTopPaddingHeaderView);

        mThreadCollectionView.setRefreshListener(() -> getPresenter().loadThreadList(mForumId, mCurrentListType, false));
        mThreadCollectionView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!isNoMore.get() && !isLoadingMore.get() && mLastItemSortKey != -1) {
                getPresenter().loadThreadList(mForumId, mLastItemSortKey, mCurrentListType, true);
            } else {
                mThreadCollectionView.setLoadingMore(false);
                mThreadCollectionView.hideMoreProgress();
            }
        });
        mThreadCollectionView.setOnItemClickListener((view, position, id) -> {
            int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
            if (itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                ThreadModel item = mCollectionAdapter.getItem(itemIndex);
                Intent intent = new Intent(this, PostListActivity.class);
                String idString = item.getId().substring(7);
                long tid = Long.parseLong(idString);
                intent.putExtra(DataContract.BUNDLE_THREAD_ID, tid);
                startActivity(intent);
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
                    mPicasso.resumeTag(ThreadListAdapter.THREAD_PICASSO_TAG);
                } else {
                    mPicasso.pauseTag(ThreadListAdapter.THREAD_PICASSO_TAG);
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
            mForumId = savedInstanceState.getLong(DataContract.BUNDLE_FORUM_ID);
            mForumName = savedInstanceState.getString(DataContract.BUNDLE_FORUM_NAME);
            mForumDescription = savedInstanceState.getString(DataContract.BUNDLE_FORUM_DESCRIPTION);
            mLastItemSortKey = savedInstanceState.getLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY);
            mCurrentListType = savedInstanceState.getInt(DataContract.BUNDLE_THREAD_LIST_TYPE);
            mListTypeSpinner.setSelection(mCurrentListType);
        } else {
            mThreadCollectionView.getSwipeToRefresh().measure(1,1);
            mThreadCollectionView.getSwipeToRefresh().setRefreshing(true);
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
        outState.putLong(DataContract.BUNDLE_FORUM_ID, mForumId);
        outState.putString(DataContract.BUNDLE_FORUM_NAME, mForumName);
        outState.putString(DataContract.BUNDLE_FORUM_DESCRIPTION, mForumDescription);
        outState.putLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY, mLastItemSortKey);
        outState.putInt(DataContract.BUNDLE_THREAD_LIST_TYPE, mCurrentListType);
        outState.putParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE, mCollectionAdapter.getItemArrayList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_thread_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishCompat();
                return true;
            case R.id.action_change_theme:
                setNightMode(!isNightMode());
                return true;
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
        mPicasso.shutdown();
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
        if(mCollectionAdapter.getItemCount() > 0) {
            ThreadModel lastItem = mCollectionAdapter.getItem(mCollectionAdapter.getItemCount() - 1 - mCollectionAdapter.getHeaderViewCount());
            mLastItemSortKey = lastItem.getSortKey();
        } else {
            mLastItemSortKey = -1;
        }
    }

    @Override
    public void showToast(int text_value, int toastType) {
        ToastProxy.showToast(this, getString(text_value), toastType);
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

    public ThreadListPresenter getPresenter() {
        return mPresenter;
    }

    private void setColorToViews(int primaryColor, int primaryDarkColor) {
        mFab.setColorNormal(primaryColor);
        mFab.setColorPressed(primaryDarkColor);
    }
}
