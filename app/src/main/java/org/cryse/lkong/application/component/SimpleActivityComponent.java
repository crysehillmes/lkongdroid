package org.cryse.lkong.application.component;

import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.PreferenceModule;
import org.cryse.lkong.ui.PhotoViewPagerActivity;
import org.cryse.lkong.ui.SettingsActivity;
import org.cryse.lkong.ui.SettingsFragment;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, PreferenceModule.class})
public interface SimpleActivityComponent {
    void inject(PhotoViewPagerActivity activity);
    void inject(SettingsActivity settingsActivity);
    void inject(SettingsFragment settingsFragment);
}
