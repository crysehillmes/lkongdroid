package org.cryse.lkong.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.FavoritesChangedEvent;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.presenter.FavoritesPresenter;
import org.cryse.lkong.ui.adapter.ThreadListAdapter;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;

import java.util.List;

import javax.inject.Inject;

public class FavoritesFragment extends SimpleCollectionFragment<
        ThreadModel,
        ThreadListAdapter,
        FavoritesPresenter> {
    private static final String LOG_TAG = FavoritesFragment.class.getName();

    boolean mNeedRefresh = false;
    @Inject
    FavoritesPresenter mPresenter;

    public static FavoritesFragment newInstance(Bundle args) {
        FavoritesFragment fragment = new FavoritesFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_notification:
                mAndroidNavigation.navigateToNotificationActivity(getActivity());
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
        if(mNeedRefresh) {
            mNeedRefresh = false;
            getPresenter().loadFavorites(mUserAccountManager.getAuthObject(), false);
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
    protected FavoritesPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected ThreadListAdapter createAdapter(List<ThreadModel> itemList) {
        return new ThreadListAdapter(getActivity(), mItemList);
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
            mAndroidNavigation.openActivityForPostListByThreadId(getActivity(), tid);
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        if(event instanceof FavoritesChangedEvent)
            mNeedRefresh = true;
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
        return UIUtils.getInsets(getActivity(), mCollectionView, true, getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
    }
}
