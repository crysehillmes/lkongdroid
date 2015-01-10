package org.cryse.lkong.application.modules;

import android.content.Context;

import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.impl.LKongDatabaseSnappyImpl;
import org.cryse.lkong.logic.LKongForumService;
import org.cryse.lkong.logic.restservice.LKongRestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = { ContextModule.class })
public class LKongModule {

    @Provides
    public LKongRestService provideLKongRestService(@ApplicationContext Context context) {
        return new LKongRestService(context);
    }

    @Singleton
    @Provides
    public LKongDatabase provideLKongDatabase(@ApplicationContext Context context) {
        return new LKongDatabaseSnappyImpl(context);
    }

    @Singleton
    @Provides
    public LKongForumService provideLKongForumService(LKongRestService lKongRestService, LKongDatabase lKongDatabase) {
        return new LKongForumService(lKongRestService, lKongDatabase);
    }
}
