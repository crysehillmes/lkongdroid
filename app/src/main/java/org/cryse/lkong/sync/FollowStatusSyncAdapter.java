package org.cryse.lkong.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.UserAccount;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.broadcast.BroadcastConstants;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumContentValues;
import org.cryse.lkong.data.provider.followedforum.FollowedForumCursor;
import org.cryse.lkong.data.provider.followedforum.FollowedForumModel;
import org.cryse.lkong.data.provider.followedforum.FollowedForumSelection;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadContentValues;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserContentValues;
import org.cryse.lkong.data.provider.followeduser.FollowedUserSelection;
import org.cryse.lkong.logic.request.GetForumInfoRequest;
import org.cryse.lkong.model.ForumModel;
import org.cryse.lkong.utils.GzipUtils;
import org.cryse.lkong.account.LKAuthObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FollowStatusSyncAdapter extends AbstractThreadedSyncAdapter {
    public static final int SYNC_INTERVAL_SECONDS = 60 * 60;

    private AccountManager mAccountManager;
    private OkHttpClient mOkHttpClient;
    private CookieManager mCookieManager;
    public FollowStatusSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        init(context);
    }

    public FollowStatusSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        init(context);
    }

    private void init(Context context) {
        this.mAccountManager = AccountManager.get(context);
        this.mOkHttpClient = new OkHttpClient();
        this.mOkHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
        this.mOkHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
        this.mCookieManager = new CookieManager();
        this.mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.mOkHttpClient.setCookieHandler(mCookieManager);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        // Used to indicate to the SyncManager that future sync requests that match the request's
        // Account and authority should be delayed at least this many seconds.
        // syncResult.delayUntil = (System.currentTimeMillis() / 1000) + SYNC_INTERVAL_SECONDS;
        try {
            UserAccount userAccount = UserAccountManager.getUserAccountFromAccountManager(account, mAccountManager);
            LKAuthObject authObject = UserAccountManager.getAuthObject(userAccount);

            clearCookies();
            applyAuthCookies(authObject);

            String url = "http://lkong.cn";
            Request request = new Request.Builder()
                    .addHeader("Accept-Encoding", "gzip")
                    .addHeader("Cache-Control", "max-age=30")
                    .url(url)
                    .build();

            Response response = mOkHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseString = GzipUtils.responseToString(response);
            Document document = Jsoup.parseBodyFragment(responseString);
            Elements elements = document.select("#setfollows");
            if(elements != null && elements.size() > 0) {
                Element element = elements.get(0);
                String followedJson = element.html();
                Log.d("SYNC_ADAPTER", followedJson);
                JSONObject rootObject = new JSONObject(followedJson);

                // Get followed forums
                JSONArray forumsArray = rootObject.getJSONArray("fid");
                int fidsCount = forumsArray.length();
                long[] fids = new long[fidsCount];
                for(int i = 0; i <= fidsCount - 1; i++) {
                    String value = forumsArray.getString(i);
                    if(TextUtils.isDigitsOnly(value))
                        fids[i] = Long.valueOf(value);
                    else
                        fids[i] = -1;
                }
                syncFollowedForumStatus(authObject, authority, fids, provider);

                // Get followed users
                JSONArray usersArray = rootObject.getJSONArray("uid");
                int uidsCount = usersArray.length();
                long[] uids = new long[uidsCount];
                for(int i = 0; i <= uidsCount - 1; i++) {
                    String value = usersArray.getString(i);
                    if(TextUtils.isDigitsOnly(value))
                        uids[i] = Long.valueOf(value);
                    else
                        uids[i] = -1;
                }
                syncFollowedUserStatus(authObject, authority, uids, provider);
            }
            clearCookies();
        } catch (Exception exception) {
            Log.e("SYNC_ADAPTER", exception.getMessage(), exception);
        }
    }

    private void syncFollowedForumStatus(LKAuthObject authObject, String authority, long[] fids, ContentProviderClient provider) throws Exception {
        int fidCount = fids.length;
        for (int i = 0; i < fidCount; i++) {
            long fid = fids[i];
            if(fid == -1) continue;
            // 查找数据库表中是否已经有这一项，如果有的话则只调整 sortorder。
            FollowedForumSelection selection = new FollowedForumSelection();
            selection.userId(authObject.getUserId()).and().forumId(fid);
            // 判断列表中是不是有这一项，如果有的话移除
            Cursor cursor = provider.query(FollowedForumColumns.contentUri(authority), null, selection.sel(), selection.args(), selection.order());
            FollowedForumContentValues forumValues = new FollowedForumContentValues();
            if(cursor.getCount() == 1) {
                cursor.moveToFirst();
                FollowedForumCursor forumCursor = new FollowedForumCursor(cursor);
                forumValues.putUserId(authObject.getUserId());
                forumValues.putForumId(forumCursor.getForumId());
                forumValues.putForumName(forumCursor.getForumName());
                forumValues.putForumIcon(forumCursor.getForumIcon());
                forumValues.putForumSortValue(i);
            } else {
                // 数据库中没有相关的记录，完全从网络获取
                GetForumInfoRequest request = new GetForumInfoRequest(authObject, fid);
                ForumModel forumModel = request.execute();

                forumValues.putUserId(authObject.getUserId());
                forumValues.putForumId(forumModel.getFid());
                forumValues.putForumName(forumModel.getName());
                forumValues.putForumIcon(forumModel.getIcon());
                forumValues.putForumSortValue(i);
            }
            cursor.close();
            provider.insert(FollowedForumColumns.contentUri(authority), forumValues.values());
        }
        FollowedForumSelection deleteSelection = new FollowedForumSelection();
        deleteSelection.forumIdNot(fids).and().userId(authObject.getUserId());
        provider.delete(FollowedForumColumns.contentUri(authority), deleteSelection.sel(), deleteSelection.args());
        getContext().sendBroadcast(new Intent(BroadcastConstants.BROADCAST_SYNC_FOLLOWED_FORUMS_DONE));
    }

    private void syncFollowedThreadStatus(LKAuthObject authObject, String authority, List<Long> tids, ContentProviderClient provider) throws RemoteException {
        List<FollowedForumModel> followedForumModels = new ArrayList<>(tids.size());
        ContentValues[] values = new ContentValues[tids.size()];
        for (int i = 0; i < tids.size(); i++) {
            long fid = tids.get(i);
            FollowedThreadContentValues threadContentValues = new FollowedThreadContentValues();

            values[i] = threadContentValues.values();
        }
        provider.bulkInsert(FollowedThreadColumns.contentUri(authority), values);
    }

    private void syncFollowedUserStatus(LKAuthObject authObject, String authority, long[] uids, ContentProviderClient provider) throws RemoteException {
        int uidCount = uids.length;
        for (int i = 0; i < uidCount; i++) {
            long targetUserId = uids[i];
            if(targetUserId == -1) continue;
            FollowedUserSelection selection = new FollowedUserSelection();
            selection.userId(authObject.getUserId()).and().targetUserId(targetUserId);
            Cursor cursor = provider.query(FollowedUserColumns.contentUri(authority), null, selection.sel(), selection.args(), selection.order());
            if(cursor.getCount() == 1) {
                // Exist row, do nothing
                cursor.moveToFirst();
            } else {
                // Create new row
                FollowedUserContentValues userValues = new FollowedUserContentValues();
                userValues.putUserId(authObject.getUserId());
                userValues.putTargetUserId(targetUserId);
                provider.insert(FollowedUserColumns.contentUri(authority), userValues.values());
            }
            cursor.close();
        }
        FollowedUserSelection deleteSelection = new FollowedUserSelection();
        deleteSelection.userId(authObject.getUserId()).and().targetUserIdNot(uids);
        provider.delete(FollowedUserColumns.contentUri(authority), deleteSelection.sel(), deleteSelection.args());
        getContext().sendBroadcast(new Intent(BroadcastConstants.BROADCAST_SYNC_FOLLOWED_USERS_DONE));
    }


    private void applyAuthCookies(LKAuthObject authObject) {
        clearCookies();
        mCookieManager.getCookieStore().add(authObject.getAuthURI(), authObject.getAuthHttpCookie());
        mCookieManager.getCookieStore().add(authObject.getDzsbheyURI(), authObject.getDzsbheyHttpCookie());
        // cookieManager.getCookieStore().add(authObject.getIdentityURI(), authObject.getIdentityHttpCookie());
    }

    private void clearCookies() {
        mCookieManager.getCookieStore().removeAll();
    }
}
