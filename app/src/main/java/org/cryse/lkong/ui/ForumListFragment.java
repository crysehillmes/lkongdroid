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
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.presenter.ForumListPresenter;
import org.cryse.lkong.ui.adapter.ForumListAdapter;
import org.cryse.lkong.ui.common.MainActivityFragment;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.view.ForumListView;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.widget.recyclerview.RecyclerViewOnItemClickListener;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ForumListFragment extends MainActivityFragment implements ForumListView {
    private static final String LOG_TAG = ForumListFragment.class.getName();
    @InjectView(R.id.fragment_forum_list_recyclerview)
    SuperRecyclerView mRecyclerView;

    ForumListAdapter mListAdapter;

    @Inject
    ForumListPresenter mPresenter;

    List<ForumModel> mForumList = new ArrayList<ForumModel>();

    public static ForumListFragment newInstance(Bundle args) {
        ForumListFragment fragment = new ForumListFragment();
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
        View contentView = inflater.inflate(R.layout.fragment_forum_list, null);
        ButterKnife.inject(this, contentView);
        initRecyclerView();
        return contentView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forum_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
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
    public void onResume() {
        super.onResume();
        if(mForumList.size() == 0)
            getPresenter().getForumList(false);
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
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.drawer_item_forum_list);
    }

    @Override
     public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPresenter().getForumList(true);
    }

    private void initRecyclerView() {
        UIUtils.InsetsValue insetsValue = UIUtils.getInsets(getActivity(), mRecyclerView, true);
        mRecyclerView.setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListAdapter = new ForumListAdapter(getActivity(), mForumList);
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.setRefreshListener(() -> getPresenter().getForumList(true));
        mRecyclerView.setOnItemClickListener((view, position, id) -> {
            ForumModel item = mListAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), ThreadListActivity.class);
            intent.putExtra(DataContract.BUNDLE_FORUM_ID, item.getFid());
            intent.putExtra(DataContract.BUNDLE_FORUM_NAME, item.getName());
            startActivity(intent);
        });
    }

    @Override
    public void showForumList(List<ForumModel> forumList) {
        if(forumList == null) return;
        mListAdapter.replaceWith(forumList);
    }

    @Override
    public void setLoading(Boolean value) {
        if(value) {
            mRecyclerView.getSwipeToRefresh().setRefreshing(true);
        } else {
            mRecyclerView.getSwipeToRefresh().setRefreshing(false);
        }
    }

    @Override
    public Boolean isLoading() {
        return mRecyclerView.getSwipeToRefresh().isRefreshing();
    }

    @Override
    public void showToast(int text_value, int toastType) {
        ToastProxy.showToast(getActivity(), getString(ToastErrorConstant.errorCodeToStringRes(text_value)), toastType);
    }

    public ForumListPresenter getPresenter() {
        return mPresenter;
    }
}
