package org.cryse.lkong.modules.notice;

import android.os.Bundle;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.NoticeRateModel;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionFragment;
import org.cryse.lkong.ui.adapter.NoticeRateCollectionAdapter;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;

import java.util.List;

import javax.inject.Inject;

public class NoticeRateFragment extends SimpleCollectionFragment<
        NoticeRateModel,
        NoticeRateCollectionAdapter,
        NoticeRatePresenter> {
    private static final String LOG_TAG = NoticeRateFragment.class.getName();

    @Inject
    NoticeRatePresenter mPresenter;

    public static NoticeRateFragment newInstance(Bundle args) {
        NoticeRateFragment fragment = new NoticeRateFragment();
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
    protected NoticeRatePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected NoticeRateCollectionAdapter createAdapter(List<NoticeRateModel> itemList) {
        return new NoticeRateCollectionAdapter(getActivity(), mATEKey, mItemList);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadNotice(authObject, start, isLoadingMore);
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return null;
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position;
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemCount()) {
            NoticeRateModel noticeModel = mCollectionAdapter.getItem(itemIndex);
            // TODO: Goto thread by post.
            if(noticeModel.getPid() > 0) {
                mNavigation.openActivityForPostListByPostId(getContext(), noticeModel.getPid());
            }
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }
}
