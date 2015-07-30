package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.PrivateChatModel;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.SimpleCollectionView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NoticePrivateChatsPresenter extends SimpleCollectionPresenter<PrivateChatModel, SimpleCollectionView<PrivateChatModel>> {
    private static final String LOG_TAG = NoticePrivateChatsPresenter.class.getName();

    @Inject
    public NoticePrivateChatsPresenter(LKongForumService forumService) {
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
        mLoadDataSubscription = mLKongForumService.getNoticePrivateChats(authObject, start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("NoticePrivateChatsPresenter::loadData() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.showSimpleData(result, isLoadingMore);
                            }
                        },
                        error -> {
                            Timber.e(error, "NoticePrivateChatsPresenter::loadData() onError().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            Timber.d("NoticePrivateChatsPresenter::loadData() onComplete().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }
}
