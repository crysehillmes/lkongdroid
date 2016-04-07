package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.malinskiy.superrecyclerview.OnMoreListener;

import org.cryse.lkong.R;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.SimpleCollectionItem;
import org.cryse.lkong.presenter.BasePresenter;
import org.cryse.lkong.ui.common.AbstractFragment;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.SimpleCollectionView;
import org.cryse.widget.recyclerview.Bookends;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;

public abstract class SimpleCollectionFragment<
        ItemType extends SimpleCollectionItem,
        AdapterType extends SimpleRecyclerViewAdapter<ItemType>,
        PresenterType extends BasePresenter>
        extends AbstractFragment
        implements SimpleCollectionView<ItemType> {
    private boolean isNoMore = false;
    private boolean isLoading = false;
    private boolean isLoadingMore = false;
    protected long mLastItemSortKey = 0;

    AppNavigation mNavigation = new AppNavigation();

    @Inject
    UserAccountManager mUserAccountManager;

    @Nullable
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.simple_collection_recyclerview)
    SuperRecyclerView mCollectionView;

    ProgressBar mMoreProgressBar;

    AdapterType mCollectionAdapter;
    Bookends<AdapterType> mWrapperAdapter;

    List<ItemType> mItemList = new ArrayList<ItemType>();

    @Override
    protected abstract void injectThis();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(getLayoutId(), null);
        ButterKnife.bind(this, contentView);
        initRecyclerView();
        setUpToolbar();
        return contentView;
    }

    private void setUpToolbar() {
        if(mToolbar != null) {
            mToolbar.setOnClickListener((view) -> {
                mCollectionView.getRecyclerView().smoothScrollToPosition(0);
            });
        }
    }

    private void initRecyclerView() {
        UIUtils.InsetsValue insetsValue = getRecyclerViewInsets();
        if(insetsValue != null)
            mCollectionView.setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());
        mCollectionView.getRecyclerView().setItemAnimator(getRecyclerViewItemAnimator());
        mCollectionView.setLayoutManager(getRecyclerViewLayoutManager());
        mCollectionAdapter = createAdapter(mItemList);
        mWrapperAdapter = new Bookends<>(mCollectionAdapter);
        mCollectionView.setAdapter(mWrapperAdapter);
        initHeaderView();
        initFooterView();
        if(getRefreshListener() != null)
            mCollectionView.setRefreshListener(getRefreshListener());
        mCollectionView.setOnMoreListener(getOnMoreListener());
        mCollectionAdapter.setOnItemClickListener(this::onItemClick);
        onCollectionViewInitComplete();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY, mLastItemSortKey);
        outState.putParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE, mCollectionAdapter.getItemArrayList());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mItemList.size() == 0) {
            if (savedInstanceState != null && savedInstanceState.containsKey(DataContract.BUNDLE_CONTENT_LIST_STORE)) {

                ArrayList<ItemType> list = savedInstanceState.getParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE);
                mCollectionAdapter.addAll(list);
                mLastItemSortKey = savedInstanceState.getLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY);

            } else {
                if(getView() != null)
                    getView().post(() -> loadInitialData());
                else
                    loadInitialData();
            }
        }
    }

    public void restoreFromState(Bundle savedInstanceState) {

    }

    public void loadInitialData() {
        loadData(mUserAccountManager.getAuthObject(), 0, false);
    }

    @Override
    public void onResume() {
        super.onResume();
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
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, getLogTag());
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, getLogTag());
    }

    @Override
    public void showSimpleData(List<ItemType> items, boolean loadMore) {
        if(loadMore) {
            if (items.size() == 0) isNoMore = true;
            mCollectionAdapter.addAll(items);
        } else {
            isNoMore = false;
            mCollectionAdapter.replaceWith(items);
        }
        if(mCollectionAdapter.getItemCount() > 1) {
            ItemType lastItem = mCollectionAdapter.getItem(mCollectionAdapter.getItemCount() - 1);
            mLastItemSortKey = lastItem.getSortKey();
        } else {
            mLastItemSortKey = 0;
        }
    }

    @Override
    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    @Override
    public void setLoadingMore(boolean value) {
        isLoadingMore = value;
        mCollectionView.setLoadingMore(value);
        if(value)
            mCollectionView.showMoreProgress();
        else
            mCollectionView.hideMoreProgress();
    }

    @Override
    public void setLoading(Boolean value) {
        isLoading = value;
        if (isLoading) {
            if(!mCollectionView.getSwipeToRefresh().isRefreshing()) {
                mCollectionView.hideRecycler();
                mCollectionView.showProgress();
            }
        } else {
            if(mCollectionView.getSwipeToRefresh().isRefreshing())
                mCollectionView.getSwipeToRefresh().setRefreshing(isLoading);
            mCollectionView.hideProgress();
            mCollectionView.showRecycler();
        }
    }

    @Override
    public Boolean isLoading() {
        return isLoading;
    }

    protected abstract String getLogTag();

    protected abstract int getLayoutId();

    protected abstract PresenterType getPresenter();

    protected abstract AdapterType createAdapter(List<ItemType> itemList);

    protected abstract void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs);

    protected abstract void onItemClick(View view, int position, long id);

    protected void onCollectionViewInitComplete() {

    }

    protected RecyclerView.ItemAnimator getRecyclerViewItemAnimator() {
        return new DefaultItemAnimator();
    }

    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected void initHeaderView() {

    }

    protected void initFooterView() {
        mMoreProgressBar = new ProgressBar(getActivity());
        RecyclerView.LayoutParams moreProgressLP = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mMoreProgressBar.setLayoutParams(moreProgressLP);
        mWrapperAdapter.addFooter(mMoreProgressBar);
    }

    protected void onEvent(AbstractEvent event) {

    }

    protected SwipeRefreshLayout.OnRefreshListener getRefreshListener() {
        return () ->
                loadData(mUserAccountManager.getAuthObject(), 0, false);
    }

    protected OnMoreListener getOnMoreListener() {
        return new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                if (!isNoMore && !isLoadingMore && mLastItemSortKey != 0) {
                    loadData(mUserAccountManager.getAuthObject(), mLastItemSortKey, true);
                } else {
                    mCollectionView.setLoadingMore(false);
                    mCollectionView.hideMoreProgress();
                }
            }

            @Override
            public void onChangeMoreVisibility(int visibility) {
                mMoreProgressBar.setVisibility(visibility);
            }
        };
    }

    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return UIUtils.getInsets(getActivity(), mCollectionView, false, false, false, getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
    }

    protected long getLastItemSortKey() {
        return mLastItemSortKey;
    }
}
