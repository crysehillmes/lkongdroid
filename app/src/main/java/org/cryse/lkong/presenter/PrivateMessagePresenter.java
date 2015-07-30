package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.PrivateChatView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PrivateMessagePresenter implements BasePresenter<PrivateChatView> {
    public static final String LOG_TAG = PrivateMessagePresenter.class.getSimpleName();
    LKongForumService mLKongForumService;
    PrivateChatView mView;
    Subscription mLoadMessagesSubscription;

    @Inject
    public PrivateMessagePresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
        this.mView = null;
    }

    public void loadPrivateMessages(LKAuthObject authObject, long targetUserId, long startSortKey, boolean isLoadingMore) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadMessagesSubscription);
        setLoadingStatus(isLoadingMore, true);
        mLoadMessagesSubscription = mLKongForumService.loadPrivateMessages(authObject, targetUserId, startSortKey)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.onLoadMessagesComplete(result, isLoadingMore);
                            }
                        },
                        error -> {
                            Timber.e(error, "PostListPresenter::loadThreadInfo() onError().", LOG_TAG);
                        },
                        () -> {
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    @Override
    public void bindView(PrivateChatView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = null;
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mLoadMessagesSubscription);
    }

    public void setLoadingStatus(boolean loadingMore, boolean isLoading) {
        if(mView == null) return;
        if (loadingMore)
            mView.setLoadingMore(isLoading);
        else
            mView.setLoading(isLoading);
    }
}
