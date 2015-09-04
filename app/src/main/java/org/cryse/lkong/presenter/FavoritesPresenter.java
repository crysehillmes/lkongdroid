package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.FavoritesView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class FavoritesPresenter extends SimpleCollectionPresenter<ThreadModel, FavoritesView<ThreadModel>> {
    private static final String LOG_TAG = FavoritesPresenter.class.getName();

    Subscription mCheckNoticeCountSubscription;
    @Inject
    public FavoritesPresenter(LKongForumService forumService) {
        super(forumService);
    }

    public void loadFavorites(LKAuthObject authObject, boolean isLoadingMore) {
        loadFavorites(authObject, -1, isLoadingMore);
    }

    public void loadFavorites(LKAuthObject authObject, long start, boolean isLoadingMore) {
        loadData(authObject, start, isLoadingMore);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getFavorite(authObject, start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("FavoritesPresenter::loadData() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.showSimpleData(result, isLoadingMore);
                            }
                        },
                        error -> {
                            Timber.e(error, "FavoritesPresenter::loadData() onError().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            Timber.d("FavoritesPresenter::loadData() onComplete().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    public void checkNoticeCountFromDatabase(long uid) {
        SubscriptionUtils.checkAndUnsubscribe(mCheckNoticeCountSubscription);
        mCheckNoticeCountSubscription = mLKongForumService.checkNoticeCountFromDatabase(uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onCheckNoticeCountComplete(result);
                        },
                        error -> {
                            Timber.e(error, "FavoritesPresenter::checkNoticeCountFromDatabase() onError().", LOG_TAG);
                        },
                        () -> {
                        }
                );
    }

    @Override
    public void destroy() {
        super.destroy();
        SubscriptionUtils.checkAndUnsubscribe(mCheckNoticeCountSubscription);
    }
}
