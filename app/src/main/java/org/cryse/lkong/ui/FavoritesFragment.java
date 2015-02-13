package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.event.FavoritesChangedEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.presenter.FavoritesPresenter;
import org.cryse.lkong.ui.adapter.ThreadListAdapter;
import org.cryse.lkong.ui.common.InActivityFragment;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.ThreadListView;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FavoritesFragment extends InActivityFragment implements ThreadListView {
    public static final String LOG_TAG = FavoritesFragment.class.getName();
    private static final String BUNDLE_NEED_REFRESH = "bundle_favorites_need_refresh";
    private boolean isNoMore = false;
    private boolean isLoading = false;
    private boolean isLoadingMore = false;
    private long mLastItemSortKey = -1;
    @Inject
    FavoritesPresenter mPresenter;

    @Inject
    RxEventBus mEventBus;

    @Inject
    UserAccountManager mUserAccountManager;

    @InjectView(R.id.fragment_favorites_recyclerview)
    SuperRecyclerView mThreadCollectionView;

    ThreadListAdapter mCollectionAdapter;

    List<ThreadModel> mItemList = new ArrayList<ThreadModel>();

    boolean mNeedRefresh = false;

    public static FavoritesFragment newInstance(Bundle args) {
        FavoritesFragment fragment = new FavoritesFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
     public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectThis();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_favorites, null);
        ButterKnife.inject(this, contentView);
        initRecyclerView();
        return contentView;
    }

    private void initRecyclerView() {
        UIUtils.InsetsValue insetsValue = UIUtils.getInsets(getActivity(), mThreadCollectionView, true);
        mThreadCollectionView.setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());
        mThreadCollectionView.setItemAnimator(new DefaultItemAnimator());
        mThreadCollectionView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCollectionAdapter = new ThreadListAdapter(getActivity(), mItemList);
        mThreadCollectionView.setAdapter(mCollectionAdapter);
        mThreadCollectionView.setRefreshListener(() -> getPresenter().loadFavorites(mUserAccountManager.getAuthObject(), false));
        mThreadCollectionView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!isNoMore && !isLoadingMore && mLastItemSortKey != -1) {
                getPresenter().loadFavorites(mUserAccountManager.getAuthObject(), mLastItemSortKey, true);
            } else {
                mThreadCollectionView.setLoadingMore(false);
                mThreadCollectionView.hideMoreProgress();
            }
        });
        mThreadCollectionView.setOnItemClickListener((view, position, id) -> {
            ThreadModel item = mCollectionAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), PostListActivity.class);
            String idString = item.getId().substring(7);
            long tid = Long.parseLong(idString);
            intent.putExtra(DataContract.BUNDLE_THREAD_ID, tid);
            startActivity(intent);
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.drawer_item_favorites);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY, mLastItemSortKey);
        outState.putParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE, mCollectionAdapter.getItemArrayList());
        outState.putBoolean(BUNDLE_NEED_REFRESH, mNeedRefresh);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(DataContract.BUNDLE_CONTENT_LIST_STORE)) {
            ArrayList<ThreadModel> list = savedInstanceState.getParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE);
            mCollectionAdapter.addAll(list);
            mLastItemSortKey = savedInstanceState.getLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY);
            mNeedRefresh = savedInstanceState.getBoolean(BUNDLE_NEED_REFRESH);
        } else {
            mThreadCollectionView.getSwipeToRefresh().measure(1,1);
            mThreadCollectionView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().loadFavorites(mUserAccountManager.getAuthObject(), false);
        }

        mEventBus.toObservable().subscribe(event -> {
           if(event instanceof FavoritesChangedEvent)
               mNeedRefresh = true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mNeedRefresh) {
            mNeedRefresh = false;
            getPresenter().loadFavorites(mUserAccountManager.getAuthObject(), false);
        }
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
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
    public void showThreadList(List<ThreadModel> threadList, boolean isLoadMore) {
        if(isLoadMore) {
            if (threadList.size() == 0) isNoMore = true;
            mCollectionAdapter.addAll(threadList);
        } else {
            isNoMore = false;
            mCollectionAdapter.replaceWith(threadList);
        }
        if(mCollectionAdapter.getItemCount() > 0) {
            ThreadModel lastItem = mCollectionAdapter.getItem(mCollectionAdapter.getItemCount() - 1);
            mLastItemSortKey = lastItem.getSortKey();
        } else {
            mLastItemSortKey = -1;
        }
    }

    @Override
    public boolean isLoadingMore() {
        return isLoadingMore;
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
    public void setLoading(Boolean value) {
        isLoading = value;
        mThreadCollectionView.getSwipeToRefresh().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return isLoading;
    }

    @Override
    public void showToast(int text_value, int toastType) {
        ToastProxy.showToast(getActivity(), getString(text_value), toastType);
    }

    public FavoritesPresenter getPresenter() {
        return mPresenter;
    }
}
