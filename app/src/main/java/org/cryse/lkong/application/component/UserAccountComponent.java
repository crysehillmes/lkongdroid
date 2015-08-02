package org.cryse.lkong.application.component;

import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.application.modules.PreferenceModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, LKongModule.class, PreferenceModule.class})
public interface UserAccountComponent {
    void inject(UserAccountManager userAccountManager);
}
