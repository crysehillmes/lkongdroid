package org.cryse.lkong.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import org.cryse.lkong.account.UserAccount;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.broadcast.BroadcastConstants;
import org.cryse.lkong.constant.CacheConstants;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectContentValues;
import org.cryse.lkong.logic.request.CheckNoticeCountRequest;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.utils.GsonUtils;
import org.cryse.lkong.utils.LKAuthObject;

public class CheckNoticeSyncAdapter extends AbstractThreadedSyncAdapter {

    private AccountManager mAccountManager;
    public CheckNoticeSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    public CheckNoticeSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context);
    }

    private void init(Context context) {
        this.mAccountManager = AccountManager.get(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // Used to indicate to the SyncManager that future sync requests that match the request's
        // Account and authority should be delayed at least this many seconds.
        // syncResult.delayUntil = (System.currentTimeMillis() / 1000) + SYNC_INTERVAL_SECONDS;
        try {
            UserAccount userAccount = UserAccountManager.getUserAccountFromAccountManager(account, mAccountManager);
            LKAuthObject authObject = UserAccountManager.getAuthObject(userAccount);

            CheckNoticeCountRequest request = new CheckNoticeCountRequest(authObject);
            NoticeCountModel noticeCountModel = request.execute();
            Gson gson = GsonUtils.getGson();
            String json = gson.toJson(noticeCountModel, NoticeCountModel.class);

            CacheObjectContentValues values = new CacheObjectContentValues();
            values.putCacheKey(CacheConstants.generateNoticeCountKey(authObject.getUserId()))
                    .putCacheValue(json);
            provider.insert(CacheObjectColumns.contentUri(authority), values.values());
            getContext().sendBroadcast(new Intent(BroadcastConstants.BROADCAST_SYNC_CHECK_NOTICE_COUNT_DONE));
        } catch (Exception exception) {
            Log.e("SYNC_ADAPTER", exception.getMessage(), exception);
        }
    }
}
