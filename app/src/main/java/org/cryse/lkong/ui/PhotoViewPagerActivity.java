package org.cryse.lkong.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.squareup.okhttp.OkHttpClient;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.OriginImageDownloader;
import org.cryse.lkong.utils.SubscriptionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PhotoViewPagerActivity extends AbstractThemeableActivity{
    private static final String LOG_TAG = PhotoViewPagerActivity.class.getName();

    private List<String> mPhotoUrls = new ArrayList<String>();
    private String mInitUrl;

    @InjectView(R.id.photo_viewpager)
    ViewPager mViewPager;
    PhotoPagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewpager);
        setIsOverrideToolbarColor(false);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.BLACK);
        ButterKnife.inject(this);
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_POST_IMAGE_INIT_URL) && intent.hasExtra(DataContract.BUNDLE_POST_IMAGE_URL_LIST)) {
            this.mPhotoUrls.addAll(intent.getStringArrayListExtra(DataContract.BUNDLE_POST_IMAGE_URL_LIST));
            this.mInitUrl = intent.getStringExtra(DataContract.BUNDLE_POST_IMAGE_INIT_URL);
        } else {
            throw new IllegalArgumentException("Wrong intent extras.");
        }
        int initPosition = 0;
        for (int i = 0; i < mPhotoUrls.size(); i++) {
            String url = mPhotoUrls.get(i);
            if (url.equals(mInitUrl)) {
                initPosition = i;
                break;
            }
        }
        mPagerAdapter = new PhotoPagerAdapter(getSupportFragmentManager(), mPhotoUrls);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(mPagerAdapter.getPageTitle(position));
                if(mViewPager.getCurrentItem() == 0) {
                    DisplayMetrics displaymetrics = new DisplayMetrics();
                    PhotoViewPagerActivity.this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    int width = displaymetrics.widthPixels;
                    PhotoViewPagerActivity.this.getSwipeBackLayout().setEnableGesture(true);
                    PhotoViewPagerActivity.this.getSwipeBackLayout().setEdgeSize(width);
                } else {
                    PhotoViewPagerActivity.this.getSwipeBackLayout().setEnableGesture(false);
                    PhotoViewPagerActivity.this.getSwipeBackLayout().setEdgeSize(0);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setCurrentItem(initPosition);
        setTitle(mPagerAdapter.getPageTitle(initPosition));
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).simpleActivityComponent().inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishCompat();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    static class PhotoPagerAdapter extends FragmentStatePagerAdapter {
        private List<String> mPhotoUrls = new ArrayList<>();

        public PhotoPagerAdapter(FragmentManager fm, List<String> photoUrls) {
            super(fm);
            mPhotoUrls.addAll(photoUrls);
        }

        @Override
        public int getCount() {
            return mPhotoUrls.size();
        }

        @Override
        public Fragment getItem(int position) {
            return ImageFragment.newInstance(mPhotoUrls.get(position));
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return String.format("%d / %d", position + 1, getCount());
        }
    }

    public static class ImageFragment extends Fragment {
        private static final String ARGS_IMAGE_URL = "IMAGE_URL";
        private Subscription mLoadImageSubscription;
        private String mImageUrl;
        @InjectView(R.id.viewpager_item_photo_progressbar)
        ProgressBar mProgressBar;
        @InjectView(R.id.viewpager_item_photo_photoview)
        SubsamplingScaleImageView mPhotoView;

        public static ImageFragment newInstance(String url) {
            ImageFragment fragment = new ImageFragment();
            Bundle args = new Bundle();
            args.putString(ARGS_IMAGE_URL, url);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Bundle args = getArguments();
            if(args != null && args.containsKey(ARGS_IMAGE_URL)) {
                mImageUrl = args.getString(ARGS_IMAGE_URL);
            } else {
                throw new IllegalArgumentException("Wrong args pass to ImageFragment.");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View contentView = inflater.inflate(R.layout.viewpager_item_photo, container, false);
            ButterKnife.inject(this, contentView);
            return contentView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            OkHttpClient client = new OkHttpClient();
            OriginImageDownloader originImageDownloader = new OriginImageDownloader(client, getActivity().getCacheDir(), "img-origin-cache");
            SubscriptionUtils.checkAndUnsubscribe(mLoadImageSubscription);
            mLoadImageSubscription = originImageDownloader.downloadImage(mImageUrl).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                mPhotoView.setImageUri(result);
                            },
                            error -> {
                                Timber.e(error, "OriginImageDownloader::downloadImage() onError().", LOG_TAG);
                                mProgressBar.setVisibility(View.INVISIBLE);
                            },
                            () -> {
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                    );
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            SubscriptionUtils.checkAndUnsubscribe(mLoadImageSubscription);
        }
    }
}
