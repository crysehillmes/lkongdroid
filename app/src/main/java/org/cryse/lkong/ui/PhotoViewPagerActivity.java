package org.cryse.lkong.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import uk.co.senab.photoview.PhotoView;

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

        mPagerAdapter = new PhotoPagerAdapter(this, mPhotoUrls);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(initPosition);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
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
        });
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).simpleActivityComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    static class PhotoPagerAdapter extends PagerAdapter {
        private Context mContext;
        private List<String> mPhotoUrls = new ArrayList<>();
        public PhotoPagerAdapter(Context context, List<String> photoUrls) {
            mContext = context;
            mPhotoUrls.addAll(photoUrls);
        }

        @Override
        public int getCount() {
            return mPhotoUrls.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            FrameLayout rootView = (FrameLayout)LayoutInflater.from(mContext).inflate(R.layout.viewpager_item_photo, container, false);
            ProgressBar progressBar = (ProgressBar) rootView.findViewById(R.id.viewpager_item_photo_progressbar);
            PhotoView photoView = (PhotoView) rootView.findViewById(R.id.viewpager_item_photo_photoview);
            Picasso.with(mContext).load(mPhotoUrls.get(position)).fit().centerInside().into(photoView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    progressBar.setVisibility(View.INVISIBLE);
                    photoView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onError() {
                    progressBar.setVisibility(View.INVISIBLE);
                    photoView.setVisibility(View.VISIBLE);
                }
            });

            // Now just add PhotoView to ViewPager and return it
            container.addView(rootView);

            return rootView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
