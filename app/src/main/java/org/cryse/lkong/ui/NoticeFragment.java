package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.NoticeModel;
import org.cryse.lkong.presenter.NoticePresenter;
import org.cryse.lkong.ui.adapter.NoticeCollectionAdapter;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.LKAuthObject;

import java.util.List;

import javax.inject.Inject;

public class NoticeFragment extends SimpleCollectionFragment<
        NoticeModel,
        NoticeCollectionAdapter,
        NoticePresenter> {
    private static final String LOG_TAG = NoticeFragment.class.getName();

    @Inject
    NoticePresenter mPresenter;

    public static NoticeFragment newInstance(Bundle args) {
        NoticeFragment fragment = new NoticeFragment();
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
    protected NoticePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected NoticeCollectionAdapter createAdapter(List<NoticeModel> itemList) {
        return new NoticeCollectionAdapter(getActivity(), mItemList);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadNotice(authObject, start, isLoadingMore);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            NoticeModel noticeModel = mCollectionAdapter.getItem(itemIndex);
            if(noticeModel.getThreadId() > 0) {
                Intent intent = new Intent(getActivity(), PostListActivity.class);
                intent.putExtra(DataContract.BUNDLE_THREAD_ID, noticeModel.getThreadId());
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }
}
