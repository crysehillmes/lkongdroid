package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.NoticeModel;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NoticePresenter extends SimpleCollectionPresenter<NoticeModel> {
    private static final String LOG_TAG = NoticePresenter.class.getName();
    @Inject
    public NoticePresenter(LKongForumService forumService) {
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
        mLoadDataSubscription = mLKongForumService.getNotice(authObject, start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("NoticePresenter::loadData() onNext().", LOG_TAG);
                            mView.showSimpleData(result, isLoadingMore);
                        },
                        error -> {
                            Timber.e(error, "NoticePresenter::loadData() onError().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            Timber.d("NoticePresenter::loadData() onComplete().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }
}
