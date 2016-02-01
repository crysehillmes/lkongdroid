package org.cryse.lkong.ui.navigation;


import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Browser;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;

import com.afollestad.appthemeengine.Config;
import com.afollestad.appthemeengine.util.Util;
import com.thefinestartist.finestwebview.FinestWebView;

import org.cryse.lkong.R;
import org.cryse.lkong.account.AccountConst;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.sync.SyncUtils;
import org.cryse.lkong.ui.ForumActivity;
import org.cryse.lkong.ui.NewPostActivity;
import org.cryse.lkong.ui.NewThreadActivity;
import org.cryse.lkong.ui.NotificationActivity;
import org.cryse.lkong.ui.PostListActivity;
import org.cryse.lkong.ui.PrivateChatActivity;
import org.cryse.lkong.ui.SearchActivity;
import org.cryse.lkong.ui.SettingsActivity;
import org.cryse.lkong.ui.UserProfileActivity;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ThemeUtils;

public class AppNavigation {
    public AppNavigation() {
    }

    public void navigateToNotificationActivity(Context context) {
        Intent intent = new Intent(context, NotificationActivity.class);
        context.startActivity(intent);
    }

    public void navigateToSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public void navigateToSearchActivity(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        context.startActivity(intent);
    }

    public void navigateToSignInActivity(Activity activity, boolean startMainActivity) {
        addNewAccount(activity, activity.getResources().getString(R.string.account_type), AccountConst.AUTHTOKEN_TYPE_FULL_ACCESS);
    }

    public void openActivityForReplyToThread(Activity activity, long threadId, String threadSubject) {
        Intent intent = new Intent(activity, NewPostActivity.class);
        intent.putExtra(DataContract.BUNDLE_THREAD_ID, threadId);
        intent.putExtra(DataContract.BUNDLE_POST_REPLY_TITLE, activity.getString(R.string.format_post_reply_title, threadSubject));
        activity.startActivity(intent);
    }

    public void openActivityForReplyToPost(Activity activity, long threadId, String postAuthorName, long postId) {
        Intent intent = new Intent(activity, NewPostActivity.class);
        intent.putExtra(DataContract.BUNDLE_THREAD_ID, threadId);
        intent.putExtra(DataContract.BUNDLE_POST_ID, postId);
        intent.putExtra(DataContract.BUNDLE_POST_REPLY_TITLE, activity.getString(R.string.format_post_reply_title, postAuthorName));
        activity.startActivity(intent);
    }

    public void openActivityForNewThread(Activity activity, long forumId, String forumName) {
        Intent intent = new Intent(activity, NewThreadActivity.class);
        intent.putExtra(DataContract.BUNDLE_FORUM_ID, forumId);
        intent.putExtra(DataContract.BUNDLE_FORUM_NAME, forumName);
        activity.startActivity(intent);
    }

    public void openActivityForEditPost(Activity activity, long threadId, String postAuthorName, long postId, String htmlContent) {
        Intent intent = new Intent(activity, NewPostActivity.class);
        intent.putExtra(DataContract.BUNDLE_THREAD_ID, threadId);
        intent.putExtra(DataContract.BUNDLE_POST_ID, postId);
        intent.putExtra(DataContract.BUNDLE_POST_REPLY_TITLE, activity.getString(R.string.format_post_reply_title, postAuthorName));
        intent.putExtra(DataContract.BUNDLE_IS_EDIT_MODE, true);
        intent.putExtra(DataContract.BUNDLE_EDIT_CONTENT, htmlContent);
        activity.startActivity(intent);
    }

    public void openActivityForEditThread(Activity activity, long tid, long pid, String title, String htmlContent) {
        Intent intent = new Intent(activity, NewThreadActivity.class);
        intent.putExtra(DataContract.BUNDLE_IS_EDIT_MODE, true);
        intent.putExtra(DataContract.BUNDLE_THREAD_ID, tid);
        intent.putExtra(DataContract.BUNDLE_POST_ID, pid);
        intent.putExtra(DataContract.BUNDLE_EDIT_TITLE, title);
        intent.putExtra(DataContract.BUNDLE_EDIT_CONTENT, htmlContent);
        activity.startActivity(intent);
    }

    public void openActivityForPostListByTimelineModel(Context context, TimelineModel item) {
        if(item.getId().startsWith("thread_")) {
            openActivityForPostListByThreadId(context, Long.valueOf(item.getId().substring(7)));
        } else if(item.getId().startsWith("post_")) {
            openActivityForPostListByPostId(context, Long.valueOf(item.getId().substring(5)));
        } else {
            openActivityForPostListByThreadId(context, item.getTid());
        }
    }

    public void openActivityForPostListByPostId(Context context, long postId) {
        Intent intent = new Intent(context, PostListActivity.class);
        intent.putExtra(DataContract.BUNDLE_POST_ID, postId);
        context.startActivity(intent);
    }

    public void openActivityForPostListByThreadId(Context context, long threadId) {
        openActivityForPostListByThreadId(context, threadId, 1);
    }

    public void openActivityForPostListByThreadId(Context context, long threadId, int page) {
        Intent intent = new Intent(context, PostListActivity.class);
        intent.putExtra(DataContract.BUNDLE_THREAD_ID, threadId);
        intent.putExtra(DataContract.BUNDLE_THREAD_CURRENT_PAGE, page);
        context.startActivity(intent);
    }

    public void openActivityForForumByForumId(Context context, long forumId, String forumName, String forumDescription) {
        Intent intent = new Intent(context, ForumActivity.class);
        intent.putExtra(DataContract.BUNDLE_FORUM_ID, forumId);
        intent.putExtra(DataContract.BUNDLE_FORUM_NAME, forumName);
        intent.putExtra(DataContract.BUNDLE_FORUM_DESCRIPTION, forumDescription);
        context.startActivity(intent);
    }

    public void openActivityForUserProfile(Activity context, int[] startingLocation, long uid) {
        UserProfileActivity.startUserProfileFromLocation(context, startingLocation, uid);
    }

    public void openActivityForUserProfileNyName(Activity context, int[] startingLocation, String name) {
        UserProfileActivity.startUserProfileFromLocation(context, startingLocation, name);
    }

    public void openActivityForPrivateMessage(Activity context, long targetUserId, String targetUserName) {
        Intent intent = new Intent(context, PrivateChatActivity.class);
        intent.putExtra(DataContract.BUNDLE_TARGET_USER_ID, targetUserId);
        intent.putExtra(DataContract.BUNDLE_TARGET_USER_NAME, targetUserName);
        context.startActivity(intent);
    }

    public void openUrl(Activity context, String url, boolean inAppBrowser) {
        if(inAppBrowser) {
            String ateKey = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("dark_theme", false) ?
                    "dark_theme" : "light_theme";
            int statusBarColor = Config.primaryColorDark(context, ateKey);
            int primaryColor = Config.primaryColor(context, ateKey);
            int accentColor = Config.primaryColor(context, ateKey);
            int iconColor = Util.isColorLight(primaryColor) ? Color.BLACK : Color.WHITE;
            int iconColorPressed = ThemeUtils.makeColorDarken(iconColor, 0.8f);
            int iconColorDisabled = ThemeUtils.makeColorDarken(iconColor, 0.6f);
            FinestWebView.Builder builder = new FinestWebView.Builder(context);
            builder
                    .statusBarColor(statusBarColor)
                    .toolbarColor(primaryColor)
                    .toolbarScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS)
                    .iconDefaultColor(iconColor)
                    .iconDisabledColor(iconColorDisabled)
                    .iconPressedColor(iconColorPressed)
                    .titleColor(iconColor)
                    .urlColor(iconColorPressed)
                    .progressBarColor(accentColor)
                    .show(url);
            /*Intent intent = new Intent(context, InAppBrowserActivity.class);
            intent.putExtra("url", url);
            context.startActivity(intent);*/
        } else {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
            context.startActivity(intent);
        }
    }

    public void navigateToManageAccount(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_SYNC_SETTINGS);
        //intent.putExtra(Settings.EXTRA_ACCOUNT_TYPES, new String[] {"org.cryse.lkong"});
        intent.putExtra(Settings.EXTRA_AUTHORITIES, new String[] {SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE});
        activity.startActivity(intent);
    }

    private void addNewAccount(Activity activity, String accountType, String authTokenType) {
        AccountManager accountManager = AccountManager.get(activity);
        final AccountManagerFuture<Bundle> future = accountManager.addAccount(accountType, authTokenType, null, null, activity, new AccountManagerCallback<Bundle>() {
            @Override
            public void run(AccountManagerFuture<Bundle> future) {
                try {
                    Bundle bnd = future.getResult();
                    /*showMessage("Account was created");
                    Log.d("udinic", "AddNewAccount Bundle is " + bnd);*/

                } catch (Exception e) {/*
                    e.printStackTrace();
                    showMessage(e.getMessage());*/
                }
            }
        }, null);
    }
}
