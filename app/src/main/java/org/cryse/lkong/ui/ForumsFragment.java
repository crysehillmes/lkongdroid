package org.cryse.lkong.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.List;

import javax.inject.Inject;


public class ForumsFragment extends SimpleCollectionFragment<
        ForumModel,
        ForumListAdapter,
        ForumsPresenter> {
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
            getPresenter().loadForums(mUserAccountManager.getAuthObject(), false);
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
        return R.layout.fragment_simple_collection;
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
        getPresenter().loadForums(authObject, isLoadingMore);
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
            NoticeCountModel model = ((NoticeCountEvent) event).getNoticeCount();
            if (model.getMentionNotice() != 0
                    || model.getNotice() != 0
                    || model.getRateNotice() != 0
                    /*|| model.getPrivateMessageNotice() != 0
                    || model.getFansNotice() != 0*/
                    ) {
                mHasNotification = true;
            } else {
                mHasNotification = false;
            }
            if(getActivity() != null)
                getActivity().invalidateOptionsMenu();
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
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.forumlist_column_count));
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return UIUtils.getInsets(getActivity(), mCollectionView, false, false, true, getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
    }

    protected void checkNewNoticeCount() {
        if (isAdded()) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).checkNewNoticeCount();
            }
        }
    }
}
