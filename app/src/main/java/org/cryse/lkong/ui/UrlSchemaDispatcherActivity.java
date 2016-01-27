package org.cryse.lkong.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.LKongUrlDispatcher;
import org.cryse.utils.preference.BooleanPrefs;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;

public class UrlSchemaDispatcherActivity extends Activity {
    AppNavigation mNavigation = new AppNavigation();

    BooleanPrefs mUseInAppBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectThis();
        mUseInAppBrowser = Prefs.getBooleanPrefs(
                PreferenceConstant.SHARED_PREFERENCE_USE_IN_APP_BROWSER,
                PreferenceConstant.SHARED_PREFERENCE_USE_IN_APP_BROWSER_VALUE
        );
        Uri data = getIntent().getData();
        String url = data.toString();
        LKongUrlDispatcher dispatcher = new LKongUrlDispatcher(mUrlCallback);
        dispatcher.parseUrl(url);
        finish();
    }

    private void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    private LKongUrlDispatcher.UrlCallback mUrlCallback = new LKongUrlDispatcher.UrlCallback() {
        @Override
        public void onUserByName(String username) {
            // Do nothing here, links only use internal.
        }

        @Override
        public void onThreadByPostId(long postId) {
            mNavigation.openActivityForPostListByPostId(UrlSchemaDispatcherActivity.this, postId);
        }

        @Override
        public void onThreadByPostId(long threadId, long postId) {
            mNavigation.openActivityForPostListByPostId(UrlSchemaDispatcherActivity.this, postId);
        }

        @Override
        public void onThreadByThreadId(long threadId) {
            mNavigation.openActivityForPostListByThreadId(UrlSchemaDispatcherActivity.this, threadId);
        }

        @Override
        public void onThreadByThreadId(long threadId, int page) {
            mNavigation.openActivityForPostListByThreadId(UrlSchemaDispatcherActivity.this, threadId, page);
        }

        @Override
        public void onFailed(String url) {
            mNavigation.openUrl(UrlSchemaDispatcherActivity.this, url, mUseInAppBrowser.get());
        }
    };
}
