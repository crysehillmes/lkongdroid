package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.application.qualifier.PrefsDefaultAccountUid;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.presenter.UserAccountPresenter;
import org.cryse.lkong.ui.navigation.NavigationDrawerAdapter;
import org.cryse.lkong.ui.common.AbstractFragment;
import org.cryse.lkong.ui.navigation.NavigationDrawerItem;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.view.UserAccountView;
import org.cryse.utils.ColorUtils;
import org.cryse.utils.preference.LongPreference;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends AbstractFragment implements UserAccountView {
    public static final String LOG_TAG = NavigationDrawerFragment.class.getName();
    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;

    @InjectView(R.id.navigation_drawer_listview)
    ListView mDrawerListView;

    @InjectView(R.id.left_drawer_account_container)
    RelativeLayout mAccountContainer;


    @InjectView(R.id.account_imageview_avatar)
    ImageView mAccountAvatarImageView;
    @InjectView(R.id.account_textview_username)
    TextView mAccountUserNameTextView;
    @InjectView(R.id.account_textview_email)
    TextView mAccountEmailTextView;

    private NavigationDrawerAdapter mDrawerAdapter;
    private View mFragmentContainerView;

    @Inject
    @PrefsDefaultAccountUid
    LongPreference mDefaultAccountUid;

    @Inject
    UserAccountPresenter mUserAccountPresenter;
    @Inject
    UserAccountManager mUserAccountManager;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    UserAccountEntity mCurrentAccount;

    /**
     * Used to post delay navigation action to improve UX
     */
    private Handler mHandler = new Handler();
    private Runnable mPendingRunnable = null;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectThis();
        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
        mHandler = new Handler();
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.

        initNavigationDrawer();
        setUpNavigationItems();

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
            // Select the last selected item.
            selectItem(mCurrentSelectedPosition, true, false);
        } else {
            // Select the default item (0).
            selectItem(mCurrentSelectedPosition, false, false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.inject(this, rootView);
        mAccountAvatarImageView.setOnClickListener(view -> {
            if(mCurrentAccount == null) {
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
            } else {
                // TODO: Go to profile view
            }
        });
        return rootView;
    }

    private void initNavigationDrawer() {
        mDrawerListView.setOnItemClickListener((parent, view, position, id) -> {
            selectItem(position, false, true);
        });
        mDrawerAdapter = new NavigationDrawerAdapter(getActivity());
        mDrawerListView.setAdapter(mDrawerAdapter);
    }

    public NavigationDrawerAdapter getNavigationAdapter() {
        return mDrawerAdapter;
    }

    private void setUpNavigationItems() {
        if(mCallbacks != null)
            mCallbacks.onInitialNavigationDrawerItems();
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void toggleDrawer() {
        if(isDrawerOpen())
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        else if(mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(mFragmentContainerView))
            mDrawerLayout.openDrawer(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = (View)getActivity().findViewById(fragmentId).getParent();
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                getActivity().invalidateOptionsMenu();
                //getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                // If mPendingRunnable is not null, then add to the message queue
                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                    mPendingRunnable = null;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }
                getActivity().invalidateOptionsMenu();
                //getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(mDrawerToggle::syncState);

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setStatusBarBackgroundColor(ColorUtils.getColorFromAttr(getActivity(), R.attr.colorPrimaryDark));
    }

    private void selectItem(int position, boolean fromSavedInstance, boolean waitForDrawerClose) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
            NavigationDrawerItem item = getNavigationAdapter().getItem(position);
            if(item.isMainItem()) {
                getNavigationAdapter().getItem(mCurrentSelectedPosition).setSelected(false);
                item.setSelected(true);

                mCurrentSelectedPosition = position;
            }
        }
        if (mCallbacks != null) {
            if(waitForDrawerClose)
                mPendingRunnable = () -> mCallbacks.onNavigationDrawerItemSelected(position, fromSavedInstance);
            else
                mCallbacks.onNavigationDrawerItemSelected(position, fromSavedInstance);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserInfo();
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserAccountPresenter().bindView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getUserAccountPresenter().unbindView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getUserAccountPresenter().destroy();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        /*if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }*/
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    /*private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(R.string.app_name);
    }*/

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public void showUserAccount(UserAccountEntity userAccount) {
        if(userAccount != null) {
            ArrayList<UserAccountEntity> accountEntities = new ArrayList<UserAccountEntity>();
            accountEntities.add(userAccount);
            mUserAccountManager.setUserAccounts(accountEntities, userAccount.getUserId());

            mCurrentAccount = userAccount;
            mAccountUserNameTextView.setText(userAccount.getUserName());
            Picasso.with(getActivity())
                    .load(userAccount.getUserAvatar())
                    .error(R.drawable.ic_default_avatar)
                    .placeholder(R.drawable.ic_default_avatar)
                    .into(mAccountAvatarImageView);
            String secondInfoText = userAccount.getEmail();
            mAccountEmailTextView.setText(secondInfoText);
        }
    }

    @Override
    public void setLoading(Boolean value) {

    }

    @Override
    public Boolean isLoading() {
        return null;
    }

    @Override
    public void showToast(int text_value, int toastType) {
        ToastProxy.showToast(getActivity(), getString(ToastErrorConstant.errorCodeToStringRes(text_value)), toastType);
    }

    public void getUserInfo() {
        // Get from local first
        // Get from web if failed.
        long uid = mDefaultAccountUid.get();
        if(uid >= 0) {
            getUserAccountPresenter().getUserAccount(uid);
        }
    }

    public UserAccountPresenter getUserAccountPresenter() {
        return mUserAccountPresenter;
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when navigation drawer need to initial items.
         */
        void onInitialNavigationDrawerItems();
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position, boolean fromSavedInstance);
    }
}
