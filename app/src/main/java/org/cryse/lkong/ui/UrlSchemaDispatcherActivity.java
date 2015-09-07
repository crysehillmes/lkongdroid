package org.cryse.lkong.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.qualifier.PrefsUseInAppBrowser;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.LKongUrlDispatcher;
import org.cryse.utils.preference.BooleanPreference;

import javax.inject.Inject;

public class UrlSchemaDispatcherActivity extends Activity {
    AppNavigation mNavigation = new AppNavigation();

    @Inject
    @PrefsUseInAppBrowser
    BooleanPreference mUseInAppBrowser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectThis();
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
