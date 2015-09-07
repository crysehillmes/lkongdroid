package org.cryse.lkong.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.model.SearchDataSet;
import org.cryse.lkong.model.SearchGroupItem;
import org.cryse.lkong.model.SearchPostItem;
import org.cryse.lkong.model.SearchUserItem;
import org.cryse.lkong.presenter.SearchPresenter;
import org.cryse.lkong.ui.adapter.SearchResultAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.SearchForumView;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends AbstractThemeableActivity implements SearchForumView {
    private static final String LOG_TAG = SearchActivity.class.getName();
    AppNavigation mNavigation = new AppNavigation();
    SearchView mSearchView = null;
    private String mQueryString = null;
    @Inject
    SearchPresenter mPresenter;
    @Inject
    UserAccountManager mUserAccountManager;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.activity_search_recyclerview)
    SuperRecyclerView mSearchResultRecyclerView;
    SearchResultAdapter mSearchResultAdapter;


    private AtomicBoolean mIsNoMore = new AtomicBoolean(false);
    private AtomicBoolean mIsLoading = new AtomicBoolean(false);
    private AtomicBoolean mIsLoadingMore = new AtomicBoolean(false);
    private AtomicLong mNextTime = new AtomicLong(-1);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);
        setUpToolbar(mToolbar);
        mToolbar.setContentInsetsAbsolute(UIUtils.calculateActionBarSize(this), 0);
        initSearchBox();
    }

    private void initSearchBox() {
        UIUtils.InsetsValue insetsValue = UIUtils.getInsets(this, mSearchResultRecyclerView, false, false, true, getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
        mSearchResultRecyclerView.setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());
        mSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultRecyclerView.setAdapter(mSearchResultAdapter);
        mSearchResultRecyclerView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!mIsNoMore.get() && !mIsLoadingMore.get() && mNextTime.get() > 0) {
                getPresenter().search(mUserAccountManager.getAuthObject(), mNextTime.get(), mQueryString, true);
            } else {
                mSearchResultRecyclerView.setLoadingMore(false);
                mSearchResultRecyclerView.hideMoreProgress();
            }
        });
        mSearchResultAdapter.setOnItemClickListener((view, position, id) -> {
            int headerCount = mSearchResultAdapter.getHeaderViewCount();
            switch (mSearchResultAdapter.getResultType()) {
                case SearchDataSet.TYPE_POST:
                    SearchPostItem postResult = (SearchPostItem) mSearchResultAdapter.getItem(position - headerCount);
                    String idString = postResult.getId();
                    if(idString.startsWith("thread_"))
                        idString = idString.substring(7);
                    if(TextUtils.isDigitsOnly(idString))
                        mNavigation.openActivityForPostListByThreadId(this, Long.valueOf(idString));
                    break;
                case SearchDataSet.TYPE_USER:
                    SearchUserItem userResult = (SearchUserItem) mSearchResultAdapter.getItem(position - headerCount);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mNavigation.openActivityForUserProfile(this, startingLocation, userResult.getUserId());
                    break;
                case SearchDataSet.TYPE_GROUP:
                    SearchGroupItem groupResult = (SearchGroupItem) mSearchResultAdapter.getItem(position - headerCount);
                    mNavigation.openActivityForForumByForumId(this, groupResult.getForumId(), groupResult.getGroupName().toString(), groupResult.getGroupDescription().toString());
                    break;
            }
        });
    }
    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            mSearchView = (SearchView) searchItem.getActionView();
            if (mSearchView != null) {
                mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                mSearchView.setIconified(false);
                mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        // mSearchResultAdapter.setDataSet(null);
                        search(s);
                        mSearchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {

                        return true;
                    }
                });
                mSearchView.setOnCloseListener(() -> false);
                mSearchView.setSubmitButtonEnabled(true);
            }

            if (mSearchView != null && !TextUtils.isEmpty(mQueryString)) {
                mSearchView.setQuery(mQueryString, false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return true;
            case android.R.id.home:
                closeActivityWithTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(String query) {
        mQueryString = query;
        mSearchResultAdapter.setDataSet(null);
        if(mQueryString != null && mQueryString.length() > 0) {
            getPresenter().search(mUserAccountManager.getAuthObject(), 0, query, false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().unbindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
    }

    @Override
    public void onSearchDone(SearchDataSet dataSet, boolean isLoadingMore) {
        if(isLoadingMore) {
            if (dataSet.getSearchResultItems() == null || dataSet.getSearchResultItems().size() == 0) mIsNoMore.set(true);
            mSearchResultAdapter.appendDataSet(dataSet);
        }
        else {
            mIsNoMore.set(false);
            mSearchResultAdapter.setDataSet(dataSet);
        }
        mNextTime.set(dataSet.getNextTime());
    }

    @Override
    public void onSearchFailed(int errorCode, Throwable throwable) {

    }

    protected SearchPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setLoading(Boolean value) {
        mIsLoading.set(value);
        mSearchResultRecyclerView.getSwipeToRefresh().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return mIsLoading.get();
    }

    @Override
    public void setLoadingMore(boolean value) {
        mIsLoadingMore.set(value);
        mSearchResultRecyclerView.setLoadingMore(value);
        if(value)
            mSearchResultRecyclerView.showMoreProgress();
        else
            mSearchResultRecyclerView.hideMoreProgress();
    }

    @Override
    public boolean isLoadingMore() {
        return mIsLoadingMore.get();
    }
}
