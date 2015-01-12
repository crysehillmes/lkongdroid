package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.logic.ThreadListType;
import org.cryse.lkong.model.ForumThreadModel;
import org.cryse.lkong.presenter.ThreadListPresenter;
import org.cryse.lkong.ui.adapter.ThreadListAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.ThreadListView;
import org.cryse.utils.ColorUtils;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ThreadListActivity extends AbstractThemeableActivity implements ThreadListView {
    private boolean isNoMore = false;
    private boolean isLoading = false;
    private boolean isLoadingMore = false;
    private long mLastItemSortKey = -1;
    @Inject
    ThreadListPresenter mPresenter;

    @InjectView(R.id.activity_forum_thread_list_recyclerview)
    SuperRecyclerView mThreadCollectionView;

    ThreadListAdapter mCollectionAdapter;

    List<ForumThreadModel> mItemList = new ArrayList<ForumThreadModel>();

    private long mForumId = -1;
    private String mForumName = "";
    private int mCurrentListType = ThreadListType.TYPE_SORT_BY_REPLY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_thread_list);
        ButterKnife.inject(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        initRecyclerView();
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_FORUM_ID) && intent.hasExtra(DataContract.BUNDLE_FORUM_NAME)) {
            mForumId = intent.getLongExtra(DataContract.BUNDLE_FORUM_ID, -1);
            mForumName = intent.getStringExtra(DataContract.BUNDLE_FORUM_NAME);
        }
        if(mForumId == -1 || TextUtils.isEmpty(mForumName))
            throw new IllegalStateException("ThreadListActivity missing extra in intent.");
        setTitle(mForumName);


    }
    private void initRecyclerView() {
        UIUtils.setInsets(this, mThreadCollectionView, true);
        mThreadCollectionView.setItemAnimator(new DefaultItemAnimator());
        mThreadCollectionView.setLayoutManager(new LinearLayoutManager(this));
        mCollectionAdapter = new ThreadListAdapter(this, mItemList);
        mThreadCollectionView.setAdapter(mCollectionAdapter);
        mThreadCollectionView.setRefreshListener(() -> getPresenter().loadThreadList(mForumId, mCurrentListType, false));
        mThreadCollectionView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!isNoMore && !isLoadingMore && mLastItemSortKey != -1) {
                getPresenter().loadThreadList(mForumId, mLastItemSortKey, mCurrentListType, true);
            } else {
                mThreadCollectionView.setLoadingMore(false);
                mThreadCollectionView.hideMoreProgress();
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(DataContract.BUNDLE_CONTENT_LIST_STORE)) {
            ArrayList<ForumThreadModel> list = savedInstanceState.getParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE);
            mCollectionAdapter.addAll(list);
            mForumId = savedInstanceState.getLong(DataContract.BUNDLE_FORUM_ID);
            mForumName = savedInstanceState.getString(DataContract.BUNDLE_FORUM_NAME);
            mLastItemSortKey = savedInstanceState.getLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY);
        } else {
            mThreadCollectionView.getSwipeToRefresh().measure(1,1);
            mThreadCollectionView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().loadThreadList(mForumId, mCurrentListType, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DataContract.BUNDLE_FORUM_ID, mForumId);
        outState.putString(DataContract.BUNDLE_FORUM_NAME, mForumName);
        outState.putLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY, mLastItemSortKey);
        outState.putParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE, mCollectionAdapter.getItemArrayList());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();
                else
                    finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
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
    }

    @Override
    public void showThreadList(List<ForumThreadModel> threadList, boolean isLoadMore) {
        if(isLoadMore) {
            if (threadList.size() == 0) isNoMore = true;
            // isLoadingMore = false;
            // if (threadList.size() != 0) mCurrentListPageNumber++;
            // addToListView(novels);
            mCollectionAdapter.addAll(threadList);
        } else {
            isNoMore = false;
            // mCurrentListPageNumber = 0;
            // mNovelList.clear();
            // addToListView(novels);
            mCollectionAdapter.replaceWith(threadList);
            /*if (getResources().getBoolean(R.bool.isTablet)) {
                //isLoadingMore = true;
                loadMore(mCurrentListPageNumber);
            }*/
        }
        if(mCollectionAdapter.getItemCount() > 0) {
            ForumThreadModel lastItem = mCollectionAdapter.getItem(mCollectionAdapter.getItemCount() - 1);
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
        isLoading = value;
        mThreadCollectionView.getSwipeToRefresh().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return isLoading;
    }

    @Override
    public void setLoadingMore(boolean value) {
        isLoadingMore = value;
        mThreadCollectionView.setLoadingMore(value);
        if(value)
            mThreadCollectionView.showMoreProgress();
        else
            mThreadCollectionView.hideMoreProgress();
    }

    @Override
    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    public ThreadListPresenter getPresenter() {
        return mPresenter;
    }
}
