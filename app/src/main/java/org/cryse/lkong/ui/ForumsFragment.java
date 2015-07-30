package org.cryse.lkong.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.FavoritesChangedEvent;
import org.cryse.lkong.event.NoticeCountEvent;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.presenter.ForumsPresenter;
import org.cryse.lkong.ui.adapter.ForumListAdapter;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.ForumsView;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.List;

import javax.inject.Inject;


public class ForumsFragment extends SimpleCollectionFragment<
        ForumModel,
        ForumListAdapter,
        ForumsPresenter> implements ForumsView<ForumModel> {
    private static final String LOG_TAG = ForumsFragment.class.getName();

    boolean mNeedRefresh = false;
    @Inject
    ForumsPresenter mPresenter;
    @Inject
    AndroidNavigation mNavigation;
    protected MenuItem mChangeThemeMenuItem;
    private MenuItem mNotificationMenuItem;
    private boolean mHasNotification = false;

    public static ForumsFragment newInstance(Bundle args) {
        ForumsFragment fragment = new ForumsFragment();
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
        inflater.inflate(R.menu.menu_forum_list, menu);
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
                mAndroidNavigation.navigateToSearchActivity(getActivity());
                return true;
            case R.id.action_open_notification:
                mAndroidNavigation.navigateToNotificationActivity(getActivity());
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
            getPresenter().loadForums(mUserAccountManager.getAuthObject(), false, false);
        }
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
        return R.layout.fragment_forums;
    }

    @Override
    protected ForumsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected ForumListAdapter createAdapter(List<ForumModel> itemList) {
        return new ForumListAdapter(getActivity(), getPicasso(), mItemList);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadForums(authObject, isLoadingMore, true);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            ForumModel item = mCollectionAdapter.getItem(position);
            mNavigation.openActivityForForumByForumId(getActivity(), item.getFid(), item.getName(), item.getDescription());
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof FavoritesChangedEvent)
            mNeedRefresh = true;
        else if(event instanceof NoticeCountEvent) {
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
            checkNewNoticeCount();
        }
    }

    protected void setActivityTitle() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            mainActivity.onSectionAttached(getString(R.string.drawer_item_forum_list));
        }
    }

    @Override
    protected SuperRecyclerView.OnMoreListener getOnMoreListener() {
        return null;
    }

    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.forumlist_detail_column_count));
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return null;
    }

    protected void checkNewNoticeCount() {
        if (isAdded()) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).checkNewNoticeCount();
            }
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
}
