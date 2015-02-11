package org.cryse.lkong.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.logic.TimelineListType;
import org.cryse.lkong.ui.common.MainActivityFragment;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.utils.ColorUtils;
import org.cryse.widget.slidingtabs.SlidingTabLayout;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NotificationFragment extends MainActivityFragment {
    private static final String LOG_TAG = NotificationFragment.class.getName();
    int mColorAccent;
    @InjectView(R.id.fragment_notification_sliding_tabs)
    SlidingTabLayout mTabLayout;
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
        super.onCreate(savedInstanceState);
        injectThis();
        mColorAccent = ColorUtils.getColorFromAttr(getActivity(), R.attr.colorAccent);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_notification_page, null);
        ButterKnife.inject(this, contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewPager();
        mTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return mColorAccent;
            }

            @Override
            public int getDividerColor(int position) {
                return mColorAccent;
            }

        });
    }

    private void initViewPager() {
        mHomePagerAdapter = new NotificationFragmentPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mHomePagerAdapter);
        mTabLayout.setTextColor(getResources().getColor(R.color.text_color_secondary_dark));
        mTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
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

    class NotificationFragmentPagerAdapter extends FragmentStatePagerAdapter {

        NotificationFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = null;
            Bundle args = new Bundle();
            switch (i) {
                case 0:
                    args.putInt(TimelineFragment.BUNDLE_LIST_TYPE, TimelineListType.TYPE_MENTIONS);
                    args.putBoolean(TimelineFragment.BUNDLE_IN_MAIN_ACTIVITY, false);
                    fragment = TimelineFragment.newInstance(args);
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            switch (position) {
                case 0:
                    title = getString(R.string.drawer_item_at_me);
                    break;
            }
            return title;
        }

    }
}
