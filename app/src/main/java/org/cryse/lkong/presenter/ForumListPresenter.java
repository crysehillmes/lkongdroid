package org.cryse.lkong.presenter;

import com.snappydb.SnappydbException;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.view.ForumListView;

import java.net.ConnectException;
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
                            mView.showForumList(result);
                        },
                        error -> {
                            Timber.d(error, "ForumListPresenter::getForumList() onError()", LOG_TAG);
                            mView.setLoading(false);
                            mView.showToast(ToastErrorConstant.TOAST_FAILURE_FORUM_LIST, ToastSupport.TOAST_ALERT);
                        },
                        () -> {
                            Timber.d("ForumListPresenter::getForumList() onComplete()", LOG_TAG);
                            mView.setLoading(false);
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
