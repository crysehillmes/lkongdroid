package org.cryse.lkong.ui;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.RequestManager;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;

import org.cryse.changelog.ChangeLogUtils;
import org.cryse.lkong.R;
import org.cryse.lkong.account.UserAccount;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.application.qualifier.PrefsCheckNoticeDuration;
import org.cryse.lkong.application.qualifier.PrefsVersionCode;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.AccountRemovedEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.event.NewAccountEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.sync.SyncUtils;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.utils.preference.IntegerPreference;
import org.cryse.utils.preference.StringPreference;

import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AbstractThemeableActivity{
    private static final String LOG_TAG = MainActivity.class.getName();
    @Inject
    AndroidNavigation mNavigation;
    @Inject
    UserAccountManager mUserAccountManager;
    @Inject
    @PrefsCheckNoticeDuration
    StringPreference mCheckNoticeDuration;
    @Inject
    @PrefsVersionCode
    IntegerPreference mVersionCodePref;

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
        //setUpToolbar(R.id.appbar, R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        setIsOverrideStatusBarColor(false);
        if(!mUserAccountManager.isSignedIn()) {
            mNavigation.navigateToSignInActivity(this, true);
            closeActivityWithTransition();
            return;
        }
        mNavigation.attachMainActivity(this);
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
        IDrawerItem[] drawerItems = new IDrawerItem[4];
        drawerItems[0] = new PrimaryDrawerItem().withName(R.string.drawer_item_homepage).withIcon(R.drawable.ic_drawer_timeline).withIdentifier(1001);
        drawerItems[1] = new PrimaryDrawerItem().withName(R.string.drawer_item_favorites).withIcon(R.drawable.ic_drawer_favorites).withIdentifier(1003);
        drawerItems[2] = new DividerDrawerItem();
        drawerItems[3] = new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIdentifier(1101).withCheckable(false);
        //Now create your drawer and pass the AccountHeader.Result
        mNaviagtionDrawer = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(mAccountHeader)
                .withStatusBarColor(getThemeEngine().getPrimaryDarkColor(this))
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
                .withOnDrawerItemClickListener((parent, view, position, id, drawerItem) -> {
                    // do something with the clicked item :D
                    if (drawerItem.getType().equalsIgnoreCase("PRIMARY_ITEM"))
                        mCurrentSelection = drawerItem.getIdentifier();
                    mPendingRunnable = () ->  onNavigationSelected(drawerItem);
                    return false;
                })
                .build();
        addAccountProfile();
        if(mCurrentSelection == 1001 && !mIsRestorePosition) {
            mNaviagtionDrawer.setSelectionByIdentifier(1001, false);
            mNavigation.navigateToHomePageFragment();
            // mNavigation.navigateToHomePageFragment();
        } else if(mIsRestorePosition) {
            mNaviagtionDrawer.setSelectionByIdentifier(mCurrentSelection, false);
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
                mNavigation.navigateToHomePageFragment();
                break;
            case 1002:
                mNavigation.navigateToForumListFragment(null);
                break;
            case 1003:
                mNavigation.navigateToFavoritesFragment(null);
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
    protected boolean hasSwipeBackLayout() {
        return false;
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
        if (event instanceof ThemeColorChangedEvent) {
            mNaviagtionDrawer.setStatusBarColor(((ThemeColorChangedEvent) event).getNewPrimaryDarkColor());
            mNaviagtionDrawer.getContent().invalidate();
            // setDrawerSelectedItemColor(((ThemeColorChangedEvent) event).getNewPrimaryColorResId());
        } else if(event instanceof NewAccountEvent) {
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
                            .withIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.drawer_account_add, getTheme()))
                            .withIdentifier(-3001)
                            .setSelectable(false)
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

    @Override
    public void closeActivityWithTransition() {
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                            if(versionCode > 0) {
                                ChangeLogUtils reader = new ChangeLogUtils(this, R.xml.changelog);

                                new MaterialDialog.Builder(this)
                                        .title(R.string.text_new_version_changes)
                                        .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
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
}
