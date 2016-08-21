package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionFragment;
import org.cryse.lkong.modules.timeline.TimelinePresenter;
import org.cryse.lkong.ui.adapter.TimelineAdapter;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;
import org.cryse.utils.preference.StringPrefs;

import java.util.List;

import javax.inject.Inject;

public class MentionsFragment extends SimpleCollectionFragment<
        TimelineModel,
        TimelineAdapter,
        TimelinePresenter> {
    private static final String LOG_TAG = MentionsFragment.class.getName();

    @Inject
    TimelinePresenter mPresenter;
    StringPrefs mAvatarDownloadPolicy;

    public static MentionsFragment newInstance(Bundle args) {
        MentionsFragment fragment = new MentionsFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        mAvatarDownloadPolicy = Prefs.getStringPrefs(PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY,
                PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY_VALUE);
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
    protected TimelinePresenter getPresenter() {
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
                int itemIndex = position;
                if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemCount()) {
                    TimelineModel model = mCollectionAdapter.getItem(itemIndex);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mNavigation.openActivityForUserProfile(getActivity(), startingLocation, model.getUserId());
                }
            }

            @Override
            public void onItemTimelineClick(View view, int adapterPosition) {
                if(adapterPosition >= 0 && adapterPosition < mCollectionAdapter.getItemCount()) {
                    TimelineModel model = mCollectionAdapter.getItem(adapterPosition);
                    mNavigation.openActivityForPostListByTimelineModel(getActivity(), model);
                }
            }
        });
        return adapter;
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadMentions(authObject, start, isLoadingMore);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
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
