package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.ForumsView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ForumsPresenter extends SimpleCollectionPresenter<ForumModel, ForumsView<ForumModel>> {
    private static final String LOG_TAG = ForumsPresenter.class.getName();

    Subscription mCheckNoticeCountSubscription;

    @Inject
    public ForumsPresenter(LKongForumService forumService) {
        super(forumService);
    }

    public void loadForums(LKAuthObject authObject, boolean isLoadingMore, boolean updateFromWeb) {
        loadData(authObject, 0, false, updateFromWeb);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getForumList((Boolean) extraArgs[0])
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("ForumsPresenter::loadData() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.showSimpleData(result, isLoadingMore);
                            }
                        },
                        error -> {
                            Timber.e(error, "ForumsPresenter::loadData() onError().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            Timber.d("ForumsPresenter::loadData() onComplete().", LOG_TAG);
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
                            Timber.e(error, "ForumsPresenter::checkNoticeCountFromDatabase() onError().", LOG_TAG);
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

