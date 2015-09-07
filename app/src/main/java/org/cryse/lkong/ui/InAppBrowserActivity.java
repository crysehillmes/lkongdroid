package org.cryse.lkong.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.widget.HTML5WebView;

import java.lang.ref.WeakReference;

import butterknife.ButterKnife;
import butterknife.Bind;

public class InAppBrowserActivity extends AbstractThemeableActivity {
    private static final String LOG_TAG = InAppBrowserActivity.class.getName();
    //public AppSettings settings;
    public String url;
    @Bind(R.id.activity_browser_toolbar)
    Toolbar mToolbar;
    @Bind(R.id.activity_browser_webview)
    HTML5WebView mBrowserView;
    @Bind(R.id.activity_browser_progressbar)
    ProgressBar mProgressBar;
    public Context context;

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.activity_zoom_enter, R.anim.slide_out_right);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_browser);
        ButterKnife.bind(this);
        setStatusBarColor();
        setSupportActionBar(mToolbar);
        final ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setBackgroundColor(getThemeEngine().getPrimaryColor());
        mBrowserView.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        url = getIntent().getStringExtra("url");
        mProgressBar.setMax(100);
        mBrowserView.setOnLoadProgressChangedListener((webView, newProgress) -> {
            mProgressBar.setProgress(newProgress);
        });

        if (url.contains("youtu") || url.contains("play.google.com")) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } else {
            if (savedInstanceState == null) {
                mBrowserView.loadUrl(url);
            } else {
                mBrowserView.restoreState(savedInstanceState);
            }
        }

        mBrowserView.setWebViewClient(new CustomWebClient(this, mProgressBar));
        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        }

        context = this;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState )
    {
        super.onSaveInstanceState(outState);
        mBrowserView.saveState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inapp_browser, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                mBrowserView.reload();
                return true;
            case R.id.action_open_web:
                try {
                    Uri weburi;

                    if (mBrowserView != null) {
                        weburi = Uri.parse(mBrowserView.getUrl());
                    } else { // on plain text
                        weburi = Uri.parse(url);
                    }

                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, weburi);
                    startActivity(launchBrowser);
                } catch (Exception e) {
                    e.printStackTrace();
                    // it is a picture link that they clicked from the timeline i think...
                }
                return true;

            default:
                return true;
        }
    }

    @Override
    public void onDestroy() {
        try {
            mBrowserView.destroy();
        } catch (Exception e) {
            // plain text browser
        }
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if (url.contains("vine")) {
                ((AudioManager)getSystemService(
                        Context.AUDIO_SERVICE)).requestAudioFocus(
                        new AudioManager.OnAudioFocusChangeListener() {
                            @Override
                            public void onAudioFocusChange(int focusChange) {}
                        }, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void onBackPressed() {
        if (mBrowserView != null && mBrowserView.canGoBack() && !mBrowserView.getUrl().equals(url)) {
            mBrowserView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    static class CustomWebClient extends WebViewClient {
        private WeakReference<Activity> mActivity;
        private WeakReference<ProgressBar> mProgressBar;
        public CustomWebClient(Activity activity, ProgressBar progressBar) {
            this.mActivity = new WeakReference<Activity>(activity);
            this.mProgressBar = new WeakReference<ProgressBar>(progressBar);
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {
            webView.loadUrl(url);
            if(mActivity.get() != null)
                mActivity.get().getIntent().putExtra("url", url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if(mActivity.get() != null)
                mActivity.get().setTitle(mActivity.get().getString(R.string.text_loading));
            if(mProgressBar.get() != null)
                mProgressBar.get().setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if(mActivity.get() != null)
                mActivity.get().setTitle(view.getTitle());
            if(mProgressBar.get() != null)
                mProgressBar.get().setVisibility(View.INVISIBLE);
        }
    }


    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }
}
