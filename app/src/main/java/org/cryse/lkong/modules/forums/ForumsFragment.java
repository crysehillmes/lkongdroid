package org.cryse.lkong.modules.forums;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.malinskiy.superrecyclerview.OnMoreListener;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionFragment;
import org.cryse.lkong.ui.adapter.ForumListAdapter;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.preference.BooleanPrefs;
import org.cryse.utils.preference.Prefs;

import java.util.List;

import javax.inject.Inject;


public class ForumsFragment extends SimpleCollectionFragment<
        ForumModel,
        ForumListAdapter,
        ForumsPresenter> implements ForumsView<ForumModel> {
    private static final String LOG_TAG = ForumsFragment.class.getName();
    AppNavigation mNavigation = new AppNavigation();

    @Inject
    ForumsPresenter mPresenter;

    private BooleanPrefs mShowInGridPrefs;

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
        setHasOptionsMenu(false);
        mShowInGridPrefs = Prefs.getBooleanPrefs(
                PreferenceConstant.SHARED_PREFERENCE_FORUMS_IN_GRID,
                PreferenceConstant.SHARED_PREFERENCE_FORUMS_IN_GRID_VALUE
        );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        ForumListAdapter adapter =  new ForumListAdapter(this, mATEKey, mItemList);
        adapter.setShowInGrid(mShowInGridPrefs.get());
        return adapter;
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadForums(authObject, isLoadingMore, true);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position;
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemCount()) {
            ForumModel item = mCollectionAdapter.getItem(position);
            mNavigation.openActivityForForumByForumId(getActivity(), item.getFid(), item.getName(), item.getDescription());
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }

    @Override
    protected OnMoreListener getOnMoreListener() {
        return new OnMoreListener() {
            @Override
            public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {
                mCollectionView.setLoadingMore(false);
                mCollectionView.hideMoreProgress();
            }

            @Override
            public void onChangeMoreVisibility(int visibility) {
                mMoreProgressBar.setVisibility(visibility);
            }
        };
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
}
