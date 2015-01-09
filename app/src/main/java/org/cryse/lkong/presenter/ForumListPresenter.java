package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.converter.ForumModel;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.view.ForumListView;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class ForumListPresenter implements BasePresenter<ForumListView> {
    public static final String LOG_TAG = ForumListPresenter.class.getName();
    LKongForumService mLKongForumService;

    ForumListView mView;

    Subscription mForumListSubscription;

    @Inject
    public ForumListPresenter(LKongForumService lKongForumService) {
        this.mLKongForumService = lKongForumService;
        this.mView = new EmptyForumListView();
    }

    public void getForumList() {
        SubscriptionUtils.checkAndUnsubscribe(mForumListSubscription);
        mView.setLoading(true);
        mForumListSubscription = mLKongForumService.getForumList()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d(String.format("ForumListPresenter::showForumList() mView instanceof EmptyForumListView = %s", mView instanceof EmptyForumListView), LOG_TAG);
                            Timber.d(String.format("ForumListPresenter::showForumList() result.size() = %d", result.size()), LOG_TAG);
                            mView.showForumList(result);
                        },
                        error -> {
                            mView.setLoading(false);
                            mView.showForumList(null);
                            mView.showToast(ToastErrorConstant.TOAST_FAILURE_FORUM_LIST, ToastSupport.TOAST_ALERT);
                            Timber.d(error, "ForumListPresenter::getForumList() on", LOG_TAG);
                        },
                        () -> {
                            mView.setLoading(false);
                            // getForumList() finished.
                            Timber.d("ForumListPresenter::getForumList() finished", LOG_TAG);
                        });
    }

    @Override
    public void bindView(ForumListView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptyForumListView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mForumListSubscription);
    }

    private class EmptyForumListView implements ForumListView {

        @Override
        public void showForumList(List<ForumModel> forumList) {

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
