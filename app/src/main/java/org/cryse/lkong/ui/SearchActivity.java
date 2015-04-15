package org.cryse.lkong.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.model.SearchDataSet;
import org.cryse.lkong.presenter.SearchPresenter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.SearchForumView;

import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SearchActivity extends AbstractThemeableActivity implements SearchForumView {
    private static final String LOG_TAG = SearchActivity.class.getName();
    SearchView mSearchView = null;
    private String mQueryString = null;
    Picasso mPicasso;
    @Inject
    SearchPresenter mPresenter;
    @Inject
    UserAccountManager mUserAccountManager;
    @InjectView(R.id.activity_search_recyclerview)
    RecyclerView mSearchResultRecyclerView;
    SearchResultAdapter mSearchResultAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        int actionBarSize = UIUtils.calculateActionBarSize(this);
        getToolbar().setContentInsetsAbsolute(actionBarSize, 0);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);
        initSearchBox();
    }

    private void initSearchBox() {
        UIUtils.InsetsValue insetsValue = UIUtils.getInsets(this, mSearchResultRecyclerView, false, false, true, getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
        mSearchResultRecyclerView.setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());

        mSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultAdapter = new SearchResultAdapter(mPicasso);
        mSearchResultRecyclerView.setAdapter(mSearchResultAdapter);
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
                        mSearchResultAdapter.setDataSet(null);
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
            getPresenter().search(mUserAccountManager.getAuthObject(), query);
            /*mListView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().searchNovel(mQueryString, 0, false);*/
        } else {
            mSearchResultAdapter.setDataSet(null);
            /*mSearchNovelList.clear();*/
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
        mPicasso.shutdown();
    }

    @Override
    public void setLoading(Boolean value) {

    }

    @Override
    public Boolean isLoading() {
        return null;
    }

    @Override
    public void showToast(int text_value, int toastType) {

    }

    @Override
    public void onSearchDone(SearchDataSet dataSet) {
        mSearchResultAdapter.setDataSet(dataSet);
    }

    @Override
    public void onSearchFailed(int errorCode, Throwable throwable) {

    }

    protected static class SearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Picasso mPicasso;
        private SearchDataSet mDataSet;
        private CircleTransform mCircleTransform;

        public SearchResultAdapter(Picasso picasso) {
            this.mPicasso = picasso;
            this.mCircleTransform = new CircleTransform();
        }

        public void setDataSet(SearchDataSet dataSet) {
            this.mDataSet = dataSet;
            this.notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {// create a new view
            View view;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case SearchDataSet.TYPE_POST:
                    view = inflater.inflate(R.layout.recyclerview_item_search_post, parent, false);
                    return new SearchPostViewHolder(view);
                case SearchDataSet.TYPE_USER:
                    view = inflater.inflate(R.layout.recyclerview_item_search_user, parent, false);
                    return new SearchUserViewHolder(view);
                case SearchDataSet.TYPE_GROUP:
                    view = inflater.inflate(R.layout.recyclerview_item_search_group, parent, false);
                    return new SearchGroupViewHolder(view);
                default:
                    throw new IllegalArgumentException("Unknown viewType.");
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (mDataSet.getDataType()) {
                case SearchDataSet.TYPE_POST:
                    bindPostResult((SearchPostViewHolder)holder, position);
                    break;
                case SearchDataSet.TYPE_USER:
                    bindUserResult((SearchUserViewHolder) holder, position);
                    break;
                case SearchDataSet.TYPE_GROUP:
                    bindGroupResult((SearchGroupViewHolder) holder, position);
                    break;
            }
        }

        private void bindPostResult(SearchPostViewHolder viewHolder, int position) {
            viewHolder.titleTextView.setText(mDataSet.getSearchPostItems().get(position).getSubject());
            viewHolder.secondaryTextView.setText(mDataSet.getSearchPostItems().get(position).getUserName());
        }

        private void bindUserResult(SearchUserViewHolder viewHolder, int position) {
            viewHolder.nameTextView.setText(mDataSet.getSearchUserItems().get(position).getUserName());
            viewHolder.signTextView.setText(mDataSet.getSearchUserItems().get(position).getSignHtml());
            mPicasso.load(mDataSet.getSearchUserItems().get(position).getAvatarUrl())
                    .placeholder(R.drawable.ic_placeholder_avatar)
                    .error(R.drawable.ic_placeholder_avatar)
                    .fit()
                    .centerCrop()
                    .transform(mCircleTransform)
                    .into(viewHolder.avatarImageView);
        }

        private void bindGroupResult(SearchGroupViewHolder viewHolder, int position) {
            viewHolder.nameTextView.setText(mDataSet.getSearchGroupItems().get(position).getGroupName());
            viewHolder.descriptionTextView.setText(mDataSet.getSearchGroupItems().get(position).getGroupDescription());
            mPicasso.load(mDataSet.getSearchGroupItems().get(position).getIconUrl())
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_placeholder)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.iconImageView);
        }

        @Override
        public int getItemViewType(int position) {
            return mDataSet.getDataType();
        }

        @Override
        public int getItemCount() {
            if(mDataSet == null) return 0;
            switch (mDataSet.getDataType()) {
                case SearchDataSet.TYPE_POST:
                    return mDataSet.getSearchPostItems().size();
                case SearchDataSet.TYPE_USER:
                    return mDataSet.getSearchUserItems().size();
                case SearchDataSet.TYPE_GROUP:
                    return mDataSet.getSearchGroupItems().size();
                default:
                    return 0;
            }
        }

        protected static class SearchPostViewHolder extends RecyclerView.ViewHolder {
            @InjectView(R.id.recyclerview_item_search_post_title)
            TextView titleTextView;
            @InjectView(R.id.recyclerview_item_search_post_secondary)
            TextView secondaryTextView;
            public SearchPostViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }

        protected static class SearchUserViewHolder extends RecyclerView.ViewHolder {
            @InjectView(R.id.recyclerview_item_search_user_icon)
            ImageView avatarImageView;
            @InjectView(R.id.recyclerview_item_search_user_name)
            TextView nameTextView;
            @InjectView(R.id.recyclerview_item_search_user_sign)
            TextView signTextView;
            public SearchUserViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }

        protected static class SearchGroupViewHolder extends RecyclerView.ViewHolder {
            @InjectView(R.id.recyclerview_item_search_group_icon)
            ImageView iconImageView;
            @InjectView(R.id.recyclerview_item_search_group_name)
            TextView nameTextView;
            @InjectView(R.id.recyclerview_item_search_group_description)
            TextView descriptionTextView;
            public SearchGroupViewHolder(View itemView) {
                super(itemView);
                ButterKnife.inject(this, itemView);
            }
        }
    }

    protected SearchPresenter getPresenter() {
        return mPresenter;
    }
}
