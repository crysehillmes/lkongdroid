package org.cryse.lkong.application.component;

import org.cryse.lkong.application.modules.ContextModule;
import org.cryse.lkong.application.modules.LKongModule;
import org.cryse.lkong.service.SendPostService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, LKongModule.class})
public interface SendServiceComponet {
    void inject(SendPostService service);
}