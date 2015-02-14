package org.cryse.lkong.ui;

import android.os.Bundle;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.presenter.TimelinePresenter;
import org.cryse.lkong.ui.adapter.TimelineAdapter;
import org.cryse.lkong.utils.LKAuthObject;

import java.util.List;

import javax.inject.Inject;

public class MentionsFragment extends SimpleCollectionFragment<
        TimelineModel,
        TimelineAdapter,
        TimelinePresenter> {
    private static final String LOG_TAG = MentionsFragment.class.getName();

    @Inject
    TimelinePresenter mPresenter;

    public static MentionsFragment newInstance(Bundle args) {
        MentionsFragment fragment = new MentionsFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
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
        return new TimelineAdapter(getActivity(), mItemList);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadMentions(authObject, start, isLoadingMore);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            TimelineModel item = mCollectionAdapter.getItem(position);
            if(item.getId().startsWith("thread_")) {
                mAndroidNavigation.openActivityForPostListByThreadId(getActivity(), Long.valueOf(item.getId().substring(7)));
            } else if(item.getId().startsWith("post_")) {
                mAndroidNavigation.openActivityForPostListByPostId(getActivity(), Long.valueOf(item.getId().substring(5)));
            } else {
                mAndroidNavigation.openActivityForPostListByThreadId(getActivity(), item.getTid());
            }
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {

    }
}
