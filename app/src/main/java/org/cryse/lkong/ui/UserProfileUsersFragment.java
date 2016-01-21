package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.lkong.model.SearchUserItem;
import org.cryse.lkong.presenter.UserProfileUsersPresenter;
import org.cryse.lkong.ui.adapter.UsersAdapter;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.utils.preference.Prefs;
import org.cryse.utils.preference.StringPrefs;

import java.util.List;

import javax.inject.Inject;

public class UserProfileUsersFragment extends SimpleCollectionFragment<
        SearchUserItem,
        UsersAdapter,
        UserProfileUsersPresenter> {
    private static final String LOG_TAG = UserProfileUsersFragment.class.getName();
    private static final String KEY_UID = "key_args_uid";
    private static final String KEY_IS_FOLLOWER = "key_args_is_follower";
    private long mUid;
    private boolean mIsFollower;

    @Inject
    UserProfileUsersPresenter mPresenter;
    StringPrefs mAvatarDownloadPolicy;

    public static UserProfileUsersFragment newInstance(long uid, boolean follower) {
        Bundle args = new Bundle();
        args.putLong(KEY_UID, uid);
        args.putBoolean(KEY_IS_FOLLOWER, follower);
        UserProfileUsersFragment fragment = new UserProfileUsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        mAvatarDownloadPolicy = Prefs.getStringPrefs(PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY,
                PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY_VALUE);
        Bundle args = getArguments();
        if(args != null && args.containsKey(KEY_UID) && args.containsKey(KEY_IS_FOLLOWER)) {
            mUid = args.getLong(KEY_UID);
            mIsFollower = args.getBoolean(KEY_IS_FOLLOWER);
        }
        setHasOptionsMenu(false);
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
        return R.layout.fragment_simple_collection;
    }

    @Override
    protected UserProfileUsersPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected UsersAdapter createAdapter(List<SearchUserItem> itemList) {
        UsersAdapter adapter = new UsersAdapter(getActivity(), mATEKey, mItemList, Integer.valueOf(mAvatarDownloadPolicy.get()));
        adapter.setOnUserItemClickListener(new UsersAdapter.OnUserItemClickListener() {
            @Override
            public void onProfileAreaClick(View view, int position, long uid) {
                int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
                if (itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    SearchUserItem model = mCollectionAdapter.getItem(itemIndex);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mNavigation.openActivityForUserProfile(getActivity(), startingLocation, model.getUserId());
                }
            }
        });
        return adapter;
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadUserFollowerOrFollowing(authObject, mUid, start, mIsFollower, isLoadingMore);
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
        mCollectionView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!getThemedActivity().isActivityDestroyed())
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