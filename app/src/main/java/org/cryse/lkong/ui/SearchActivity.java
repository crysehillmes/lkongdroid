package org.cryse.lkong.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.UIUtils;

import butterknife.ButterKnife;

public class SearchActivity extends AbstractThemeableActivity {
    private static final String LOG_TAG = SearchActivity.class.getName();
    SearchView mSearchView = null;
    private String mQueryString = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        getToolbar().setContentInsetsAbsolute(actionBarSize, 0);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);
        initSearchBox();
    }

    private void initSearchBox() {

    }
    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentActivityExit(this, LOG_TAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView view = (SearchView) searchItem.getActionView();
            mSearchView = view;
            if (view == null) {
                // LOGW(TAG, "Could not set up search view, view is null.");
            } else {
                view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                view.setIconified(false);
                view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        search(s);
                        Toast.makeText(SearchActivity.this, s, Toast.LENGTH_SHORT).show();
                        view.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {

                        return true;
                    }
                });
                view.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        return false;
                    }
                });
                view.setSubmitButtonEnabled(true);
            }

            if (view != null && !TextUtils.isEmpty(mQueryString)) {
                view.setQuery(mQueryString, false);
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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(String query) {
        mQueryString = query;
        setTitle(mQueryString);
        if(mQueryString != null && mQueryString.length() > 0) {
            /*mListView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().searchNovel(mQueryString, 0, false);*/
        } else {
            /*mSearchNovelList.clear();*/
        }
    }
}
