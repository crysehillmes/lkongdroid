package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.data.model.PinnedForumEntity;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.presenter.PinnedForumsPresenter;
import org.cryse.lkong.ui.adapter.PinnedForumsAdapter;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.widget.recyclerview.SuperRecyclerView;


import java.util.List;

import javax.inject.Inject;


public class PinnedForumsFragment extends SimpleCollectionFragment<
        PinnedForumEntity,
        PinnedForumsAdapter,
        PinnedForumsPresenter> {
    private static final String LOG_TAG = PinnedForumsFragment.class.getName();

    @Inject
    PinnedForumsPresenter mPresenter;

    @Inject
    AndroidNavigation mNavigation;

    public static PinnedForumsFragment newInstance(Bundle args) {
        PinnedForumsFragment fragment = new PinnedForumsFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(null, 0, false);
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
    protected PinnedForumsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected PinnedForumsAdapter createAdapter(List<PinnedForumEntity> itemList) {
        return new PinnedForumsAdapter(getActivity(), getPicasso(), mItemList);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadPinnedForums(mUserAccountManager.getCurrentUserId());
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            PinnedForumEntity item = mCollectionAdapter.getItem(position);
            mNavigation.openActivityForForumByForumId(getActivity(), item.getForumId(), item.getForumName(), "");
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }

    @Override
    protected SuperRecyclerView.OnMoreListener getOnMoreListener() {
        return null;
    }

    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.forumlist_column_count));
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return null;
    }
}
