package org.cryse.lkong.application.component;

import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.application.modules.PreferenceModule;
import org.cryse.lkong.ui.ForumListFragment;
import org.cryse.lkong.ui.NavigationDrawerFragment;
import org.cryse.lkong.ui.NewPostActivity;
import org.cryse.lkong.ui.NewThreadActivity;
import org.cryse.lkong.ui.PostListActivity;
import org.cryse.lkong.ui.SignInActivity;
import org.cryse.lkong.ui.ThreadListActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, LKongModule.class, PreferenceModule.class})
public interface UserAccountComponent {
    void inject(UserAccountManager userAccountManager);
}
