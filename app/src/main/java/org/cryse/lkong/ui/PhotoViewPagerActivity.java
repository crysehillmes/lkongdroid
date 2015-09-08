package org.cryse.lkong.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import org.apache.tika.Tika;
import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.SubscriptionUtils;
import org.cryse.lkong.utils.file.FileCopier;
import org.cryse.lkong.utils.snackbar.SimpleSnackbarType;
import org.cryse.lkong.utils.snackbar.SnackbarUtils;
import org.cryse.lkong.utils.snackbar.ToastErrorConstant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import butterknife.ButterKnife;
import butterknife.Bind;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PhotoViewPagerActivity extends AbstractThemeableActivity {
    private static final String LOG_TAG = PhotoViewPagerActivity.class.getName();

    private List<String> mPhotoUrls = new ArrayList<String>();
    private String mInitUrl;
    private String mImageFolderName;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.photo_viewpager)
    ViewPager mViewPager;
    PhotoPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewpager);
        setIsOverrideToolbarColor(false);
        ButterKnife.bind(this);
        setUpToolbar(mToolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(Color.BLACK);
        mImageFolderName = getString(R.string.app_name);
        Intent intent = getIntent();
        if (intent.hasExtra(DataContract.BUNDLE_POST_IMAGE_INIT_URL) && intent.hasExtra(DataContract.BUNDLE_POST_IMAGE_URL_LIST)) {
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
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTitle(mPagerAdapter.getPageTitle(position));
                if (mViewPager.getCurrentItem() == 0) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pictures_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivityWithTransition();
                return true;
            case R.id.action_save_image_as:
                savePictureAs(mPhotoUrls.get(mViewPager.getCurrentItem()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void savePictureAs(String url) {
        Future<File> fileFuture = Glide.with(this)
                .load(url)
                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        Observable.from(fileFuture).subscribeOn(Schedulers.io()).subscribe(cachedFile -> {
            String fileName = URLUtil.guessFileName(url, null, null);
            File sdPicturesFolder = new File(Environment.getExternalStorageDirectory(), "Pictures");
            if (!sdPicturesFolder.exists())
                sdPicturesFolder.mkdirs();
            File appPicturesFolder = new File(sdPicturesFolder, mImageFolderName);
            if (!appPicturesFolder.exists())
                appPicturesFolder.mkdirs();
            File targetFile = new File(appPicturesFolder, fileName);

            FileCopier.copyTo(cachedFile, targetFile, new FileCopier.CopyCallback() {
                @Override
                public void onError(Exception exception) {
                    runOnUiThread(() -> showSnackbar(ToastErrorConstant.TOAST_FAILURE_SAVE_IMAGE_AS, SimpleSnackbarType.ERROR));
                }

                @Override
                public void onComplete() {
                    runOnUiThread(() -> showSnackbar(getString(R.string.toast_success_save_image_as, mImageFolderName, fileName), SimpleSnackbarType.INFO));
                }
            });
        }, error -> {
            runOnUiThread(() -> showSnackbar(ToastErrorConstant.TOAST_FAILURE_SAVE_IMAGE_AS, SimpleSnackbarType.ERROR));
        });
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
        private static final String SUB_CACHE_DIR = "img-origin-cache";
        static Tika tika = new Tika();
        private Subscription mLoadImageSubscription;
        private String mImageUrl;
        @Bind(R.id.viewpager_item_progressbar)
        ProgressBar mProgressBar;
        @Bind(R.id.viewpager_item_imageview_secondary)
        ImageView mSecondaryPhotoView;
        @Bind(R.id.viewpager_item_imageview_primary)
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
            if (args != null && args.containsKey(ARGS_IMAGE_URL)) {
                mImageUrl = args.getString(ARGS_IMAGE_URL);
            } else {
                throw new IllegalArgumentException("Wrong args pass to ImageFragment.");
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View contentView = inflater.inflate(R.layout.viewpager_item_photo, container, false);
            ButterKnife.bind(this, contentView);
            mPhotoView.setOnImageEventListener(new SubsamplingScaleImageView.OnImageEventListener() {

                @Override
                public void onReady() {

                }

                @Override
                public void onImageLoaded() {

                }

                @Override
                public void onPreviewLoadError(Exception e) {
                    onOpenImageError();
                }

                @Override
                public void onImageLoadError(Exception e) {
                    onOpenImageError();
                }

                @Override
                public void onTileLoadError(Exception e) {

                }

                private void onOpenImageError() {
                    SnackbarUtils.makeSimple(
                            getSnackbarRootView(),
                            getString(R.string.toast_error_open_origin_image),
                            SimpleSnackbarType.ERROR,
                            Snackbar.LENGTH_SHORT
                    ).show();
                }
            });
            return contentView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            SubscriptionUtils.checkAndUnsubscribe(mLoadImageSubscription);
            Observable<File> getImageFileObservable = Observable.create(subscriber -> {
                try {
                    File file = Glide
                            .with(this).load(mImageUrl).downloadOnly(Integer.MAX_VALUE, Integer.MAX_VALUE).get();
                    subscriber.onNext(file);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            });
            mLoadImageSubscription = getImageFileObservable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                try {

                                    String mimeTypeString = tika.detect(result);
                                    if (mimeTypeString.toLowerCase().contains("gif")) {
                                        mPhotoView.setVisibility(View.GONE);
                                        mSecondaryPhotoView.setVisibility(View.VISIBLE);
                                        Glide
                                                .with(this)
                                                .load(mImageUrl)
                                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                                .into(mSecondaryPhotoView);
                                    } else {
                                        mPhotoView.setImage(ImageSource.uri(Uri.fromFile(result)));
                                    }
                                } catch (Exception ex) {
                                    Timber.e(ex, "OriginImageDownloader::downloadImage() onError().", LOG_TAG);
                                }
                            },
                            error -> {
                                Timber.e(error, "Load image onError().", LOG_TAG);
                                mProgressBar.setVisibility(View.INVISIBLE);
                                SnackbarUtils.makeSimple(
                                        getSnackbarRootView(),
                                        getString(R.string.toast_error_open_origin_image),
                                        SimpleSnackbarType.ERROR,
                                        Snackbar.LENGTH_SHORT
                                ).show();
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

        @Override
        public void onDestroyView() {
            Glide.clear(mSecondaryPhotoView);
            super.onDestroyView();
        }

        protected View getSnackbarRootView() {
            return getView();
        }
    }
}
