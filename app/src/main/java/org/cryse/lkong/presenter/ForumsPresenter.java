package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ForumsPresenter extends SimpleCollectionPresenter<ForumModel> {
    private static final String LOG_TAG = ForumsPresenter.class.getName();

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
}

