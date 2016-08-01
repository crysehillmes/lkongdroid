package org.cryse.lkong.modules.userprofile;

import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.model.SearchUserItem;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionPresenter;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.modules.simplecollection.SimpleCollectionView;

import java.util.ArrayList;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class UserProfileUsersPresenter extends SimpleCollectionPresenter<SearchUserItem, SimpleCollectionView<SearchUserItem>> {
    private static final String LOG_TAG = UserProfileUsersPresenter.class.getName();

    @Inject
    public UserProfileUsersPresenter(LKongForumService forumService) {
        super(forumService);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDataSubscription);
        long uid = (long) extraArgs[0];
        boolean follower = (boolean) extraArgs[1];
        setLoadingStatus(isLoadingMore, true);
        mLoadDataSubscription = mLKongForumService.getUserFollow(authObject, uid, follower, start)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if(mView != null)
                                mView.showSimpleData(result, isLoadingMore);
                        },
                        error -> {
                            if(mView != null) {
                                mView.showSimpleData(new ArrayList<SearchUserItem>(), isLoadingMore);
                                Timber.e(error, "UserProfileThreadsPresenter::loadData() onError().", LOG_TAG);
                            }
                            setLoadingStatus(isLoadingMore, false);
                        },
                        () -> {
                            setLoadingStatus(isLoadingMore, false);
                        }
                );
    }

    public void loadUserFollowerOrFollowing(LKAuthObject authObject, long uid, long start, boolean follower, boolean isLoadingMore) {
        loadData(authObject, start, isLoadingMore, uid, follower);
    }
}
