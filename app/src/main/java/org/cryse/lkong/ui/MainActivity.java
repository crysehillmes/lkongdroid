package org.cryse.lkong.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.service.CheckNoticeService;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.CircleTransform;
import org.cryse.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import br.liveo.interfaces.NavigationLiveoListener;

public class MainActivity extends AbstractMainActivity implements NavigationLiveoListener {
    private static final String LOG_TAG = MainActivity.class.getName();
    Picasso mPicasso;
    @Inject
    AndroidNavigation mNavigation;
    @Inject
    UserAccountManager mUserAccountManager;

    public List<String> mListNameItem;
    UserAccountEntity mCurrentAccount = null;

    ServiceConnection mBackgroundServiceConnection;
    private CheckNoticeService.CheckNoticeCountServiceBinder mCheckNoticeServiceBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        if(!mUserAccountManager.isSignedIn()) {
            mNavigation.navigateToSignInActivity(this);
            finishCompat();
        }
        setIsOverrideStatusBarColor(false);
        mNavigation.attachMainActivity(this);
        super.onCreate(savedInstanceState);
        setDrawerLayoutBackground(isNightMode());
        getDrawerLayout().setStatusBarBackgroundColor(getThemeEngine().getPrimaryDarkColor(this));
        getSwipeBackLayout().setEnableGesture(false);
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

    @Override
    public void onUserInformation() {
        //User information here
        /*this.mUserName.setText("Rudson Lima");
        this.mUserEmail.setText("rudsonlive@gmail.com");
        this.mUserPhoto.setImageResource(R.drawable.ic_rudsonlive);*/
        this.mUserBackground.setImageResource(isNightMode() ? R.drawable.drawer_top_image_dark : R.drawable.drawer_top_image_light);
        if(mUserAccountManager.isSignedIn()) {
            mCurrentAccount = mUserAccountManager.getCurrentUserAccount();
            if(mCurrentAccount != null) {
                this.mUserName.setText(mCurrentAccount.getUserName());
                mPicasso
                        .load(mCurrentAccount.getUserAvatar())
                        .error(R.drawable.ic_default_avatar)
                        .placeholder(R.drawable.ic_default_avatar)
                        .resizeDimen(R.dimen.size_avatar_large, R.dimen.size_avatar_large)
                        .transform(new CircleTransform())
                        .into(this.mUserPhoto);
                String secondInfoText = mCurrentAccount.getEmail();
                this.mUserEmail.setText(secondInfoText);
            }
        } else {
            mNavigation.navigateToSignInActivity(this);
            finishCompat();
        }
    }

    @Override
    public void onInt(Bundle savedInstanceState) {
        //Creation of the list items is here

        // set listener {required}
        this.setNavigationListener(this);

        //First item of the position selected from the list
        this.setDefaultStartPositionNavigation(0);

        // name of the list items
        mListNameItem = new ArrayList<>();
        mListNameItem.add(0, getString(R.string.drawer_item_timeline));
        mListNameItem.add(1, getString(R.string.drawer_item_forum_list));
        mListNameItem.add(2, getString(R.string.drawer_item_favorites));
        /*mListNameItem.add(3, getString(R.string.drafts));
        mListNameItem.add(4, getString(R.string.more_markers)); //This item will be a subHeader
        mListNameItem.add(5, getString(R.string.trash));
        mListNameItem.add(6, getString(R.string.spam));*/

        // icons list items
        List<Integer> mListIconItem = new ArrayList<>();
        mListIconItem.add(0, R.drawable.ic_drawer_timeline);
        mListIconItem.add(1, R.drawable.ic_drawer_forum_list); //Item no icon set 0
        mListIconItem.add(2, R.drawable.ic_drawer_favorites); //Item no icon set 0
        /*mListIconItem.add(3, R.drawable.ic_drafts_black_24dp);
        mListIconItem.add(4, 0); //When the item is a subHeader the value of the icon 0
        mListIconItem.add(5, R.drawable.ic_delete_black_24dp);
        mListIconItem.add(6, R.drawable.ic_report_black_24dp);*/

        //{optional} - Among the names there is some subheader, you must indicate it here
        List<Integer> mListHeaderItem = new ArrayList<>();
        //mListHeaderItem.add(4);

        //{optional} - Among the names there is any item counter, you must indicate it (position) and the value here
        SparseIntArray mSparseCounterItem = new SparseIntArray(); //indicate all items that have a counter
        /*mSparseCounterItem.put(0, 7);
        mSparseCounterItem.put(1, 123);
        mSparseCounterItem.put(6, 250);*/

        //If not please use the FooterDrawer use the setFooterVisible(boolean visible) method with value false
        this.setFooterInformationDrawer(R.string.drawer_item_settings, R.drawable.ic_drawer_settings);

        this.setNavigationAdapter(mListNameItem, mListIconItem, mListHeaderItem, mSparseCounterItem);
    }

    @Override
    public String getLogTag() {
        return LOG_TAG;
    }

    @Override
    public int getToolbarLayoutId() {
        return R.id.my_awesome_toolbar;
    }

    @Override
    public int getToolbarCustomShadowLayoutId() {
        return R.id.toolbar_shadow;
    }

    @Override
    public void onItemClickNavigation(int position, int layoutContainerId) {
        switch (position) {
            case 0:
                mNavigation.navigateToTimelineFragment();
                break;
            case 1:
                mNavigation.navigateToForumListFragment(null);
                break;
            case 2:
                mNavigation.navigateToFavoritesFragment(null);
                break;
            default:
                throw new IllegalArgumentException("Unknown NavigationDrawerItem position.");
        }
    }

    @Override
    public void onPrepareOptionsMenuNavigation(Menu menu, int position, boolean visible) {
        /*
        //hide the menu when the navigation is opens
        switch (position) {
            case 0:
                menu.findItem(R.id.menu_add).setVisible(!visible);
                menu.findItem(R.id.menu_search).setVisible(!visible);
                break;

            case 1:
                menu.findItem(R.id.menu_add).setVisible(!visible);
                menu.findItem(R.id.menu_search).setVisible(!visible);
                break;
        }*/
    }

    @Override
    public void onClickUserPhotoNavigation(View v) {
        // user photo onClick
        // Toast.makeText(this, R.string.open_user_profile, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClickFooterItemNavigation(View v) {
        // footer onClick
        // startActivity(new Intent(this, SettingsActivity.class));
        mNavigation.navigateToSettingsActivity(this);
    }

    public void onSectionAttached(String title) {
        setTitle(title);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if (event instanceof ThemeColorChangedEvent) {
            getDrawerLayout().setStatusBarBackgroundColor(((ThemeColorChangedEvent) event).getNewPrimaryDarkColor());
            setDrawerSelectedItemColor(((ThemeColorChangedEvent) event).getNewPrimaryColorResId());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unbindService(mBackgroundServiceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this.getApplicationContext(), CheckNoticeService.class);
        this.bindService(service, mBackgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPicasso.shutdown();
    }

    public void checkNewNoticeCount() {
        if(mCheckNoticeServiceBinder != null && mCheckNoticeServiceBinder.isBinderAlive())
            mCheckNoticeServiceBinder.checkNoticeCount(mUserAccountManager.getAuthObject());
    }
}
