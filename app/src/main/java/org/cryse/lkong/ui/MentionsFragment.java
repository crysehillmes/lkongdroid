package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bumptech.glide.Glide;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.presenter.TimelinePresenter;
import org.cryse.lkong.ui.adapter.TimelineAdapter;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;

import java.util.List;

import javax.inject.Inject;

public class MentionsFragment extends SimpleCollectionFragment<
        TimelineModel,
        TimelineAdapter,
        TimelinePresenter> {
    private static final String LOG_TAG = MentionsFragment.class.getName();
    private static final String LOAD_IMAGE_TASK_TAG = "mentions_load_image_tag";

    @Inject
    TimelinePresenter mPresenter;

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
        TimelineAdapter adapter = new TimelineAdapter(getActivity(), mItemList, LOAD_IMAGE_TASK_TAG);
        adapter.setOnTimelineModelItemClickListener(new TimelineAdapter.OnTimelineModelItemClickListener() {
            @Override
            public void onProfileAreaClick(View view, int position, long uid) {
                int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
                if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    TimelineModel model = mCollectionAdapter.getItem(itemIndex);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mAndroidNavigation.openActivityForUserProfile(getActivity(), startingLocation, model.getUserId());
                }
            }

            @Override
            public void onItemTimelineClick(View view, int adapterPosition) {
                int itemIndex = adapterPosition - mCollectionAdapter.getHeaderViewCount();
                if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    TimelineModel model = mCollectionAdapter.getItem(itemIndex);
                    mAndroidNavigation.openActivityForPostListByTimelineModel(getActivity(), model);
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
