package org.cryse.lkong.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.lkong.broadcast.BroadcastConstants;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.presenter.FollowedForumsPresenter;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.ui.adapter.ForumListAdapter;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.preference.BooleanPrefs;
import org.cryse.utils.preference.Prefs;

import java.util.List;

import javax.inject.Inject;


public class FollowedForumsFragment extends SimpleCollectionFragment<
        ForumModel,
        ForumListAdapter,
        FollowedForumsPresenter> {
    private static final String LOG_TAG = FollowedForumsFragment.class.getName();

    @Inject
    FollowedForumsPresenter mPresenter;

    AppNavigation mNavigation = new AppNavigation();

    private BooleanPrefs mShowInGridPrefs;

    public static FollowedForumsFragment newInstance(Bundle args) {
        FollowedForumsFragment fragment = new FollowedForumsFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mShowInGridPrefs = Prefs.getBooleanPrefs(
                PreferenceConstant.SHARED_PREFERENCE_FORUMS_IN_GRID,
                PreferenceConstant.SHARED_PREFERENCE_FORUMS_IN_GRID_VALUE
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //loadData(null, 0, false);
        getActivity().registerReceiver(mSyncFollowedForumsDoneBroadcastReceiver, new IntentFilter(BroadcastConstants.BROADCAST_SYNC_FOLLOWED_FORUMS_DONE));
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mSyncFollowedForumsDoneBroadcastReceiver);
        super.onPause();
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_simple_collection;
    }

    @Override
    protected FollowedForumsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected ForumListAdapter createAdapter(List<ForumModel> itemList) {
        ForumListAdapter adapter =  new ForumListAdapter(this, mATEKey, mItemList);
        adapter.setShowInGrid(mShowInGridPrefs.get());
        return adapter;
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadFollowedForums(mUserAccountManager.getAuthObject());
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position;
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemCount()) {
            ForumModel item = mCollectionAdapter.getItem(position);
            mNavigation.openActivityForForumByForumId(getActivity(), item.getFid(), item.getName(), "");
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if (event instanceof CurrentAccountChangedEvent) {
            getPresenter().loadFollowedForums(mUserAccountManager.getAuthObject());
            /*Account account = mUserAccountManager.getCurrentUserAccount().getAccount();
            if(account != null)
                SyncUtils.manualSync(account, SyncUtils.SYNC_AUTHORITY_FOLLOW_STATUS);*/
        }
    }

    protected RecyclerView.ItemAnimator getRecyclerViewItemAnimator() {
        return null;
    }

    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new GridLayoutManager(
                getActivity(),
                getResources().getInteger(
                        mShowInGridPrefs.get() ? R.integer.forumlist_column_count : R.integer.forumlist_detail_column_count
                )
        );
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return null;
    }

    private BroadcastReceiver mSyncFollowedForumsDoneBroadcastReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            // update your views
            loadData(null, 0, false);
        }
    };
}
