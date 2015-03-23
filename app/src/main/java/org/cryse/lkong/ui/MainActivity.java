package org.cryse.lkong.ui;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.service.CheckNoticeService;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.htmltextview.AsyncTargetDrawable;

import java.util.concurrent.Executors;

import javax.inject.Inject;

public class MainActivity extends AbstractThemeableActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    @Inject
    AndroidNavigation mNavigation;
    @Inject
    UserAccountManager mUserAccountManager;

    AccountHeader.Result mAccountHeader;
    Drawer.Result mNaviagtionDrawer;

    Picasso mPicasso;
    UserAccountEntity mCurrentAccount = null;

    ServiceConnection mBackgroundServiceConnection;
    private CheckNoticeService.CheckNoticeCountServiceBinder mCheckNoticeServiceBinder;

    int mCurrentSelection = 0;
    boolean mIsRestorePosition = false;

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
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        if (!mUserAccountManager.isSignedIn()) {
            mNavigation.navigateToSignInActivity(this);
            finishCompat();
        }
        setIsOverrideStatusBarColor(false);
        mNavigation.attachMainActivity(this);
        /*setDrawerLayoutBackground(isNightMode());
        getDrawerLayout().setStatusBarBackgroundColor(getThemeEngine().getPrimaryDarkColor(this));*/
        getSwipeBackLayout().setEnableGesture(false);
        if(savedInstanceState!=null && savedInstanceState.containsKey("selection_item_position")) {
            mCurrentSelection = savedInstanceState.getInt("selection_item_position");
            mIsRestorePosition = true;
        } else {
            mCurrentSelection = 1001;
            mIsRestorePosition = false;
        }
        initDrawer();
        mBackgroundServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mCheckNoticeServiceBinder = (CheckNoticeService.CheckNoticeCountServiceBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mCheckNoticeServiceBinder = null;
            }
        };
    }

    private void initDrawer() {
        mCurrentAccount = mUserAccountManager.getCurrentUserAccount();
        // Create the AccountHeader
        mAccountHeader = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(isNightMode() ? R.drawable.drawer_top_image_dark : R.drawable.drawer_top_image_light)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withName(mCurrentAccount.getUserName())
                                .withEmail(mCurrentAccount.getEmail())
                                .withIcon(getResources().getDrawable(R.drawable.ic_default_avatar)),
                        new ProfileDrawerItem()
                                .withName("Add profile")
                        .setSelectable(false)
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public void onProfileChanged(View view, IProfile profile) {
                    }
                })
                .withCurrentProfileHiddenInList(true)
                .build();

        //Now create your drawer and pass the AccountHeader.Result
        mNaviagtionDrawer = new Drawer()
                .withActivity(this)
                .withToolbar(getToolbar())
                .withAccountHeader(mAccountHeader)
                .withStatusBarColor(getThemeEngine().getPrimaryDarkColor(this))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_timeline).withIcon(R.drawable.ic_drawer_timeline).withIdentifier(1001),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_forum_list).withIcon(R.drawable.ic_drawer_forum_list).withIdentifier(1002),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_favorites).withIcon(R.drawable.ic_drawer_favorites).withIdentifier(1003),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_settings).withIdentifier(1101).withCheckable(false)

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
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (drawerItem.getType().equalsIgnoreCase("PRIMARY_ITEM"))
                            mCurrentSelection = drawerItem.getIdentifier();
                        mPendingRunnable = () ->  onNavigationSelected(drawerItem);
                    }
                })
                .build();
        if(mCurrentSelection == 1001 && !mIsRestorePosition) {
            mNaviagtionDrawer.setSelectionByIdentifier(1001, false);
            mNavigation.navigateToTimelineFragment();
        } else if(mIsRestorePosition) {
            mNaviagtionDrawer.setSelectionByIdentifier(mCurrentSelection, false);
        }

    }

    private void onNavigationSelected(IDrawerItem drawerItem) {
        switch (drawerItem.getIdentifier()) {
            case 1001:
                mNavigation.navigateToTimelineFragment();
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

    public void checkNewNoticeCount() {
        if (mCheckNoticeServiceBinder != null && mCheckNoticeServiceBinder.isBinderAlive())
            mCheckNoticeServiceBinder.checkNoticeCount(mUserAccountManager.getAuthObject());
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if (event instanceof ThemeColorChangedEvent) {
            mNaviagtionDrawer.setStatusBarColor(((ThemeColorChangedEvent) event).getNewPrimaryDarkColor());
            mNaviagtionDrawer.getContent().invalidate();
            // setDrawerSelectedItemColor(((ThemeColorChangedEvent) event).getNewPrimaryColorResId());
        }
    }
}
