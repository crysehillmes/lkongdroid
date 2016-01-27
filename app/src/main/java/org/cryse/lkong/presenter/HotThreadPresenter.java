package org.cryse.lkong.presenter;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.model.HotThreadModel;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.ForumsView;
import org.cryse.lkong.view.SimpleCollectionView;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class HotThreadPresenter extends SimpleCollectionPresenter<HotThreadModel, SimpleCollectionView<HotThreadModel>> {
    private static final String LOG_TAG = HotThreadPresenter.class.getName();

    @Inject
    public HotThreadPresenter(LKongForumService forumService) {
        super(forumService);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getHotThread((Boolean) extraArgs[0])
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.showSimpleData(result, isLoadingMore);
                        },
                        error -> {
                            if(mView != null) {
                                mView.showSimpleData(new ArrayList<HotThreadModel>(), isLoadingMore);
                                Timber.e(error, "UserProfileThreadsPresenter::loadData() onError().", LOG_TAG);
                            }
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    public void loadHotThreads(boolean isDigest) {
        loadData(null, 0, false, isDigest);
    }
}

