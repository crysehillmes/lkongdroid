package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.SimpleCollectionView;

import java.util.List;

import rx.Subscription;

public abstract class SimpleCollectionPresenter<ItemType> implements BasePresenter<SimpleCollectionView<ItemType>> {
    protected LKongForumService mLKongForumService;
    protected SimpleCollectionView mView;
    protected Subscription mLoadDataSubscription;

    public SimpleCollectionPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
        this.mView = new EmptySimpleCollectionView();
    }

    protected abstract void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs);

    @Override
    public void bindView(SimpleCollectionView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptySimpleCollectionView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
    }

    public void setLoadingStatus(boolean loadingMore, boolean isLoading) {
        if (loadingMore)
            mView.setLoadingMore(isLoading);
        else
            mView.setLoading(isLoading);
    }

    private class EmptySimpleCollectionView implements SimpleCollectionView<ItemType> {


        @Override
        public void showSimpleData(List<ItemType> items, boolean loadMore) {

        }

        @Override
        public boolean isLoadingMore() {
            return false;
        }

        @Override
        public void setLoadingMore(boolean value) {

        }

        @Override
        public void setLoading(Boolean value) {

        }

        @Override
        public Boolean isLoading() {
            return null;
        }

        @Override
        public void showToast(int text_value, int toastType) {

        }
    }
}
