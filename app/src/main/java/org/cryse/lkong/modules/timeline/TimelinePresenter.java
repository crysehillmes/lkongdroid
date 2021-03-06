package org.cryse.lkong.modules.timeline;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.logic.TimelineListType;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionPresenter;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionView;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class TimelinePresenter extends SimpleCollectionPresenter<TimelineModel, SimpleCollectionView<TimelineModel>> {
    private static final String LOG_TAG = TimelinePresenter.class.getName();

    @Inject
    public TimelinePresenter(LKongForumService forumService) {
        super(forumService);
    }

    public void loadTimeline(LKAuthObject authObject, boolean isLoadingMore, boolean onlyThread) {
        loadTimeline(authObject, -1, isLoadingMore, onlyThread);
    }

    public void loadTimeline(LKAuthObject authObject, long start, boolean isLoadingMore, boolean onlyThread) {
        loadData(authObject, start, isLoadingMore, TimelineListType.TYPE_TIMELINE, onlyThread);
    }

    public void loadMentions(LKAuthObject authObject, boolean isLoadingMore) {
        loadMentions(authObject, -1, isLoadingMore);
    }

    public void loadMentions(LKAuthObject authObject, long start, boolean isLoadingMore) {
        loadData(authObject, start, isLoadingMore, TimelineListType.TYPE_MENTIONS, false);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getTimeline(authObject, start, (Integer) extraArgs[0], (Boolean) extraArgs[1])
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("TimelinePresenter::loadData() onNext().", LOG_TAG);
                            if (mView != null) {
                                mView.showSimpleData(result, isLoadingMore);
                            }
                        },
                        error -> {
                            Timber.e(error, "TimelinePresenter::loadData() onError().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            Timber.d("TimelinePresenter::loadData() onComplete().", LOG_TAG);
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }
}
