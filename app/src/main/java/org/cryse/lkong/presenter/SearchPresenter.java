package org.cryse.lkong.presenter;

import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.SearchDataSet;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.view.SearchForumView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class SearchPresenter implements BasePresenter<SearchForumView> {
    private static final String LOG_TAG = SearchPresenter.class.getName();
    LKongForumService mLKongForumService;
    Subscription mSearchSubscription;
    SearchForumView mView;
    @Inject
    public SearchPresenter(LKongForumService forumService) {
        this.mLKongForumService = forumService;
    }

    public void search(LKAuthObject authObject, String search) {
        SubscriptionUtils.checkAndUnsubscribe(mSearchSubscription);
        mSearchSubscription = mLKongForumService.search(authObject, search)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.onSearchDone(result);
                        },
                        error -> {
                            if(mView != null) {
                                mView.onSearchFailed(0, error);
                                Timber.e(error, "SearchPresenter::search() onError().", LOG_TAG);
                                mView.setLoading(false);
                            }
                        },
                        () -> {
                        }
                );
    }

    @Override
    public void bindView(SearchForumView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = null;
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mSearchSubscription);
    }
}
