package org.cryse.lkong.ui;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.NavigationDrawerItem;
import org.cryse.lkong.ui.navigation.NavigationType;

import javax.inject.Inject;


public class MainActivity extends AbstractThemeableActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    @Inject
    AndroidNavigation mNavigation;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        mNavigation.attachMainActivity(this);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).mainActivityComponent().inject(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mNavigation.detachMainActivity();
    }

    @Override
    public void onInitialNavigationDrawerItems() {
        mNavigationDrawerFragment.getNavigationAdapter().addItem(
                new NavigationDrawerItem(
                        getString(R.string.drawer_item_forum_list),
                        NavigationType.FRAGMENT_FORUM_LIST,
                        R.drawable.ic_drawer_forum_list,
                        true,
                        true
                )
        );
        mNavigationDrawerFragment.getNavigationAdapter().addItem(
                new NavigationDrawerItem(
                        getString(R.string.drawer_item_favorites),
                        NavigationType.FRAGMENT_FAVORITES,
                        R.drawable.ic_drawer_favorites,
                        true,
                        true
                )
        );
        mNavigationDrawerFragment.getNavigationAdapter().addItem(
                new NavigationDrawerItem(
                        getString(R.string.drawer_item_timeline),
                        NavigationType.FRAGMENT_TIMELINE,
                        R.drawable.ic_drawer_timeline,
                        true,
                        true
                )
        );
        mNavigationDrawerFragment.getNavigationAdapter().addItem(
                new NavigationDrawerItem(
                        getString(R.string.drawer_item_at_me),
                        NavigationType.FRAGMENT_AT_ME_MESSAGES,
                        R.drawable.ic_drawer_message,
                        true,
                        true
                )
        );
        mNavigationDrawerFragment.getNavigationAdapter().addItem(
                new NavigationDrawerItem(
                        getString(R.string.drawer_item_settings),
                        NavigationType.ACTIVITY_SETTINGS,
                        R.drawable.ic_drawer_settings,
                        false,
                        false
                )
        );
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, boolean fromSavedInstance) {
        if(fromSavedInstance) return;
        NavigationDrawerItem item = mNavigationDrawerFragment.getNavigationAdapter().getItem(position);
        switch (item.getNavigationType()) {
            case FRAGMENT_FORUM_LIST:
                mNavigation.navigateToForumListFragment(null);
                break;
            case FRAGMENT_FAVORITES:
                mNavigation.navigateToFavoritesFragment(null);
                break;
            case FRAGMENT_TIMELINE:
                mNavigation.navigateToTimelineFragment();
                break;
            case FRAGMENT_AT_ME_MESSAGES:
                mNavigation.navigateToAtMeMessagesFragment();
                break;
            case ACTIVITY_SETTINGS:
                mNavigation.navigateToSettingsActivity();
                break;
            default:
                throw new IllegalArgumentException("Unknown NavigationDrawerItem position.");
        }
    }

    public void onSectionAttached(String title) {
        mTitle = title;
        setTitle(mTitle);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mNavigationDrawerFragment.toggleDrawer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
