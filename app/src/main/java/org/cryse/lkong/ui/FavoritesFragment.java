package org.cryse.lkong.ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.broadcast.BroadcastConstants;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.event.FavoritesChangedEvent;
import org.cryse.lkong.event.NoticeCountEvent;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.presenter.FavoritesPresenter;
import org.cryse.lkong.sync.SyncUtils;
import org.cryse.lkong.ui.adapter.ThreadListAdapter;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.FavoritesView;

import java.util.List;

import javax.inject.Inject;

public class FavoritesFragment extends SimpleCollectionFragment<
        ThreadModel,
        ThreadListAdapter,
        FavoritesPresenter> implements FavoritesView<ThreadModel> {
    private static final String LOG_TAG = FavoritesFragment.class.getName();

    boolean mNeedRefresh = false;
    @Inject
    FavoritesPresenter mPresenter;

    protected MenuItem mChangeThemeMenuItem;
    private MenuItem mNotificationMenuItem;
    private boolean mHasNotification = false;

    public static FavoritesFragment newInstance(Bundle args) {
        FavoritesFragment fragment = new FavoritesFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getThemedActivity().setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getPrimaryColor());
        final ActionBar actionBar = getThemedActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        mNotificationMenuItem = menu.findItem(R.id.action_open_notification);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(mChangeThemeMenuItem != null) {
            if(isNightMode() == null)
                mChangeThemeMenuItem.setVisible(false);
            else if(isNightMode() != null && isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_light_theme);
            else if(isNightMode() != null && !isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_dark_theme);
        }
        if(mNotificationMenuItem != null) {
            if(mHasNotification) mNotificationMenuItem.setIcon(R.drawable.ic_action_notification_red_dot);
            else mNotificationMenuItem.setIcon(R.drawable.ic_action_notification);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_search:
                mNavigation.navigateToSearchActivity(getActivity());
                return true;
            case R.id.action_open_notification:
                mNavigation.navigateToNotificationActivity(getActivity());
                return true;
            case R.id.action_change_theme:
                if(isNightMode() != null) {
                    getThemedActivity().setNightMode(!isNightMode());
                }
                return true;
            case android.R.id.home:
                if(getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).getNavigationDrawer().openDrawer();
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActivityTitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNewNoticeCount();
        if(mNeedRefresh) {
            mNeedRefresh = false;
            getPresenter().loadFavorites(mUserAccountManager.getAuthObject(), false);
        }
        IntentFilter checkNoticeIntentFilter = new IntentFilter(BroadcastConstants.BROADCAST_SYNC_CHECK_NOTICE_COUNT_DONE);
        checkNoticeIntentFilter.setPriority(10);
        getActivity().registerReceiver(mCheckNoticeCountDoneBroadcastReceiver, checkNoticeIntentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mCheckNoticeCountDoneBroadcastReceiver);
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
        return R.layout.fragment_favorites;
    }

    @Override
    protected FavoritesPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected ThreadListAdapter createAdapter(List<ThreadModel> itemList) {
        ThreadListAdapter adapter = new ThreadListAdapter(getActivity(), mItemList);
        adapter.setOnThreadItemClickListener(new ThreadListAdapter.OnThreadItemClickListener() {
            @Override
            public void onProfileAreaClick(View view, int position, long uid) {
                int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
                if (itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    ThreadModel model = mCollectionAdapter.getItem(itemIndex);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mNavigation.openActivityForUserProfile(getActivity(), startingLocation, model.getUid());
                }
            }

            @Override
            public void onItemThreadClick(View view, int adapterPosition) {
                int itemIndex = adapterPosition - mCollectionAdapter.getHeaderViewCount();
                if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    ThreadModel item = mCollectionAdapter.getItem(itemIndex);
                    String idString = item.getId().substring(7);
                    long tid = Long.parseLong(idString);
                    mNavigation.openActivityForPostListByThreadId(getActivity(), tid);
                }
            }
        });
        return adapter;
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadFavorites(authObject, start, isLoadingMore);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            ThreadModel item = mCollectionAdapter.getItem(position);
            String idString = item.getId().substring(7);
            long tid = Long.parseLong(idString);
            mNavigation.openActivityForPostListByThreadId(getActivity(), tid);
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof FavoritesChangedEvent)
            mNeedRefresh = true;
        else if(event instanceof NoticeCountEvent) {
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
        } else if (event instanceof CurrentAccountChangedEvent) {
            loadData(mUserAccountManager.getAuthObject(), 0, false);
            checkNewNoticeCount();
        }
    }

    protected void setActivityTitle() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            mainActivity.onSectionAttached(getString(R.string.drawer_item_favorites));
        }
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return null;
    }

    protected void checkNewNoticeCount() {
        if (isAdded()) {
            Account account = mUserAccountManager.getCurrentUserAccount().getAccount();
            if(account != null)
                SyncUtils.manualSync(account, SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE);
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
        }
    }

    @Override
    public void onCheckNoticeCountComplete(NoticeCountModel noticeCountModel) {
        if(noticeCountModel != null) {
            mHasNotification = noticeCountModel.hasNotification() && noticeCountModel.getUserId() == mUserAccountManager.getCurrentUserId();
            if(getActivity() != null)
                getActivity().invalidateOptionsMenu();
        }
    }

    private BroadcastReceiver mCheckNoticeCountDoneBroadcastReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            // update your views
            // loadData(null, 0, false);
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
            abortBroadcast();
        }
    };
}
