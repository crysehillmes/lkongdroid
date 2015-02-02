package org.cryse.lkong.ui.navigation;


import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.ui.FavoritesFragment;
import org.cryse.lkong.ui.ForumListFragment;
import org.cryse.lkong.ui.MainActivity;
import org.cryse.lkong.ui.SignInActivity;

public class AndroidNavigation {
    private LKongApplication mApplication;
    private MainActivity mMainActivity;
    private FragmentManager mMainActivityFragmentManager;
    public AndroidNavigation(Application context) {
        this.mApplication = (LKongApplication)context;
    }

    public void attachMainActivity(MainActivity mainActivity) {
        this.mMainActivity = mainActivity;
        this.mMainActivityFragmentManager = mainActivity.getFragmentManager();
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
        fragmentTransaction.setCustomAnimations(android.R.animator.fade_in,
                android.R.animator.fade_out);
        if(backStackTag != null)
            fragmentTransaction.addToBackStack(backStackTag);
        fragmentTransaction.replace(R.id.container, targetFragment);
        fragmentTransaction.commit();
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

    public void navigateToSettingsActivity() {

    }

    public void navigateToSignInActivity(Context context) {
        Intent intent = new Intent(context, SignInActivity.class);
        context.startActivity(intent);
    }
}
