package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.cryse.lkong.R;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.model.SearchDataSet;
import org.cryse.lkong.model.SearchGroupItem;
import org.cryse.lkong.model.SearchPostItem;
import org.cryse.lkong.model.SearchUserItem;
import org.cryse.lkong.presenter.SearchPresenter;
import org.cryse.lkong.ui.adapter.SearchResultAdapter;
import org.cryse.lkong.ui.common.AbstractFragment;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.view.SearchForumView;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;
import org.cryse.utils.preference.StringPrefs;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;

public class SearchFragment extends AbstractFragment implements SearchForumView {
    private static final String LOG_TAG = SearchFragment.class.getName();
    AppNavigation mNavigation = new AppNavigation();

    private String mQueryString = null;
    @Inject
    SearchPresenter mPresenter;
    @Inject
    UserAccountManager mUserAccountManager;

    StringPrefs mAvatarDownloadPolicy;

    @Bind(R.id.activity_search_recyclerview)
    SuperRecyclerView mSearchResultRecyclerView;
    SearchResultAdapter mSearchResultAdapter;
    private OnSearchResultListener mOnSearchResultListener;


    private AtomicBoolean mIsNoMore = new AtomicBoolean(false);
    private AtomicBoolean mIsLoading = new AtomicBoolean(false);
    private AtomicBoolean mIsLoadingMore = new AtomicBoolean(false);
    private AtomicLong mNextTime = new AtomicLong(-1);

    public static SearchFragment newInstance(String searchString, OnSearchResultListener onSearchResultListener) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(DataContract.BUNDLE_SEARCH_QUERY, searchString);
        searchFragment.setArguments(args);
        searchFragment.mOnSearchResultListener = onSearchResultListener;
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        mAvatarDownloadPolicy = Prefs.getStringPrefs(PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY,
                PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY_VALUE);
        // UIUtils.setInsets(this, mCollectionView, false, false, true, Build.VERSION.SDK_INT < 21);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(DataContract.BUNDLE_SEARCH_QUERY)) {
                mQueryString = args.getString(DataContract.BUNDLE_SEARCH_QUERY);
            }
        }
        /*if(TextUtils.isEmpty(mSearchString)) {
            throw new IllegalArgumentException("Need SEARCH_STRING param.");
        }*/
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_search, null);
        ButterKnife.bind(this, contentView);
        initRecyclerView();
        return contentView;
    }

    private void initRecyclerView() {
        mSearchResultAdapter = new SearchResultAdapter(getContext(), mATEKey, Integer.valueOf(mAvatarDownloadPolicy.get()));
        mSearchResultRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mSearchResultRecyclerView.setAdapter(mSearchResultAdapter);
        mSearchResultRecyclerView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!mIsNoMore.get() && !mIsLoadingMore.get() && mNextTime.get() > 0) {
                mPresenter.search(mUserAccountManager.getAuthObject(), mNextTime.get(), mQueryString, true);
            } else {
                mSearchResultRecyclerView.setLoadingMore(false);
                mSearchResultRecyclerView.hideMoreProgress();
            }
        });
        mSearchResultAdapter.setOnItemClickListener((view, position, id) -> {
            switch (mSearchResultAdapter.getResultType()) {
                case SearchDataSet.TYPE_POST:
                    SearchPostItem postResult = (SearchPostItem) mSearchResultAdapter.getItem(position);
                    String idString = postResult.getId();
                    if (idString.startsWith("thread_"))
                        idString = idString.substring(7);
                    if (TextUtils.isDigitsOnly(idString))
                        mNavigation.openActivityForPostListByThreadId(getContext(), Long.valueOf(idString));
                    break;
                case SearchDataSet.TYPE_USER:
                    SearchUserItem userResult = (SearchUserItem) mSearchResultAdapter.getItem(position);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mNavigation.openActivityForUserProfile(getActivity(), startingLocation, userResult.getUserId());
                    break;
                case SearchDataSet.TYPE_GROUP:
                    SearchGroupItem groupResult = (SearchGroupItem) mSearchResultAdapter.getItem(position);
                    mNavigation.openActivityForForumByForumId(getContext(), groupResult.getForumId(), groupResult.getGroupName().toString(), groupResult.getGroupDescription().toString());
                    break;
            }
        });
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getContext()).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
    }

    private void search() {
        mSearchResultAdapter.clear();
        mNextTime.set(0);
        if (!TextUtils.isEmpty(mQueryString)) {
            mPresenter.search(mUserAccountManager.getAuthObject(), 0, mQueryString, false);
        }
    }

    public void search(String query) {
        if (TextUtils.isEmpty(query)) {
            throw new IllegalArgumentException("Param query should not be empty.");
        } else {
            this.mQueryString = query;
            search();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.bindView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unbindView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
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

    @Override
    public void setLoading(Boolean value) {
        mIsLoading.set(value);
        if (mIsLoading.get()) {
            mSearchResultRecyclerView.showProgress();
        } else {
            mSearchResultRecyclerView.hideProgress();
            mSearchResultRecyclerView.showRecycler();
        }
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

    public interface OnSearchResultListener {
        void onItemClick(Object item, int position);
    }
}
