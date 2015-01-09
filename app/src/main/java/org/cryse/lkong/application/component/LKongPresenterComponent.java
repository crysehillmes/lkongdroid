package org.cryse.lkong.application.component;

import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.application.modules.PreferenceModule;
import org.cryse.lkong.ui.NavigationDrawerFragment;
import org.cryse.lkong.ui.SignInActivity;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {ContextModule.class, LKongModule.class, PreferenceModule.class})
@Singleton
public interface LKongPresenterComponent {
    void inject(SignInActivity activity);
    void inject(NavigationDrawerFragment fragment);
}
