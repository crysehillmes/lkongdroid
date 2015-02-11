package org.cryse.lkong.ui.navigation;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.logic.TimelineListType;
import org.cryse.lkong.ui.FavoritesFragment;
import org.cryse.lkong.ui.ForumListFragment;
import org.cryse.lkong.ui.HomePageFragment;
import org.cryse.lkong.ui.MainActivity;
import org.cryse.lkong.ui.NewPostActivity;
import org.cryse.lkong.ui.NewThreadActivity;
import org.cryse.lkong.ui.SettingsActivity;
import org.cryse.lkong.ui.SignInActivity;
import org.cryse.lkong.ui.TimelineFragment;
import org.cryse.lkong.utils.DataContract;

public class AndroidNavigation {
    private LKongApplication mApplication;
    private MainActivity mMainActivity;
    private FragmentManager mMainActivityFragmentManager;
    public AndroidNavigation(Application context) {
        this.mApplication = (LKongApplication)context;
    }

    public void attachMainActivity(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
        this.mMainActivityFragmentManager = mainActivity.getSupportFragmentManager();
    }

    public void detachMainActivity() {
        this.mMainActivity = null;
        this.mMainActivityFragmentManager = null;
    }

    public boolean isAttachToMainActivity() {
        return (mMainActivity != null) && (mMainActivityFragmentManager != null);
    }

    public boolean popEntireFragmentBackStack() {
        final int backStackCount = mMainActivityFragmentManager.getBackStackEntryCount();
        // Clear Back Stack
        for (int i = 0; i < backStackCount; i++) {
            mMainActivityFragmentManager.popBackStack();
        }
        return backStackCount > 0;
    }

    public void switchContentFragment(Fragment targetFragment, String backStackTag) {
        if(!isAttachToMainActivity())
            throw new IllegalStateException("Should attach to MainActivity before call any method.");
        popEntireFragmentBackStack();
        FragmentTransaction fragmentTransaction = mMainActivityFragmentManager
                .beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        if(backStackTag != null)
            fragmentTransaction.addToBackStack(backStackTag);
        fragmentTransaction.replace(R.id.container, targetFragment);
        fragmentTransaction.commit();
    }

    public void navigateToHomeFragment() {
        if(isAttachToMainActivity()) {
            Fragment fragment = HomePageFragment.newInstance(null);
            switchContentFragment(fragment, null);
        }
    }

    public void navigateToForumListFragment(Bundle args) {
        if(isAttachToMainActivity()) {
            Fragment fragment = ForumListFragment.newInstance(args);
            switchContentFragment(fragment, null);
        }
    }

    public void navigateToFavoritesFragment(Bundle args) {
        if(isAttachToMainActivity()) {
            Fragment fragment = FavoritesFragment.newInstance(args);
            switchContentFragment(fragment, null);
        }
    }

    public void navigateToTimelineFragment() {
        if(isAttachToMainActivity()) {
            Bundle args = new Bundle();
            args.putInt(TimelineFragment.BUNDLE_LIST_TYPE, TimelineListType.TYPE_TIMELINE);
            args.putBoolean(TimelineFragment.BUNDLE_IN_MAIN_ACTIVITY, true);
            Fragment fragment = TimelineFragment.newInstance(args);
            switchContentFragment(fragment, null);
        }
    }

    public void navigateToAtMeMessagesFragment() {
        if(isAttachToMainActivity()) {
            Bundle args = new Bundle();
            args.putInt(TimelineFragment.BUNDLE_LIST_TYPE, TimelineListType.TYPE_AT_ME);
            Fragment fragment = TimelineFragment.newInstance(args);
            switchContentFragment(fragment, null);
        }
    }

    public void navigateToSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public void navigateToSignInActivity(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
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
}
