package org.cryse.lkong.modules.userprofile;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionPresenter;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionView;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserProfileTimelinePresenter extends SimpleCollectionPresenter<TimelineModel, SimpleCollectionView<TimelineModel>> {
    private static final String LOG_TAG = UserProfileTimelinePresenter.class.getName();

    @Inject
    public UserProfileTimelinePresenter(LKongForumService forumService) {
        super(forumService);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getUserAll(authObject, (Long) extraArgs[0], start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.showSimpleData(result, isLoadingMore);
                        },
                        error -> {
                            if(mView != null) {
                                mView.showSimpleData(new ArrayList<TimelineModel>(), isLoadingMore);
                                Timber.e(error, "UserProfileTimelinePresenter::loadData() onError().", LOG_TAG);
                            }
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    public void loadUserTimeline(LKAuthObject authObject, long uid, long start, boolean isLoadingMore) {
        loadData(authObject, start, isLoadingMore, uid);
    }
}
