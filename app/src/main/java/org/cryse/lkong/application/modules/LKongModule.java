package org.cryse.lkong.application.modules;

import android.accounts.AccountManager;
import android.content.Context;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.data.LKongDatabase;
import org.cryse.lkong.data.impl.LKongDatabaseSqliteImpl;
import org.cryse.lkong.logic.LKongForumService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = { ContextModule.class })
public class LKongModule {

    @Singleton
    @Provides
    public LKongDatabase provideLKongDatabase(@ApplicationContext Context context) {
        return new LKongDatabaseSqliteImpl(context);
    }

    @Singleton
    @Provides
    public LKongForumService provideLKongForumService(LKongDatabase lKongDatabase) {
        return new LKongForumService(lKongDatabase);
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
