package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.ThreadListView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ThreadListPresenter implements BasePresenter<ThreadListView> {
    public static final String LOG_TAG = ThreadListPresenter.class.getName();
    LKongForumService mLKongForumService;
    ThreadListView mView;
    Subscription mLoadThreadListSubscription;

    @Inject
    public ThreadListPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
    }

    public void loadThreadList(long fid, int listType, boolean loadingMore) {
        loadThreadList(fid, -1, listType, loadingMore);
    }

    public void loadThreadList(long fid, long start, int listType, boolean loadingMore) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadThreadListSubscription);
        setLoadingStatus(loadingMore, true);
        mLoadThreadListSubscription = mLKongForumService.getForumThread(fid, start, listType)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.showThreadList(result, loadingMore);
                        },
                        error -> {
                            Timber.e(error, "ThreadListPresenter::loadThreadList() onError().", LOG_TAG);
                            setLoadingStatus(loadingMore, false);
                        },
                        () -> {
                            setLoadingStatus(loadingMore, false);
                        }
                );
    }


    @Override
    public void bindView(ThreadListView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptyTheadListView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mLoadThreadListSubscription);
    }

    private void setLoadingStatus(boolean loadingMore, boolean isLoading) {
        if(loadingMore)
            mView.setLoadingMore(isLoading);
        else
            mView.setLoading(isLoading);
    }

    private class EmptyTheadListView implements ThreadListView {

        @Override
        public void showThreadList(List<ThreadModel> threadList, boolean isLoadMore) {

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
