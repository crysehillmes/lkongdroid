package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.common.InActivityFragment;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.utils.ColorUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificationFragment extends InActivityFragment {
    private static final String LOG_TAG = NotificationFragment.class.getName();
    int mColorAccent;
    @Inject
    UserAccountManager mUserAccountManager;
    @InjectView(R.id.toolbar)
    Toolbar mToolbar;
    @InjectView(R.id.tablayout)
    TabLayout mTabLayout;
    @InjectView(R.id.fragment_notification_viewpager)
    ViewPager mViewPager;
    NotificationFragmentPagerAdapter mHomePagerAdapter;

    public static NotificationFragment newInstance(Bundle args) {
        NotificationFragment fragment = new NotificationFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        mColorAccent = ColorUtils.getColorFromAttr(getActivity(), R.attr.colorAccent);
        Bundle args = getArguments();
        if(args != null && args.containsKey(DataContract.BUNDLE_USER_ID)) {
            long userId = args.getLong(DataContract.BUNDLE_USER_ID);
            if(mUserAccountManager.getCurrentUserId() != userId)
                mUserAccountManager.setCurrentUserAccount(userId);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notification_page, null);
        ButterKnife.inject(this, contentView);
        mTabLayout.setBackgroundColor(getThemedActivity().getThemeEngine().getPrimaryColor(getActivity()));
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewPager();
        final ActionBar actionBar = getThemedActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            getActivity().setTitle(R.string.drawer_item_notification);
        }
        mToolbar.setBackgroundColor(getPrimaryColor());
        if (mViewPager != null) {
            initViewPager();
            mTabLayout.setupWithViewPager(mViewPager);
            mTabLayout.setBackgroundColor(getPrimaryColor());
        }
    }

    private void initViewPager() {
        mHomePagerAdapter = new NotificationFragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mHomePagerAdapter);
        mHomePagerAdapter.addFragment(
                getString(R.string.drawer_item_mentions),
                MentionsFragment.newInstance(Bundle.EMPTY)
        );
        mHomePagerAdapter.addFragment(
                getString(R.string.drawer_item_notice),
                NoticeFragment.newInstance(Bundle.EMPTY)
        );
        mHomePagerAdapter.addFragment(
                getString(R.string.drawer_item_private_messages),
                NoticePrivateChatsFragment.newInstance(Bundle.EMPTY)
        );
        mHomePagerAdapter.addFragment(
                getString(R.string.drawer_item_notice_rate),
                NoticeRateFragment.newInstance(Bundle.EMPTY)
        );
        mHomePagerAdapter.notifyDataSetChanged();
        getThemedActivity().setSupportActionBar(mToolbar);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mViewPager.getCurrentItem() == 0) {
                    AbstractThemeableActivity containerActivity = (AbstractThemeableActivity) getActivity();
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    containerActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = displaymetrics.widthPixels;
                    containerActivity.getSwipeBackLayout().setEnableGesture(true);
                    containerActivity.getSwipeBackLayout().setEdgeSize(width);
                } else {
                    AbstractThemeableActivity containerActivity = (AbstractThemeableActivity) getActivity();
                    containerActivity.getSwipeBackLayout().setEnableGesture(false);
                    containerActivity.getSwipeBackLayout().setEdgeSize(0);
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_notification_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getThemedActivity().closeActivityWithTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void setActivityTitle() {
        getActivity().setTitle(getFragmentTitle());
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.drawer_item_notification);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
    }

    static class NotificationFragmentPagerAdapter extends FragmentStatePagerAdapter {
        List<Fragment> mFragments = new ArrayList<>();
        List<String> mFragmentTitles = new ArrayList<>();
        NotificationFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(String title, Fragment fragment) {
            mFragmentTitles.add(title);
            mFragments.add(fragment);
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

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if (event instanceof ThemeColorChangedEvent) {
            mTabLayout.setBackgroundColor(((ThemeColorChangedEvent) event).getNewPrimaryColor());
        }
    }
}
