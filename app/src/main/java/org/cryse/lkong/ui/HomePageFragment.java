package org.cryse.lkong.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.AccountRemovedEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.event.NoticeCountEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.PunchResult;
import org.cryse.lkong.presenter.HomePagePresenter;
import org.cryse.lkong.ui.common.AbstractFragment;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.snackbar.SimpleSnackbarType;
import org.cryse.lkong.view.HomePageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class HomePageFragment extends AbstractFragment implements HomePageView {
    public static final String LOG_TAG = HomePageFragment.class.getName();
    private Picasso mPicasso = null;

    @Inject
    AndroidNavigation mNavigation;
    @Inject
    HomePagePresenter mPresenter;
    @Inject
    UserAccountManager mUserAccountManager;
    @InjectView(R.id.tablayout)
    TabLayout mTabLayout;
    @InjectView(R.id.fragment_homepage_viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;

    protected MenuItem mChangeThemeMenuItem;
    private MenuItem mNotificationMenuItem;
    private MenuItem mPunchMenuItem;
    private boolean mHasNotification = false;
    private PunchResult mCurrentUserPunchResult;
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
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNewNoticeCount();
        mPresenter.punch(mUserAccountManager.getAuthObject());
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.bindView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unbindView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPicasso.shutdown();
        mPresenter.destroy();
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
        inflater.inflate(R.menu.menu_home_page, menu);
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        mNotificationMenuItem = menu.findItem(R.id.action_open_notification);
        mPunchMenuItem = menu.findItem(R.id.action_punch);
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
        if(mPunchMenuItem != null && isAdded()) {
            if(mCurrentUserPunchResult == null) {
                mPunchMenuItem.setTitle(getString(R.string.action_punch));
            } else {
                String punchString = getString(R.string.format_punchday_count, mCurrentUserPunchResult.getPunchDay());
                mPunchMenuItem.setTitle(punchString);
            }
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
            case R.id.action_punch:
                if(mCurrentUserPunchResult != null) {
                    String punchString = getString(R.string.format_punchday_count, mCurrentUserPunchResult.getPunchDay());
                    showSnackbar(
                            punchString,
                            SimpleSnackbarType.INFO,
                            SimpleSnackbarType.LENGTH_SHORT
                    );
                } else {
                    mPresenter.punch(mUserAccountManager.getAuthObject());
                    showSnackbar(
                            getString(R.string.text_punching),
                            SimpleSnackbarType.INFO,
                            SimpleSnackbarType.LENGTH_SHORT
                    );
                }
                return true;
            case R.id.action_sign_out:
                signOut();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof NoticeCountEvent) {
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
        } else if(event instanceof ThemeColorChangedEvent) {
            int newPrimaryColor = ((ThemeColorChangedEvent) event).getNewPrimaryColor();
            if(mTabLayout != null) mTabLayout.setBackgroundColor(newPrimaryColor);
        } else if (event instanceof CurrentAccountChangedEvent) {
            mCurrentUserPunchResult = null;
            getThemedActivity().invalidateOptionsMenu();
            mPresenter.punch(mUserAccountManager.getAuthObject());
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

    @Override
    public void onPunchUserComplete(PunchResult punchResult) {
        mCurrentUserPunchResult = punchResult;
        getThemedActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCheckNoticeCountComplete(NoticeCountModel noticeCountModel) {
        if(noticeCountModel != null) {
            mHasNotification = noticeCountModel.hasNotification();
            if(getActivity() != null)
                getActivity().invalidateOptionsMenu();
        }
    }

    private void signOut() {
        long currentUid = mUserAccountManager.getCurrentUserId();
        try {
            mUserAccountManager.signOut(currentUid);
            getEventBus().sendEvent(new AccountRemovedEvent());
        } catch (NeedSignInException ex) {
            mNavigation.navigateToSignInActivity(getActivity(), true);
            Activity parentActivity = getActivity();
            if(parentActivity instanceof MainActivity)
                ((MainActivity)parentActivity).closeActivityWithTransition();
        } catch (Exception e) {
            Timber.e(e, e.getMessage(), LOG_TAG);
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
