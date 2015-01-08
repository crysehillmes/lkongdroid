package org.cryse.lkong.ui;

import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.util.Log;
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
        Log.d("MainActivity", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    protected void injectThis() {
        Log.d("MainActivity", "injectThis");
        LKongApplication.get(this).mainActivityComponent().inject(this);
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
            case ACTIVITY_SETTINGS:
                mNavigation.navigateToSettingsActivity();
                break;
            default:
                throw new IllegalArgumentException("Unknown NavigationDrawerItem position.");
        }
    }

    public void onSectionAttached(String title) {
        mTitle = title;
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
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
