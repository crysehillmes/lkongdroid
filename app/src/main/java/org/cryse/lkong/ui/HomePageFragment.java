package org.cryse.lkong.ui;

import android.accounts.Account;
import android.animation.Animator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;

import com.afollestad.appthemeengine.util.Util;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.broadcast.BroadcastConstants;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.AccountRemovedEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.event.NoticeCountEvent;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.PunchResult;
import org.cryse.lkong.presenter.HomePagePresenter;
import org.cryse.lkong.sync.SyncUtils;
import org.cryse.lkong.ui.common.AbstractFragment;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.ui.search.SuggestionsBuilder;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.animation.LayerEnablingAnimatorListener;
import org.cryse.lkong.utils.DonateUtils;
import org.cryse.lkong.utils.snackbar.SimpleSnackbarType;
import org.cryse.lkong.view.HomePageView;
import org.cryse.utils.preference.BooleanPrefs;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;
import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import timber.log.Timber;

public class HomePageFragment extends AbstractFragment implements HomePageView {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1023;
    public static final String LOG_TAG = HomePageFragment.class.getName();
    private static final String SEARCH_FRAGMENT_TAG = "search_fragment_tag";

    AppNavigation mNavigation = new AppNavigation();

    @Inject
    HomePagePresenter mPresenter;
    @Inject
    UserAccountManager mUserAccountManager;
    BooleanPrefs mForumsFirst;

    @Bind(R.id.searchview)
    PersistentSearchView mSearchView;
    @Bind(R.id.view_search_tint)
    View mSearchTintView;
    @Bind(R.id.search_fragment_container)
    FrameLayout mSearchContainer;
    @Bind(R.id.tablayout)
    TabLayout mTabLayout;
    @Bind(R.id.fragment_homepage_viewpager)
    ViewPager mViewPager;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    protected MenuItem mChangeThemeMenuItem;
    private MenuItem mNotificationMenuItem;
    private MenuItem mPunchMenuItem;
    private MenuItem mSearchMenuItem;
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
        mForumsFirst = Prefs.getBooleanPrefs(
                PreferenceConstant.SHARED_PREFERENCE_FORUMS_FIRST,
                PreferenceConstant.SHARED_PREFERENCE_FORUMS_FIRST_VALUE
        );
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_homepage, container, false);
        ButterKnife.bind(this, contentView);
        getThemedActivity().setSupportActionBar(mToolbar);
        final ActionBar actionBar = getThemedActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setBackgroundColor(getPrimaryColor());
        if (mViewPager != null) {
            setupViewPager(mViewPager);
        }
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                AppCompatActivity activity = getAppCompatActivity();
                activity.setTitle(mViewPager.getAdapter().getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        int toolbarTextColor = Util.isColorLight(getPrimaryColor()) ? Color.BLACK : Color.WHITE;
        mTabLayout.setSelectedTabIndicatorColor(toolbarTextColor);
        Adapter adapter = (Adapter) mViewPager.getAdapter();
        for(int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = mTabLayout.getTabAt(i);
            if(tab != null) {
                tab.setIcon(adapter.getIconResId(i));
                tab.setText("");
            }
        }
        setUpSearchView();
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActivityTitle();
        getActivity().invalidateOptionsMenu();
        if (getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //Toast.makeText(getContext(), "onBackPressed", Toast.LENGTH_SHORT).show();
                        if (mSearchView.isSearching()) {
                            mSearchView.closeSearch();
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
            });
        }
    }

    public void setUpSearchView() {
        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_RECOGNITION_REQUEST_CODE);
        if (delegate.isVoiceRecognitionAvailable()) {
            mSearchView.setVoiceRecognitionDelegate(delegate);
        }
        mSearchTintView.setOnClickListener(v -> mSearchView.cancelEditing());
        mSearchView.setSuggestionBuilder(new SuggestionsBuilder(getContext()));
        mSearchView.setSearchListener(new PersistentSearchView.SearchListener() {

            @Override
            public void onSearchEditOpened() {
                //Use this to tint the screen
                mSearchTintView.setVisibility(View.VISIBLE);
                mSearchTintView
                        .animate()
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new LayerEnablingAnimatorListener(mSearchTintView))
                        .start();

            }

            @Override
            public void onSearchEditClosed() {
                mSearchTintView
                        .animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new LayerEnablingAnimatorListener(mSearchTintView) {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mSearchTintView.setVisibility(View.GONE);
                            }
                        })
                        .start();
            }

            @Override
            public boolean onSearchEditBackPressed() {
                if (mSearchView.isEditing()) {
                    mSearchView.cancelEditing();
                    return true;
                }
                return false;
            }

            @Override
            public void onSearchExit() {
                SearchFragment searchFragment = (SearchFragment) getChildFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
                if (searchFragment != null) {
                    slideOutToButtom(mSearchContainer, true, () -> {
                        //searchFragment::clearSearch
                        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                        fragmentTransaction.remove(searchFragment);
                        fragmentTransaction.commit();
                        getChildFragmentManager().executePendingTransactions();
                    });
                } else {
                    slideOutToButtom(mSearchContainer, true, null);
                }
            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String string) {
                SearchFragment searchFragment = (SearchFragment) getChildFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
                if (searchFragment == null) {

                    searchFragment = SearchFragment.newInstance("", (novelModel, position) -> {
                        //mPresenter.showNovelDetail(novelModel);
                    });
                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.search_fragment_container, searchFragment, SEARCH_FRAGMENT_TAG);
                    fragmentTransaction.commit();
                    getChildFragmentManager().executePendingTransactions();
                }
                if(searchFragment != null) {
                    if (!mSearchContainer.isShown()) {
                        final SearchFragment finalSearchFragment = searchFragment;
                        slideInToTop(mSearchContainer, true, () -> finalSearchFragment.search(string));
                    } else {
                        searchFragment.search(string);
                    }
                }
            }

            @Override
            public void onSearchCleared() {

            }

        });
    }

    @Override
    public void onResume() {
        super.onResume();
        checkNewNoticeCount();
        mPresenter.punch(mUserAccountManager.getAuthObject());
        IntentFilter checkNoticeIntentFilter = new IntentFilter(BroadcastConstants.BROADCAST_SYNC_CHECK_NOTICE_COUNT_DONE);
        checkNoticeIntentFilter.setPriority(10);
        getActivity().registerReceiver(mCheckNoticeCountDoneBroadcastReceiver, checkNoticeIntentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mCheckNoticeCountDoneBroadcastReceiver);
        super.onPause();
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
        mPresenter.destroy();
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getChildFragmentManager());
        if(mForumsFirst.get()) {
            adapter.addFragment(ForumsFragment.newInstance(null), getString(R.string.drawer_item_forum_list), R.drawable.ic_forums);
            adapter.addFragment(FollowedForumsFragment.newInstance(null), getString(R.string.drawer_item_followed_forums), R.drawable.ic_stared);
            adapter.addFragment(TimelineFragment.newInstance(null), getString(R.string.drawer_item_timeline), R.drawable.ic_timeline);
        } else {
            adapter.addFragment(TimelineFragment.newInstance(null), getString(R.string.drawer_item_timeline), R.drawable.ic_timeline);
            adapter.addFragment(FollowedForumsFragment.newInstance(null), getString(R.string.drawer_item_followed_forums), R.drawable.ic_stared);
            adapter.addFragment(ForumsFragment.newInstance(null), getString(R.string.drawer_item_forum_list), R.drawable.ic_forums);
        }
        adapter.addFragment(HotThreadFragment.newInstance(false), getString(R.string.drawer_item_hot_thread), R.drawable.ic_whatshot);
        adapter.addFragment(HotThreadFragment.newInstance(true), getString(R.string.drawer_item_digest_thread), R.drawable.ic_digest);
        viewPager.setAdapter(adapter);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home_page, menu);
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        mNotificationMenuItem = menu.findItem(R.id.action_open_notification);
        mSearchMenuItem = menu.findItem(R.id.action_open_search);
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
                if (mSearchMenuItem != null && getView() != null) {
                    View menuItemView = getView().findViewById(R.id.action_open_search);
                    mSearchView.setStartPositionFromMenuItem(menuItemView, getView().getMeasuredWidth());
                    mSearchView.openSearch();
                    return true;
                } else {
                    return false;
                }
            case R.id.action_open_notification:
                mNavigation.navigateToNotificationActivity(getActivity());
                return true;
            case R.id.action_change_theme:
                if(isNightMode() != null) {
                    toggleNightMode();
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
            case R.id.action_donate:
                DonateUtils.showDonateDialog(getActivity());
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
        } else if (event instanceof CurrentAccountChangedEvent) {
            mCurrentUserPunchResult = null;
            getThemedActivity().invalidateOptionsMenu();
            mPresenter.punch(mUserAccountManager.getAuthObject());
            checkNewNoticeCount();
        }
    }

    protected void checkNewNoticeCount() {
        if (isAdded()) {
            Account account = mUserAccountManager.getCurrentUserAccount().getAccount();
            if(account != null)
                SyncUtils.manualSync(account, SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE);
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
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
            mHasNotification = noticeCountModel.hasNotification() && noticeCountModel.getUserId() == mUserAccountManager.getCurrentUserId();
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

    private void slideInToTop(View v, boolean animated, Runnable runOnAnimationEnd) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        v.setTranslationY(metrics.heightPixels);
        v.setAlpha(0);
        if (!v.isShown())
            v.setVisibility(View.VISIBLE);
        v.animate().
                translationY(0).
                alpha(1).
                setDuration(animated ? 500 : 0).
                setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new LayerEnablingAnimatorListener(v) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (runOnAnimationEnd != null)
                            runOnAnimationEnd.run();
                    }
                });
    }

    private void slideOutToButtom(View v, boolean animated, Runnable runOnAnimationEnd) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        v.setTranslationY(0);
        v.setAlpha(1);
        v.animate().
                translationY(metrics.heightPixels).
                alpha(0).
                setDuration(animated ? 300 : 0).
                setInterpolator(new AccelerateInterpolator())
                .setListener(new LayerEnablingAnimatorListener(v) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        v.setVisibility(View.INVISIBLE);
                        if (runOnAnimationEnd != null)
                            runOnAnimationEnd.run();
                    }
                });
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        private final List<Integer> mFragmentIcons = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title, @DrawableRes int icon) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
            mFragmentIcons.add(icon);
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

        public int getIconResId(int position) {
            return mFragmentIcons.get(position);
        }
    }

    private BroadcastReceiver mCheckNoticeCountDoneBroadcastReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            // update your views
            // loadData(null, 0, false);
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
            abortBroadcast();
        }
    };
}
