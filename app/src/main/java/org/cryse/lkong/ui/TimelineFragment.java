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
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.logic.TimelineListType;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.presenter.TimelinePresenter;
import org.cryse.lkong.ui.adapter.TimelineAdapter;
import org.cryse.lkong.ui.common.MainActivityFragment;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.TimelineView;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TimelineFragment extends MainActivityFragment implements TimelineView {
    public static final String BUNDLE_LIST_TYPE = "timeline_list_type";
    private boolean isNoMore = false;
    private boolean isLoading = false;
    private boolean isLoadingMore = false;
    private long mLastItemSortKey = -1;
    private int mListType;
    @Inject
    TimelinePresenter mPresenter;

    @Inject
    RxEventBus mEventBus;

    @Inject
    AndroidNavigation mAndroidNavigation;

    @Inject
    UserAccountManager mUserAccountManager;

    @InjectView(R.id.fragment_timeline_recyclerview)
    SuperRecyclerView mCollectionView;

    TimelineAdapter mCollectionAdapter;

    List<TimelineModel> mItemList = new ArrayList<TimelineModel>();

    public static TimelineFragment newInstance(Bundle args) {
        TimelineFragment fragment = new TimelineFragment();
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
        Bundle args = getArguments();
        if(args == null)
            throw new IllegalArgumentException();
        mListType = args.getInt(BUNDLE_LIST_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_timeline, null);
        ButterKnife.inject(this, contentView);
        initRecyclerView();
        return contentView;
    }

    private void initRecyclerView() {
        UIUtils.InsetsValue insetsValue = UIUtils.getInsets(getActivity(), mCollectionView, true);
        mCollectionView.setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());
        mCollectionView.setItemAnimator(new DefaultItemAnimator());
        mCollectionView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCollectionAdapter = new TimelineAdapter(getActivity(), mItemList);
        mCollectionView.setAdapter(mCollectionAdapter);
        mCollectionView.setRefreshListener(() -> getPresenter().loadTimeline(mUserAccountManager.getAuthObject(), mListType, false));
        mCollectionView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!isNoMore && !isLoadingMore && mLastItemSortKey != -1) {
                getPresenter().loadTimeline(mUserAccountManager.getAuthObject(), mLastItemSortKey, mListType, true);
            } else {
                mCollectionView.setLoadingMore(false);
                mCollectionView.hideMoreProgress();
            }
        });
        mCollectionView.setOnItemClickListener((view, position, id) -> {
            TimelineModel item = mCollectionAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), PostListActivity.class);
            intent.putExtra(DataContract.BUNDLE_THREAD_ID, item.getTid());
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
        if(mListType == TimelineListType.TYPE_AT_ME) {
            return getString(R.string.drawer_item_at_me);
        } else if(mListType == TimelineListType.TYPE_TIMELINE) {
            return getString(R.string.drawer_item_timeline);
        } else {
            throw new IllegalStateException("Wrong list type.");
        }
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

        if(savedInstanceState != null && savedInstanceState.containsKey(DataContract.BUNDLE_CONTENT_LIST_STORE)) {
            ArrayList<TimelineModel> list = savedInstanceState.getParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE);
            mCollectionAdapter.addAll(list);
            mLastItemSortKey = savedInstanceState.getLong(DataContract.BUNDLE_THREAD_LIST_LAST_SORTKEY);
        } else {
            mCollectionView.getSwipeToRefresh().measure(1,1);
            mCollectionView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().loadTimeline(mUserAccountManager.getAuthObject(), mListType, false);
        }

        mEventBus.toObservable().subscribe(event -> {
        });
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
    public void showTimeline(List<TimelineModel> timelineItems, boolean loadMore) {
        if(loadMore) {
            if (timelineItems.size() == 0) isNoMore = true;
            // isLoadingMore = false;
            // if (threadList.size() != 0) mCurrentListPageNumber++;
            // addToListView(novels);
            mCollectionAdapter.addAll(timelineItems);
        } else {
            isNoMore = false;
            // mCurrentListPageNumber = 0;
            // mNovelList.clear();
            // addToListView(novels);
            mCollectionAdapter.replaceWith(timelineItems);
            /*if (getResources().getBoolean(R.bool.isTablet)) {
                //isLoadingMore = true;
                loadMore(mCurrentListPageNumber);
            }*/
        }
        if(mCollectionAdapter.getItemCount() > 0) {
            TimelineModel lastItem = mCollectionAdapter.getItem(mCollectionAdapter.getItemCount() - 1);
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
        mCollectionView.setLoadingMore(value);
        if(value)
            mCollectionView.showMoreProgress();
        else
            mCollectionView.hideMoreProgress();
    }

    @Override
    public void setLoading(Boolean value) {
        isLoading = value;
        mCollectionView.getSwipeToRefresh().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return isLoading;
    }

    @Override
    public void showToast(int text_value, int toastType) {
        ToastProxy.showToast(getActivity(), getString(text_value), toastType);
    }

    public TimelinePresenter getPresenter() {
        return mPresenter;
    }
}
