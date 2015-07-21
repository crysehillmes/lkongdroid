package org.cryse.lkong.application.modules;

import android.accounts.AccountManager;
import android.content.Context;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.LKongDatabaseHelper;
import org.cryse.lkong.data.dao.CacheObjectDao;
import org.cryse.lkong.data.dao.PinnedForumDao;
import org.cryse.lkong.data.impl.LKongDatabaseSqliteImpl;
import org.cryse.lkong.event.RxEventBus;
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
    public LKongDatabaseHelper provideLKongDatabaseHelper(@ApplicationContext Context context) {
        return new LKongDatabaseHelper(context);
    }

    @Singleton
    @Provides
    public CacheObjectDao provideCacheObjectDao(LKongDatabaseHelper helper) {
        return new CacheObjectDao(helper);
    }

    @Singleton
    @Provides
    public PinnedForumDao providePinnedForumDao(LKongDatabaseHelper helper) {
        return new PinnedForumDao(helper);
    }

    @Singleton
    @Provides
    public LKongDatabase provideLKongDatabase(CacheObjectDao cacheObjectDao, PinnedForumDao pinnedForumDao) {
        return new LKongDatabaseSqliteImpl(cacheObjectDao, pinnedForumDao);
    }

    @Singleton
    @Provides
    public LKongForumService provideLKongForumService(LKongRestService lKongRestService, LKongDatabase lKongDatabase, RxEventBus rxEventBus) {
        return new LKongForumService(lKongRestService, lKongDatabase, rxEventBus);
    }

    @Singleton
    @Provides
    public UserAccountManager provideUserAccountManager(@ApplicationContext Context context) {
        LKongApplication application = (LKongApplication)context;
        return application.getUserAccountManager();
    }

    @Singleton
    @Provides
    public AccountManager provideAccountManager(@ApplicationContext Context context) {
        LKongApplication application = (LKongApplication)context;
        return AccountManager.get(context);
    }
}
