package org.cryse.lkong.ui;

import android.accounts.Account;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import org.cryse.changelog.ChangeLogUtils;
import org.cryse.lkong.R;
import org.cryse.lkong.account.UserAccount;
import org.cryse.lkong.application.AppPermissions;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.AccountRemovedEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.event.NewAccountEvent;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.sync.SyncUtils;
import org.cryse.lkong.ui.common.AbstractActivity;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.utils.preference.IntegerPrefs;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;
import org.cryse.utils.preference.StringPrefs;

import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AbstractActivity implements EasyPermissions.PermissionCallbacks{
    private static final String LOG_TAG = MainActivity.class.getName();
    AppNavigation mNavigation = new AppNavigation();
    @Inject
    UserAccountManager mUserAccountManager;
    StringPrefs mCheckNoticeDuration;
    IntegerPrefs mVersionCodePref;

    AccountHeader mAccountHeader;
    Drawer mNaviagtionDrawer;

    UserAccount mCurrentAccount = null;

    int mCurrentSelection = 0;
    boolean mIsRestorePosition = false;
    List<UserAccount> mUserAccountList;
    /**
     * Used to post delay navigation action to improve UX
     */
    private Handler mHandler = new Handler();
    private Runnable mPendingRunnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCheckNoticeDuration = Prefs.getStringPrefs(
                PreferenceConstant.SHARED_PREFERENCE_CHECK_NOTIFICATION_DURATION,
                PreferenceConstant.SHARED_PREFERENCE_CHECK_NOTIFICATION_DURATION_VALUE
        );
        mVersionCodePref = Prefs.getIntPrefs(
                PreferenceConstant.SHARED_PREFERENCE_VERSION_CODE,
                PreferenceConstant.SHARED_PREFERENCE_VERSION_CODE_VALUE
        );
        if(!mUserAccountManager.isSignedIn()) {
            mNavigation.navigateToSignInActivity(this, true);
            closeActivityWithTransition();
            return;
        }
        /*setDrawerLayoutBackground(isNightMode());
        getDrawerLayout().setStatusBarBackgroundColor(getThemeEngine().getPrimaryDarkColor(this));
        getSwipeBackLayout().setEnableGesture(false);*/
        if(savedInstanceState!=null && savedInstanceState.containsKey("selection_item_position")) {
            mCurrentSelection = savedInstanceState.getInt("selection_item_position");
            mIsRestorePosition = true;
        } else {
            mCurrentSelection = 1001;
            mIsRestorePosition = false;
        }
        initDrawer();
        checkStoragePermissions();
    }

    private void initDrawer() {
        DrawerImageLoader.init(new DrawerImageLoader.IDrawerImageLoader() {
            @Override
            public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                Glide.with(MainActivity.this).load(uri).placeholder(placeholder).into(imageView);
            }

            @Override
            public void cancel(ImageView imageView) {
                //Glide.clear(imageView);
            }

            @Override
            public Drawable placeholder(Context ctx) {
                return null;
            }

            @Override
            public Drawable placeholder(Context ctx, String tag) {
                return null;
            }
        });

        // Create the AccountHeader
        AccountHeaderBuilder accountHeaderBuilder = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(
                        isNightMode() ? R.drawable.drawer_top_image_dark : R.drawable.drawer_top_image_light
                );
        accountHeaderBuilder.withOnAccountHeaderListener((view, iProfile, b) -> {
            if (iProfile.getIdentifier() == -3001) {
                mNavigation.navigateToSignInActivity(MainActivity.this, false);
            } else {
                long uid = iProfile.getIdentifier();
                if(mUserAccountManager.getCurrentUserId() == uid) {
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mNavigation.openActivityForUserProfile(this, startingLocation, uid);
                } else {
                    mUserAccountManager.setCurrentUserAccount(uid);
                    getEventBus().sendEvent(new CurrentAccountChangedEvent());
                }
            }
            return true;
        }).withCurrentProfileHiddenInList(true);
        mAccountHeader = accountHeaderBuilder.build();
        IDrawerItem[] drawerItems = new IDrawerItem[5];
        drawerItems[0] = new PrimaryDrawerItem().withName(R.string.drawer_item_homepage).withIcon(R.drawable.ic_drawer_timeline).withIdentifier(1001);
        drawerItems[1] = new PrimaryDrawerItem().withName(R.string.drawer_item_favorites).withIcon(R.drawable.ic_drawer_favorites).withIdentifier(1003);
        drawerItems[2] = new PrimaryDrawerItem().withName(R.string.drawer_item_browse_history).withIcon(R.drawable.ic_drawer_browse_history).withIdentifier(1004);
        drawerItems[3] = new DividerDrawerItem();
        drawerItems[4] = new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIdentifier(1101).withSelectable(false);
        //Now create your drawer and pass the AccountHeader.Result
        mNaviagtionDrawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(mAccountHeader)
                .withStatusBarColor(getPrimaryDarkColor())
                .addDrawerItems(
                        drawerItems
                )
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {

                    }

                    @Override
                    public void onDrawerClosed(View view) {
                        supportInvalidateOptionsMenu();
                        // If mPendingRunnable is not null, then add to the message queue
                        if (mPendingRunnable != null) {
                            mHandler.post(mPendingRunnable);
                            mPendingRunnable = null;
                        }
                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        if (iDrawerItem instanceof PrimaryDrawerItem)
                            mCurrentSelection = iDrawerItem.getIdentifier();
                        mPendingRunnable = () ->  onNavigationSelected(iDrawerItem);
                        return false;
                    }
                })
                .build();
        addAccountProfile();
        if(mCurrentSelection == 1001 && !mIsRestorePosition) {
            mNaviagtionDrawer.setSelection(1001, false);
            navigateToHomePageFragment();
            // mNavigation.navigateToHomePageFragment();
        } else if(mIsRestorePosition) {
            mNaviagtionDrawer.setSelection(mCurrentSelection, false);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        checkVersionCode();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Account account = mUserAccountManager.getCurrentUserAccount().getAccount();
        // SyncUtils.setPeriodicSync(account, SyncUtils.SYNC_AUTHORITY, true, SyncUtils.SYNC_FREQUENCE);
        SyncUtils.manualSync(account, SyncUtils.SYNC_AUTHORITY_FOLLOW_STATUS);
        SyncUtils.setPeriodicSync(
                account,
                SyncUtils.SYNC_AUTHORITY_FOLLOW_STATUS,
                false,
                SyncUtils.SYNC_FREQUENCE_HALF_HOUR
        );
        SyncUtils.manualSync(account, SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE);
        SyncUtils.setPeriodicSync(
                account,
                SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE,
                false,
                Integer.valueOf(mCheckNoticeDuration.get())
        );
    }

    private void onNavigationSelected(IDrawerItem drawerItem) {
        switch (drawerItem.getIdentifier()) {
            case 1001:
                navigateToHomePageFragment();
                break;
            case 1003:
                navigateToFavoritesFragment(null);
                break;
            case 1004:
                navigateToBrowseHistoryFragment(null);
                break;
            case 1101:
                mNavigation.navigateToSettingsActivity(MainActivity.this);
                break;
            default:
                throw new IllegalArgumentException("Unknown NavigationDrawerItem position.");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selection_item_position", mCurrentSelection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentActivityExit(this, LOG_TAG);
    }

    public void onSectionAttached(String title) {
        setTitle(title);
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof NewAccountEvent) {
            addAccountProfile();
        } else if(event instanceof AccountRemovedEvent) {
            if(!mUserAccountManager.isSignedIn()) {
                mNavigation.navigateToSignInActivity(this, true);
                closeActivityWithTransition();
            } else {
                addAccountProfile();
            }
        }
    }

    private void addAccountProfile() {
        if(mAccountHeader.getProfiles() != null) {
            int profilesCount = mAccountHeader.getProfiles().size();
            for (int i = 0; i < profilesCount; i++) {
                mAccountHeader.removeProfile(0);
            }
        }
        try {
            mCurrentAccount = mUserAccountManager.getCurrentUserAccount();
            mUserAccountList = mUserAccountManager.getUserAccounts();
            for (UserAccount entity : mUserAccountList) {

                ProfileDrawerItem profileDrawerItem = new ProfileDrawerItem()
                        .withIcon(entity.getUserAvatar())
                        .withName(entity.getUserName())
                        .withEmail(entity.getUserEmail())
                        .withIdentifier((int) entity.getUserId());
                mAccountHeader.addProfiles(profileDrawerItem);
            }
            mAccountHeader.addProfiles(
                    new ProfileDrawerItem()
                            .withName(getString(R.string.drawer_item_account_add))
                            .withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_drawer_account_add, getTheme()))
                            .withIdentifier(-3001)
                            .withSelectable(false)
            );
        } catch (NeedSignInException ex) {
            mNavigation.navigateToSignInActivity(this, true);
            closeActivityWithTransition();
            return;
        }
        getEventBus().sendEvent(new CurrentAccountChangedEvent());
    }

    @Override
    public void onBackPressed() {
        if(mNaviagtionDrawer != null && mNaviagtionDrawer.isDrawerOpen()) {
            mNaviagtionDrawer.closeDrawer();
            return;
        }
        if (!getSupportFragmentManager().popBackStackImmediate()) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void closeActivityWithTransition() {
        finish();
    }

    public Drawer getNavigationDrawer() {
        return mNaviagtionDrawer;
    }

    public void checkVersionCode() {
        Observable.create((Subscriber<? super Integer> subscriber) -> {
            try {
                int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                if (versionCode > mVersionCodePref.get()) {
                    mVersionCodePref.set(versionCode);
                    subscriber.onNext(versionCode);
                    return;
                }
                subscriber.onNext(0);
                subscriber.onCompleted();
            } catch (PackageManager.NameNotFoundException e) {
                subscriber.onError(e);
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        versionCode -> {
                            if (versionCode > 0) {
                                ChangeLogUtils reader = new ChangeLogUtils(this, R.xml.changelog);

                                new MaterialDialog.Builder(this)
                                        .title(R.string.text_new_version_changes)
                                        .content(reader.toSpannable(versionCode))
                                        .show();
                            }
                        },
                        error -> {
                            Timber.d(error, error.getMessage(), LOG_TAG);
                        },
                        () -> {
                        });
    }

    public boolean popEntireFragmentBackStack() {
        final int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        // Clear Back Stack
        for (int i = 0; i < backStackCount; i++) {
            getSupportFragmentManager().popBackStack();
        }
        return backStackCount > 0;
    }

    public void switchContentFragment(Fragment targetFragment, String backStackTag) {
        popEntireFragmentBackStack();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                .beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        if (backStackTag != null)
            fragmentTransaction.addToBackStack(backStackTag);
        fragmentTransaction.replace(R.id.container, targetFragment);
        fragmentTransaction.commit();
    }

    public void navigateToFavoritesFragment(Bundle args) {
        Fragment fragment = FavoritesFragment.newInstance(args);
        switchContentFragment(fragment, null);
    }

    public void navigateToBrowseHistoryFragment(Bundle args) {
        Fragment fragment = BrowseHistoryFragment.newInstance(args);
        switchContentFragment(fragment, null);
    }

    public void navigateToHomePageFragment() {
        Bundle args = new Bundle();
        Fragment fragment = HomePageFragment.newInstance(args);
        switchContentFragment(fragment, null);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    private void checkStoragePermissions() {
        if (EasyPermissions.hasPermissions(this, AppPermissions.PERMISSIONS)) {
            // Already have permission, do the thing
            // ...
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.dialog_title_permission_storage),
                    AppPermissions.RC_PERMISSION_STORAGE, AppPermissions.PERMISSIONS);
        }
    }

    @Override
    public void onPermissionsGranted(List<String> permissions) {
        if (AppPermissions.PERMISSIONS_SET.containsAll(permissions)) {
            // Restarting application
            // Schedule start after 1 second
            /*PendingIntent pi = PendingIntent.getActivity(
                    this,
                    0,
                    new Intent(this, MainActivity.class),
                    PendingIntent.FLAG_CANCEL_CURRENT);
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pi);

            // Stop now
            finish();
            System.exit(0);*/
        } else {
            finish();
        }
    }

    @Override
    public void onPermissionsDenied(List<String> perms) {
        finish();
    }
}
