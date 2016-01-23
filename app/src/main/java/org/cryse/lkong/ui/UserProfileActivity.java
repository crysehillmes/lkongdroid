package org.cryse.lkong.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import org.cryse.lkong.R;
import org.cryse.lkong.utils.DataContract;

public class UserProfileActivity extends SimpleContainerActivity {
    private static final String TAG = UserProfileActivity.class.getCanonicalName();

    public static void startUserProfileFromLocation(Context startingContext, int[] startingLocation, long uid) {
        Intent intent = new Intent(startingContext, UserProfileActivity.class);
        intent.putExtra(UserProfileFragment.ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(DataContract.BUNDLE_USER_ID, uid);
        startingContext.startActivity(intent);
    }

    public static void startUserProfileFromLocation(Context startingContext, int[] startingLocation, String userName) {
        Intent intent = new Intent(startingContext, UserProfileActivity.class);
        intent.putExtra(UserProfileFragment.ARG_REVEAL_START_LOCATION, startingLocation);
        intent.putExtra(DataContract.BUNDLE_USER_NAME, userName);
        startingContext.startActivity(intent);
    }

    @Override
    protected void injectThis() {

    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected Fragment newFragment() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        return UserProfileFragment.newInstance(extras);
    }

    public void goToAllActivitiesFragment(long uid, String userName) {
        Fragment fragment = UserProfileTimelineFragment.newInstance(uid, userName);
        switchFragment(fragment, UserProfileTimelineFragment.class.getName() + Long.toString(uid));
    }

    public void goToFollowerFragment(long uid, String userName) {
        Fragment fragment = UserProfileUsersFragment.newInstance(uid, userName, true);
        switchFragment(fragment, UserProfileUsersFragment.class.getName() + "follower"  + Long.toString(uid));
    }

    public void goToFollowingFragment(long uid, String userName) {
        Fragment fragment = UserProfileUsersFragment.newInstance(uid, userName, false);
        switchFragment(fragment, UserProfileUsersFragment.class.getName() + "following"  + Long.toString(uid));
    }

    public void goToThreadFragment(long uid, String userName) {
        Fragment fragment = UserProfileThreadsFragment.newInstance(uid, userName, false);
        switchFragment(fragment, UserProfileThreadsFragment.class.getName() + "thread"  + Long.toString(uid));
    }

    public void goToDigestsFragment(long uid, String userName) {
        Fragment fragment = UserProfileThreadsFragment.newInstance(uid, userName, true);
        switchFragment(fragment, UserProfileThreadsFragment.class.getName() + "digest"  + Long.toString(uid));
    }

    private void switchFragment(Fragment fragment, String backstackTag) {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.setCustomAnimations(
                android.R.anim.fade_in,
                android.R.anim.fade_out,
                android.R.anim.fade_in,
                android.R.anim.fade_out
        );
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(backstackTag);
        fragmentTransaction.commit();
    }
}
