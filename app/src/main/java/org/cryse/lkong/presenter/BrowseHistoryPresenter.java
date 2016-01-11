package org.cryse.lkong.presenter;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.BrowseHistory;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.BrowseHistoryView;
import org.cryse.lkong.view.FavoritesView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class BrowseHistoryPresenter extends SimpleCollectionPresenter<BrowseHistory, BrowseHistoryView<BrowseHistory>> {
    private static final String LOG_TAG = BrowseHistoryPresenter.class.getName();

    Subscription mCheckNoticeCountSubscription;
    @Inject
    public BrowseHistoryPresenter(LKongForumService forumService) {
        super(forumService);
    }

    public void loadBrowseHistory(LKAuthObject authObject, boolean isLoadingMore) {
        loadBrowseHistory(authObject, -1, isLoadingMore);
    }

    public void loadBrowseHistory(LKAuthObject authObject, long start, boolean isLoadingMore) {
        loadData(authObject, start, isLoadingMore);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getBrowseHistory(authObject.getUserId(), (int)start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("BrowseHistoryPresenter::loadData() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.showSimpleData(result, isLoadingMore);
                            }
                        },
                        error -> {
                            Timber.e(error, "BrowseHistoryPresenter::loadData() onError().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            Timber.d("BrowseHistoryPresenter::loadData() onComplete().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    public void clearBrowseHistory(LKAuthObject authObject) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        mLoadDataSubscription = mLKongForumService.cleanBrowseHistory(authObject.getUserId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("BrowseHistoryPresenter::clearBrowseHistory() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.onClearBrowseHistory();
                            }
                        },
                        error -> {
                            Timber.e(error, "BrowseHistoryPresenter::clearBrowseHistory() onError().", LOG_TAG);
                        },
                        () -> {
                            Timber.d("BrowseHistoryPresenter::clearBrowseHistory() onComplete().", LOG_TAG);
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
                            Timber.e(error, "BrowseHistoryPresenter::checkNoticeCountFromDatabase() onError().", LOG_TAG);
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
