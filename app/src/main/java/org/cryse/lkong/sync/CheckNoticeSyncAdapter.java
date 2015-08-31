package org.cryse.lkong.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.gson.Gson;

import org.cryse.lkong.R;
import org.cryse.lkong.account.UserAccount;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.broadcast.BroadcastConstants;
import org.cryse.lkong.constant.CacheConstants;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectColumns;
import org.cryse.lkong.data.provider.cacheobject.CacheObjectContentValues;
import org.cryse.lkong.logic.request.CheckNoticeCountRequest;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.ui.NotificationActivity;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.GsonUtils;
import org.cryse.lkong.utils.LKAuthObject;

public class CheckNoticeSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int NOTIFICATION_START_ID = 150;

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
            Gson gson = new Gson();
            String json = gson.toJson(noticeCountModel, NoticeCountModel.class);

            CacheObjectContentValues values = new CacheObjectContentValues();
            values.putCacheKey(CacheConstants.generateNoticeCountKey(authObject.getUserId()))
                    .putCacheValue(json);
            provider.insert(CacheObjectColumns.contentUri(authority), values.values());
            getContext().sendBroadcast(new Intent(BroadcastConstants.BROADCAST_SYNC_CHECK_NOTICE_COUNT_DONE));
            showNewNoticeNotification(authObject.getUserId());
        } catch (Exception exception) {
            Log.e("SYNC_ADAPTER", exception.getMessage(), exception);
        }
    }

    public void showNewNoticeNotification(long userId) {
        NotificationManager notificationManager =
                (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mResultBuilder = new NotificationCompat.Builder(getContext());
        Intent openNotificationActivityIntent = new Intent(getContext(), NotificationActivity.class);
        PendingIntent chaptersListIntent =
                PendingIntent.getActivity(getContext(), 0, openNotificationActivityIntent, PendingIntent.FLAG_ONE_SHOT);

        Bundle extras = Bundle.EMPTY;
        mResultBuilder.setContentTitle("You have new message.")
                .setContentText("You have new message.")
                .setSmallIcon(R.drawable.ic_notification_done)
                .setExtras(extras)
                .setContentIntent(chaptersListIntent)
                .setAutoCancel(true);
        notificationManager.notify(NOTIFICATION_START_ID + (int)userId, mResultBuilder.build());
    }
}
