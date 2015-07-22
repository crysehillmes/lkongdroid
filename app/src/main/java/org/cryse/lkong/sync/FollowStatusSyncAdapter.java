package org.cryse.lkong.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.cryse.lkong.account.UserAccount;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.data.provider.followedforum.FollowedForumContentValues;
import org.cryse.lkong.data.provider.followedforum.FollowedForumModel;
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

    private void syncFollowedForumStatus(List<Long> fids, String authority, ContentProviderClient provider, SyncResult syncResult) {
        List<FollowedForumModel> followedForumModels = new ArrayList<>(fids.size());
        for (long fid : fids) {
            FollowedForumContentValues values = new FollowedForumContentValues();

        }
    }

    private void syncFollowedThreadStatus(List<Long> tids, String authority, ContentProviderClient provider, SyncResult syncResult) {

    }

    private void syncFollowedUserStatus(List<Long> uids, String authority, ContentProviderClient provider, SyncResult syncResult) {

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
