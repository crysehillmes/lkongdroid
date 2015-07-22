package org.cryse.lkong.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.UserAccount;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.data.provider.followedforum.FollowedForumColumns;
import org.cryse.lkong.data.provider.followedforum.FollowedForumContentValues;
import org.cryse.lkong.data.provider.followedforum.FollowedForumModel;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadColumns;
import org.cryse.lkong.data.provider.followedthread.FollowedThreadContentValues;
import org.cryse.lkong.data.provider.followeduser.FollowedUserColumns;
import org.cryse.lkong.data.provider.followeduser.FollowedUserContentValues;
import org.cryse.lkong.utils.GzipUtils;
import org.cryse.lkong.utils.LKAuthObject;
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
        this.mOkHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);
        this.mOkHttpClient.setReadTimeout(1, TimeUnit.MINUTES);
        this.mCookieManager = new CookieManager();
        this.mCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        this.mOkHttpClient.setCookieHandler(mCookieManager);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        try {
            UserAccount userAccount = UserAccountManager.getUserAccountFromAccountManager(account, mAccountManager);
            LKAuthObject authObject = UserAccountManager.getAuthObject(userAccount);

            clearCookies();
            applyAuthCookies(authObject);

            String url ="http://lkong.cn";
            Request request = new Request.Builder()
                    .addHeader("Accept-Encoding", "gzip")
                    .url(url)
                    .build();

            Response response = mOkHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            String responseString = GzipUtils.decompress(response.body().bytes());
            Document document = Jsoup.parseBodyFragment(responseString);
            Elements elements = document.select("#setfollows");
            if(elements != null && elements.size() > 0) {
                Element element = elements.get(0);
                String followedJson = element.html();
                Log.d("SYNC_ADAPTER", followedJson);

            }
            clearCookies();
        } catch (Exception exception) {
            Log.e("SYNC_ADAPTER", exception.getMessage(), exception);
        }
    }

    private void syncFollowedForumStatus(long currentUserId, List<Long> fids, ContentProviderClient provider) throws RemoteException {
        List<FollowedForumModel> followedForumModels = new ArrayList<>(fids.size());
        ContentValues[] values = new ContentValues[fids.size()];
        for (int i = 0; i < fids.size(); i++) {
            long fid = fids.get(i);
            FollowedForumContentValues forumValues = new FollowedForumContentValues();
            values[i] = forumValues.values();
        }
        provider.bulkInsert(FollowedForumColumns.CONTENT_URI, values);
    }

    private void syncFollowedThreadStatus(long currentUserId, List<Long> tids, ContentProviderClient provider) throws RemoteException {
        List<FollowedForumModel> followedForumModels = new ArrayList<>(tids.size());
        ContentValues[] values = new ContentValues[tids.size()];
        for (int i = 0; i < tids.size(); i++) {
            long fid = tids.get(i);
            FollowedThreadContentValues threadContentValues = new FollowedThreadContentValues();

            values[i] = threadContentValues.values();
        }
        provider.bulkInsert(FollowedThreadColumns.CONTENT_URI, values);
    }

    private void syncFollowedUserStatus(long currentUserId, List<Long> uids, ContentProviderClient provider) throws RemoteException {
        ContentValues[] values = new ContentValues[uids.size()];
        for (int i = 0; i < uids.size(); i++) {
            long uid = uids.get(i);
            FollowedUserContentValues contentValues = new FollowedUserContentValues();
            contentValues.putUserId(currentUserId);
            contentValues.putTargetUserId(uid);
            values[i] = contentValues.values();
        }
        provider.bulkInsert(FollowedUserColumns.CONTENT_URI, values);
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
