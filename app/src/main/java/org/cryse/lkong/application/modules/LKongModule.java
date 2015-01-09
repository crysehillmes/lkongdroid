package org.cryse.lkong.application.modules;

import android.content.Context;

import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.logic.restservice.LKongRestService;

import dagger.Module;
import dagger.Provides;

@Module(includes = { ContextModule.class })
public class LKongModule {

    @Provides
    public LKongRestService provideLKongRestService(@ApplicationContext Context context) {
        return new LKongRestService(context);
    }

    @Provides
    public LKongForumService provideLKongForumService(LKongRestService lKongRestService) {
        return new LKongForumService(lKongRestService);
    }
}
