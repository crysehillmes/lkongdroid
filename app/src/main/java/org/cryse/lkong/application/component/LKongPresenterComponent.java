package org.cryse.lkong.application.component;

import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.application.modules.PreferenceModule;
import org.cryse.lkong.ui.FavoritesFragment;
import org.cryse.lkong.ui.FollowedForumsFragment;
import org.cryse.lkong.ui.ForumActivity;
import org.cryse.lkong.ui.ForumsFragment;
import org.cryse.lkong.ui.HomePageFragment;
import org.cryse.lkong.ui.InAppBrowserActivity;
import org.cryse.lkong.ui.MainActivity;
import org.cryse.lkong.ui.MentionsFragment;
import org.cryse.lkong.ui.NewPostActivity;
import org.cryse.lkong.ui.NewThreadActivity;
import org.cryse.lkong.ui.NoticeFragment;
import org.cryse.lkong.ui.NoticeRateFragment;
import org.cryse.lkong.ui.NotificationActivity;
import org.cryse.lkong.ui.NotificationFragment;
import org.cryse.lkong.ui.PostListActivity;
import org.cryse.lkong.ui.SearchActivity;
import org.cryse.lkong.ui.TimelineFragment;
import org.cryse.lkong.ui.UserProfileActivity;
import org.cryse.lkong.ui.UserProfileThreadsFragment;
import org.cryse.lkong.ui.UserProfileTimelineFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, LKongModule.class, PreferenceModule.class})
public interface LKongPresenterComponent {
    void inject(MainActivity activity);
    void inject(ForumsFragment fragment);
    void inject(ForumActivity activity);
    void inject(PostListActivity activity);
    void inject(NewPostActivity activity);
    void inject(NewThreadActivity activity);
    void inject(HomePageFragment activity);
    void inject(FavoritesFragment fragment);
    void inject(FollowedForumsFragment fragment);
    void inject(TimelineFragment fragment);
    void inject(MentionsFragment fragment);
    void inject(NotificationFragment fragment);
    void inject(NoticeFragment fragment);
    void inject(NoticeRateFragment fragment);
    void inject(NotificationActivity activity);
    void inject(SearchActivity activity);
    void inject(UserProfileActivity activity);
    void inject(InAppBrowserActivity activity);
    void inject(UserProfileTimelineFragment fragment);
    void inject(UserProfileThreadsFragment fragment);
}
