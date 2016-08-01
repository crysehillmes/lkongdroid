package org.cryse.lkong.application.component;

import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.modules.browsehistory.BrowseHistoryFragment;
import org.cryse.lkong.modules.favorites.FavoritesFragment;
import org.cryse.lkong.modules.followedforums.FollowedForumsFragment;
import org.cryse.lkong.modules.forum.ForumActivity;
import org.cryse.lkong.modules.forums.ForumsFragment;
import org.cryse.lkong.modules.homepage.HomePageFragment;
import org.cryse.lkong.ui.HotThreadFragment;
import org.cryse.lkong.ui.MainActivity;
import org.cryse.lkong.ui.MentionsFragment;
import org.cryse.lkong.ui.NewPostActivity;
import org.cryse.lkong.ui.NewThreadActivity;
import org.cryse.lkong.modules.notice.NoticeFragment;
import org.cryse.lkong.modules.notice.NoticePrivateChatsFragment;
import org.cryse.lkong.modules.notice.NoticeRateFragment;
import org.cryse.lkong.modules.notice.NotificationActivity;
import org.cryse.lkong.modules.notice.NotificationFragment;
import org.cryse.lkong.modules.postlist.PostListActivity;
import org.cryse.lkong.modules.privatemessage.PrivateChatActivity;
import org.cryse.lkong.modules.privatemessage.PrivateChatFragment;
import org.cryse.lkong.modules.search.SearchFragment;
import org.cryse.lkong.modules.timeline.TimelineFragment;
import org.cryse.lkong.ui.UrlSchemaDispatcherActivity;
import org.cryse.lkong.modules.userprofile.UserProfileFragment;
import org.cryse.lkong.modules.userprofile.UserProfileThreadsFragment;
import org.cryse.lkong.modules.userprofile.UserProfileTimelineFragment;
import org.cryse.lkong.modules.userprofile.UserProfileUsersFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, LKongModule.class})
public interface LKongPresenterComponent {
    void inject(MainActivity activity);
    void inject(UrlSchemaDispatcherActivity activity);
    void inject(ForumsFragment fragment);
    void inject(ForumActivity activity);
    void inject(PostListActivity activity);
    void inject(NewPostActivity activity);
    void inject(NewThreadActivity activity);
    void inject(HomePageFragment fragment);
    void inject(FavoritesFragment fragment);
    void inject(HotThreadFragment fragment);
    void inject(BrowseHistoryFragment fragment);
    void inject(FollowedForumsFragment fragment);
    void inject(TimelineFragment fragment);
    void inject(MentionsFragment fragment);
    void inject(NotificationFragment fragment);
    void inject(NoticeFragment fragment);
    void inject(NoticeRateFragment fragment);
    void inject(NotificationActivity activity);
    void inject(SearchFragment fragment);
    void inject(UserProfileFragment fragment);
    void inject(UserProfileTimelineFragment fragment);
    void inject(UserProfileThreadsFragment fragment);
    void inject(UserProfileUsersFragment fragment);
    void inject(NoticePrivateChatsFragment fragment);
    void inject(PrivateChatFragment fragment);
    void inject(PrivateChatActivity activity);
}
