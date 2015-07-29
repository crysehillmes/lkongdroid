package org.cryse.lkong.ui;

import android.os.Bundle;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.presenter.NoticePrivateChatsPresenter;
import org.cryse.lkong.ui.adapter.NoticePrivateChatsCollectionAdapter;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;

import java.util.List;

import javax.inject.Inject;

public class NoticePrivateChatsFragment extends SimpleCollectionFragment<
        PrivateChatModel,
        NoticePrivateChatsCollectionAdapter,
        NoticePrivateChatsPresenter> {
    private static final String LOG_TAG = NoticePrivateChatsFragment.class.getName();

    @Inject
    NoticePrivateChatsPresenter mPresenter;

    public static NoticePrivateChatsFragment newInstance(Bundle args) {
        NoticePrivateChatsFragment fragment = new NoticePrivateChatsFragment();
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
    protected NoticePrivateChatsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected NoticePrivateChatsCollectionAdapter createAdapter(List<PrivateChatModel> itemList) {
        return new NoticePrivateChatsCollectionAdapter(getActivity(), getPicasso(), mItemList);
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
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            PrivateChatModel privateChatModel = mCollectionAdapter.getItem(itemIndex);
            // TODO: Goto thread by post.
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }
}
