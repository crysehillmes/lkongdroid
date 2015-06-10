package org.cryse.lkong.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.NoticeCountEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.ui.common.AbstractFragment;
import org.cryse.lkong.ui.navigation.AndroidNavigation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HomePageFragment extends AbstractFragment {
    public static final String LOG_TAG = HomePageFragment.class.getName();
    private static final String TABLAYOUT_TAG = "TABLAYOUT_TAG";
    private Picasso mPicasso = null;

    @Inject
    AndroidNavigation mNavigation;
    @InjectView(R.id.fragment_homepage_tablayout)
    TabLayout mTabLayout;
    @InjectView(R.id.fragment_homepage_viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.fragment_homepage_toolbar)
    Toolbar mToolbar;

    protected MenuItem mChangeThemeMenuItem;
    private MenuItem mNotificationMenuItem;
    private boolean mHasNotification = false;
    public static HomePageFragment newInstance(Bundle args) {
        HomePageFragment fragment = new HomePageFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        mPicasso = new Picasso.Builder(getActivity()).executor(Executors.newSingleThreadExecutor()).build();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_homepage, container, false);
        ButterKnife.inject(this, contentView);
        getThemedActivity().setSupportActionBar(mToolbar);
        final ActionBar actionBar = getThemedActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setBackgroundColor(getPrimaryColor());
        if (mViewPager != null) {
            setupViewPager(mViewPager);
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.setBackgroundColor(getPrimaryColor());
        }
        return contentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActivityTitle();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNewNoticeCount();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(TimelineFragment.newInstance(null), getString(R.string.drawer_item_timeline));
        adapter.addFragment(PinnedForumsFragment.newInstance(null), getString(R.string.drawer_item_pinned_forums));
        viewPager.setAdapter(adapter);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_timeline, menu);
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        mNotificationMenuItem = menu.findItem(R.id.action_open_notification);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(mChangeThemeMenuItem != null) {
            if(isNightMode() == null)
                mChangeThemeMenuItem.setVisible(false);
            else if(isNightMode() != null && isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_light_theme);
            else if(isNightMode() != null && !isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_dark_theme);
        }
        if(mNotificationMenuItem != null) {
            if(mHasNotification) mNotificationMenuItem.setIcon(R.drawable.ic_action_notification_red_dot);
            else mNotificationMenuItem.setIcon(R.drawable.ic_action_notification);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_search:
                mNavigation.navigateToSearchActivity(getActivity());
                return true;
            case R.id.action_open_notification:
                mNavigation.navigateToNotificationActivity(getActivity());
                return true;
            case R.id.action_change_theme:
                if(isNightMode() != null) {
                    getThemedActivity().setNightMode(!isNightMode());
                }
                return true;
            case android.R.id.home:
                if(getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).getNavigationDrawer().openDrawer();
                    return true;
                } else {
                    return false;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void analyticsTrackEnter() {

    }

    @Override
    protected void analyticsTrackExit() {

    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof NoticeCountEvent) {
            NoticeCountModel model = ((NoticeCountEvent) event).getNoticeCount();
            if (model.getMentionNotice() != 0
                    || model.getNotice() != 0
                    || model.getRateNotice() != 0
                    /*|| model.getPrivateMessageNotice() != 0
                    || model.getFansNotice() != 0*/
                    ) {
                mHasNotification = true;
            } else {
                mHasNotification = false;
            }
            if(getActivity() != null)
                getActivity().invalidateOptionsMenu();
        } else if(event instanceof ThemeColorChangedEvent) {
            int newPrimaryColor = ((ThemeColorChangedEvent) event).getNewPrimaryColor();
            if(mTabLayout != null) mTabLayout.setBackgroundColor(newPrimaryColor);
        }
    }

    protected void checkNewNoticeCount() {
        if (isAdded()) {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).checkNewNoticeCount();
            }
        }
    }

    protected void setActivityTitle() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            mainActivity.onSectionAttached(getString(R.string.drawer_item_homepage));
        }
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
