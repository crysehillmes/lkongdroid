package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.TimelineView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class TimelinePresenter implements BasePresenter<TimelineView> {
    public static final String LOG_TAG = TimelinePresenter.class.getName();
    LKongForumService mLKongForumService;
    TimelineView mView;
    Subscription mLoadTimelineSubscription;

    @Inject
    public TimelinePresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
    }

    public void loadTimeline(LKAuthObject authObject, boolean loadingMore) {
        loadTimeline(authObject, -1, loadingMore);
    }

    public void loadTimeline(LKAuthObject authObject, long start, boolean loadingMore) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadTimelineSubscription);
        setLoadingStatus(loadingMore, true);
        mLoadTimelineSubscription = mLKongForumService.getTimeline(authObject, start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("ThreadListPresenter::loadTimeline() onNext().", LOG_TAG);
                            mView.showTimeline(result, loadingMore);
                        },
                        error -> {
                            Timber.e(error, "ThreadListPresenter::loadTimeline() onError().", LOG_TAG);
                            setLoadingStatus(loadingMore, false);
                        },
                        () -> {
                            Timber.d("ThreadListPresenter::loadTimeline() onComplete().", LOG_TAG);
                            setLoadingStatus(loadingMore, false);
                        }
                );
    }


    @Override
    public void bindView(TimelineView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptyTimelineView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mLoadTimelineSubscription);
    }

    private void setLoadingStatus(boolean loadingMore, boolean isLoading) {
        if (loadingMore)
            mView.setLoadingMore(isLoading);
        else
            mView.setLoading(isLoading);
    }

    private class EmptyTimelineView implements TimelineView {
        @Override
        public void showTimeline(List<TimelineModel> timelineItems, boolean loadMore) {

        }

        @Override
        public boolean isLoadingMore() {
            return false;
        }

        @Override
        public void setLoadingMore(boolean value) {

        }

        @Override
        public void setLoading(Boolean value) {

        }

        @Override
        public Boolean isLoading() {
            return null;
        }

        @Override
        public void showToast(int text_value, int toastType) {

        }
    }
}
