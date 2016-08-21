package org.cryse.lkong.modules.simplecollection;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.modules.base.BasePresenter;
import org.cryse.lkong.utils.SubscriptionUtils;

import rx.Subscription;

public abstract class SimpleCollectionPresenter<ItemType, ViewType extends SimpleCollectionView<ItemType>> implements BasePresenter<ViewType> {
    protected LKongForumService mLKongForumService;
    protected ViewType mView;
    protected Subscription mLoadDataSubscription;

    public SimpleCollectionPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
        this.mView = null;
    }

    protected abstract void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs);

    @Override
    public void bindView(ViewType view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = null;
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
    }

    public void setLoadingStatus(boolean loadingMore, boolean isLoading) {
        if(mView == null) return;
        if (loadingMore)
            mView.setLoadingMore(isLoading);
        else
            mView.setLoading(isLoading);
    }
}
