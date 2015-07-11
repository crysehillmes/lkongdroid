package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.SimpleCollectionView;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserProfileThreadsPresenter extends SimpleCollectionPresenter<ThreadModel, SimpleCollectionView<ThreadModel>> {
    private static final String LOG_TAG = UserProfileThreadsPresenter.class.getName();

    @Inject
    public UserProfileThreadsPresenter(LKongForumService forumService) {
        super(forumService);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getUserThreads(authObject, (Long) extraArgs[0], start, (Boolean) extraArgs[1])
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.showSimpleData(result, isLoadingMore);
                        },
                        error -> {
                            if(mView != null) {
                                mView.showSimpleData(new ArrayList<ThreadModel>(), isLoadingMore);
                                Timber.e(error, "UserProfileThreadsPresenter::loadData() onError().", LOG_TAG);
                            }
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    public void loadUserThreads(LKAuthObject authObject, long uid, long start, boolean isDigest, boolean isLoadingMore) {
        loadData(authObject, start, isLoadingMore, uid, isDigest);
    }
}
