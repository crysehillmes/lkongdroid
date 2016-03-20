package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.presenter.UserProfileTimelinePresenter;
import org.cryse.lkong.ui.adapter.TimelineAdapter;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;
import org.cryse.utils.preference.StringPrefs;
import org.cryse.widget.recyclerview.DividerItemDecoration;

import java.util.List;

import javax.inject.Inject;

public class UserProfileTimelineFragment extends SimpleCollectionFragment<
        TimelineModel,
        TimelineAdapter,
        UserProfileTimelinePresenter> {
    private static final String LOG_TAG = UserProfileTimelineFragment.class.getName();
    private static final String KEY_UID = "key_args_uid";
    private static final String KEY_USERNAME= "key_args_username";
    private static final String LOAD_IMAGE_TASK_TAG = "timeline_load_image_tag";
    private long mUid;
    private String mUserName;

    @Inject
    UserProfileTimelinePresenter mPresenter;
    StringPrefs mAvatarDownloadPolicy;

    public static UserProfileTimelineFragment newInstance(long uid, String userName) {
        Bundle args = new Bundle();
        args.putLong(KEY_UID, uid);
        args.putString(KEY_USERNAME, userName);
        UserProfileTimelineFragment fragment = new UserProfileTimelineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAvatarDownloadPolicy = Prefs.getStringPrefs(PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY,
                PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY_VALUE);
        if(getArguments() != null && getArguments().containsKey(KEY_UID)) {
            mUid = getArguments().getLong(KEY_UID);
            mUserName = getArguments().getString(KEY_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = super.onCreateView(inflater, container, savedInstanceState);
        setUpToolbar(mToolbar);
        return contentView;
    }

    protected void setUpToolbar(Toolbar toolbar) {
        getAppCompatActivity().setSupportActionBar(toolbar);
        ActionBar actionBar = getAppCompatActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getSwipeBackActivity().onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void restoreFromState(Bundle savedInstanceState) {
        if(savedInstanceState.containsKey(KEY_UID)
                && savedInstanceState.containsKey(KEY_USERNAME)
                ) {
            this.mUid = savedInstanceState.getLong(KEY_UID);
            this.mUserName = savedInstanceState.getString(KEY_USERNAME);
            setTitle();
        }
    }

    private void setTitle() {
        getAppCompatActivity().setTitle(getString(R.string.format_all_activities_of, mUserName));
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_simple_collection_with_toolbar;
    }

    @Override
    protected UserProfileTimelinePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected TimelineAdapter createAdapter(List<TimelineModel> itemList) {
        TimelineAdapter adapter = new TimelineAdapter(
                getActivity(),
                mItemList,
                Integer.valueOf(mAvatarDownloadPolicy.get()),
                mATEKey
        );
        adapter.setOnTimelineModelItemClickListener(new TimelineAdapter.OnTimelineModelItemClickListener() {
            @Override
            public void onProfileAreaClick(View view, int position, long uid) {
                if (position >= 0 && position < mCollectionAdapter.getItemCount()) {
                    TimelineModel model = mCollectionAdapter.getItem(position);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mNavigation.openActivityForUserProfile(getActivity(), startingLocation, model.getUserId());
                }
            }

            @Override
            public void onItemTimelineClick(View view, int adapterPosition) {
                if (adapterPosition >= 0 && adapterPosition < mCollectionAdapter.getItemCount()) {
                    TimelineModel model = mCollectionAdapter.getItem(adapterPosition);
                    mNavigation.openActivityForPostListByTimelineModel(getActivity(), model);
                }
            }
        });
        return adapter;
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadUserTimeline(authObject, mUid, start, isLoadingMore);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
    }

    @Override
    protected SwipeRefreshLayout.OnRefreshListener getRefreshListener() {
        return null;
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return null;
    }

    @Override
    protected void onCollectionViewInitComplete() {
        super.onCollectionViewInitComplete();
        mCollectionView.addItemDecoration(new DividerItemDecoration(getActivity()));
        mCollectionView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(getThemedActivity() != null && !getThemedActivity().isActivityDestroyed())
                        Glide.with(getActivity()).resumeRequests();
                } else {
                    Glide.with(getActivity()).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
}