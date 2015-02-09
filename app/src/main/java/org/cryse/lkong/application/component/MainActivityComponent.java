package org.cryse.lkong.application.component;

import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.PreferenceModule;
import org.cryse.lkong.ui.MainActivity;
import org.cryse.lkong.ui.SettingsActivity;

import dagger.Component;

@Component(modules = {ContextModule.class, PreferenceModule.class})
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);
    void inject(SettingsActivity settingsActivity);
}
