package org.cryse.lkong.presenter;

import org.cryse.lkong.data.model.PinnedForumEntity;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PinnedForumsPresenter extends SimpleCollectionPresenter<PinnedForumEntity> {
    private static final String LOG_TAG = PinnedForumsPresenter.class.getName();
    @Inject
    public PinnedForumsPresenter(LKongForumService forumService) {
        super(forumService);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {

    }

    public void loadPinnedForums(long uid) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(false, true);
        mLoadDataSubscription = mLKongForumService.loadUserPinnedForums(uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("ForumsPresenter::loadData() onNext().", LOG_TAG);
                            mView.showSimpleData(result, false);
                        },
                        error -> {
                            Timber.e(error, "ForumsPresenter::loadData() onError().", LOG_TAG);
                            setLoadingStatus(false, false);
                        },
                        () -> {
                            Timber.d("ForumsPresenter::loadData() onComplete().", LOG_TAG);
                            setLoadingStatus(false, false);
                        }
                );
    }
}

