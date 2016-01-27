package org.cryse.lkong.ui;

import android.os.Bundle;
import android.view.View;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.presenter.NoticePrivateChatsPresenter;
import org.cryse.lkong.ui.adapter.NoticePrivateChatsCollectionAdapter;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;
import org.cryse.utils.preference.StringPrefs;

import java.util.List;

import javax.inject.Inject;

public class NoticePrivateChatsFragment extends SimpleCollectionFragment<
        PrivateChatModel,
        NoticePrivateChatsCollectionAdapter,
        NoticePrivateChatsPresenter> {
    private static final String LOG_TAG = NoticePrivateChatsFragment.class.getName();

    @Inject
    NoticePrivateChatsPresenter mPresenter;
    StringPrefs mAvatarDownloadPolicy;

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
    protected NoticePrivateChatsPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected NoticePrivateChatsCollectionAdapter createAdapter(List<PrivateChatModel> itemList) {
        return new NoticePrivateChatsCollectionAdapter(getActivity(), mATEKey, mItemList, Integer.valueOf(mAvatarDownloadPolicy.get()));
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
            PrivateChatModel model = mCollectionAdapter.getItem(itemIndex);
            mNavigation.openActivityForPrivateMessage(
                    getActivity(),
                    model.getTargetUserId(),
                    model.getTargetUserName()
                    );
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }
}
