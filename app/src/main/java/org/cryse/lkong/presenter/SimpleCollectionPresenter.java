package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.SimpleCollectionView;

import rx.Subscription;

public abstract class SimpleCollectionPresenter<ItemType> implements BasePresenter<SimpleCollectionView<ItemType>> {
    protected LKongForumService mLKongForumService;
    protected SimpleCollectionView mView;
    protected Subscription mLoadDataSubscription;

    public SimpleCollectionPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
        this.mView = null;
    }

    protected abstract void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs);

    @Override
    public void bindView(SimpleCollectionView view) {
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
