package org.cryse.lkong.modules.notice;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.NoticeRateModel;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionPresenter;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NoticeRatePresenter extends SimpleCollectionPresenter<NoticeRateModel, SimpleCollectionView<NoticeRateModel>> {
    private static final String LOG_TAG = NoticeRatePresenter.class.getName();

    @Inject
    public NoticeRatePresenter(LKongForumService forumService) {
        super(forumService);
    }

    public void loadNotice(LKAuthObject authObject, boolean isLoadingMore) {
        loadNotice(authObject, -1, isLoadingMore);
    }

    public void loadNotice(LKAuthObject authObject, long start, boolean isLoadingMore) {
        loadData(authObject, start, isLoadingMore);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getNoticeRateLog(authObject, start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("NoticeRatePresenter::loadData() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.showSimpleData(result, isLoadingMore);
                            }
                        },
                        error -> {
                            Timber.e(error, "NoticeRatePresenter::loadData() onError().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            Timber.d("NoticeRatePresenter::loadData() onComplete().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }
}
