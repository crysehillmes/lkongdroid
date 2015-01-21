package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.view.NewPostView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NewPostPresenter implements BasePresenter<NewPostView> {
    public static final String LOG_TAG = NewPostPresenter.class.getName();

    LKongForumService mLKongForumService;
    NewPostView mView;
    Subscription mUploadSubscription;

    @Inject
    public NewPostPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
    }

    public void newPost(LKAuthObject authObject, long tid, Long pid, String content) {
        SubscriptionUtils.checkAndUnsubscribe(mUploadSubscription);
        mUploadSubscription = mLKongForumService.newPostReply(authObject, tid, pid, content)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            Timber.d("NewReplyPresenter::newPost() onNext().", LOG_TAG);
                            mView.onPostComplete(result);
                        },
                        error -> {
                            Timber.e(error, "NewReplyPresenter::newPost() onError().", LOG_TAG);
                        },
                        () -> {
                            Timber.d("NewReplyPresenter::newPost() onComplete().", LOG_TAG);
                        }
                );
    }


    @Override
    public void bindView(NewPostView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = new EmptyNewPostView();
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mUploadSubscription);
    }

    private class EmptyNewPostView implements NewPostView {
        @Override
        public void onPostComplete(NewPostResult result) {

        }
    }
}
