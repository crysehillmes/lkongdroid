package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.ThreadListView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FavoritesPresenter implements BasePresenter<ThreadListView> {
    public static final String LOG_TAG = FavoritesPresenter.class.getName();
    LKongForumService mLKongForumService;
    ThreadListView mView;
    Subscription mLoadFavoritesSubscription;

    @Inject
    public FavoritesPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
        this.mView = new EmptyTheadListView();
    }

    public void loadFavorites(LKAuthObject authObject, boolean loadingMore) {
        loadFavorites(authObject, -1, loadingMore);
    }

    public void loadFavorites(LKAuthObject authObject, long start, boolean loadingMore) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadFavoritesSubscription);
        setLoadingStatus(loadingMore, true);
        mLoadFavoritesSubscription = mLKongForumService.getFavorite(authObject, start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("ThreadListPresenter::loadThreadList() onNext().", LOG_TAG);
                            mView.showThreadList(result, loadingMore);
                        },
                        error -> {
                            Timber.e(error, "ThreadListPresenter::loadThreadList() onError().", LOG_TAG);
                            setLoadingStatus(loadingMore, false);
                        },
                        () -> {
                            Timber.d("ThreadListPresenter::loadThreadList() onComplete().", LOG_TAG);
                            setLoadingStatus(loadingMore, false);
                        }
                );
    }


    @Override
    public void bindView(ThreadListView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptyTheadListView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mLoadFavoritesSubscription);
    }

    private void setLoadingStatus(boolean loadingMore, boolean isLoading) {
        if(loadingMore)
            mView.setLoadingMore(isLoading);
        else
            mView.setLoading(isLoading);
    }

    private class EmptyTheadListView implements ThreadListView {

        @Override
        public void showThreadList(List<ThreadModel> threadList, boolean isLoadMore) {

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
