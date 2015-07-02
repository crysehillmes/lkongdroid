package org.cryse.lkong.presenter;

import org.cryse.lkong.data.model.PinnedForumEntity;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.SimpleCollectionView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PinnedForumsPresenter extends SimpleCollectionPresenter<PinnedForumEntity, SimpleCollectionView<PinnedForumEntity>> {
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
        mLoadDataSubscription = mLKongForumService.loadUserPinnedForums(uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("PinnedForumsPresenter::loadData() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.showSimpleData(result, false);
                            }
                        },
                        error -> {
                            Timber.e(error, "PinnedForumsPresenter::loadData() onError().", LOG_TAG);
                        },
                        () -> {
                            Timber.d("PinnedForumsPresenter::loadData() onComplete().", LOG_TAG);
                        }
                );
    }
}

