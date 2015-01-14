package org.cryse.lkong.ui;

import android.content.Intent;
import android.graphics.Outline;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.presenter.PostListPresenter;
import org.cryse.lkong.ui.adapter.PostListAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.PostListView;
import org.cryse.lkong.widget.PagerControl;
import org.cryse.utils.ColorUtils;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostListActivity extends AbstractThemeableActivity implements PostListView{
    private int mCurrentPage = -1;
    private int mPageCount = 0;
    private ThreadInfoModel mThreadModel;
    @Inject
    PostListPresenter mPresenter;

    @InjectView(R.id.activity_post_list_recyclerview)
    SuperRecyclerView mPostCollectionView;
    @InjectView(R.id.activity_post_list_header_container)
    LinearLayout mHeaderView;

    View mRecyclerTopPaddingHeaderView;
    PagerControl mHeaderPagerControl;
    PagerControl mFooterPagerControl;
    private PagerControl.OnPagerControlListener mOnPagerControlListener;

    private PostListAdapter mCollectionAdapter;

    List<PostModel> mItemList = new ArrayList<PostModel>();

    private long mThreadId = -1;
    private String mThreadSubject = "";

    private int mBaseTranslationY;
    private String[] mPageIndicatorItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        ButterKnife.inject(this);
        /*getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        setupPageControlListener();
        initRecyclerView();
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_THREAD_ID)) {
            mThreadId = intent.getLongExtra(DataContract.BUNDLE_THREAD_ID, -1);
        }
        if(mThreadId == -1)
            throw new IllegalStateException("PostListActivity missing extra in intent.");
        setTitle(mThreadSubject);
    }

    private void initRecyclerView() {
        /*UIUtils.InsetsValue insetsValue = UIUtils.getInsets(this, mPostCollectionView.getRecyclerView(), true);
        mPostCollectionView.getRecyclerView().setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());*/
        mPostCollectionView.getSwipeToRefresh().setProgressViewEndTarget(
                true,
                UIUtils.calculateActionBarSize(this) * 3);

        mPostCollectionView.setItemAnimator(new DefaultItemAnimator());
        mPostCollectionView.setLayoutManager(new LinearLayoutManager(this));
        mCollectionAdapter = new PostListAdapter(this, mItemList);
        mPostCollectionView.setAdapter(mCollectionAdapter);

        mRecyclerTopPaddingHeaderView = getLayoutInflater().inflate(R.layout.layout_empty_recyclerview_top_padding, null);
        RecyclerView.LayoutParams topPaddingLP = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.calculateActionBarSize(this) * 2);
        mRecyclerTopPaddingHeaderView.setLayoutParams(topPaddingLP);
        mCollectionAdapter.addHeaderView(mRecyclerTopPaddingHeaderView);

        mHeaderPagerControl = (PagerControl)getLayoutInflater().inflate(R.layout.widget_pager_control, null);
        RecyclerView.LayoutParams layoutParams1 = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHeaderPagerControl.setLayoutParams(layoutParams1);
        mHeaderPagerControl.setOnPagerControlListener(mOnPagerControlListener);
        mHeaderView.addView(mHeaderPagerControl);


        mFooterPagerControl = (PagerControl)getLayoutInflater().inflate(R.layout.widget_pager_control, null);
        RecyclerView.LayoutParams layoutParams2 = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mFooterPagerControl.setLayoutParams(layoutParams2);
        mFooterPagerControl.setOnPagerControlListener(mOnPagerControlListener);
        mCollectionAdapter.addFooterView(mFooterPagerControl);
        mFooterPagerControl.setVisibility(View.INVISIBLE);

        mPostCollectionView.getRecyclerView().setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
                int toolbarHeight = getToolbar().getHeight();
                if (dragging || scrollY < toolbarHeight) {
                    if (firstScroll) {
                        float currentHeaderTranslationY = mHeaderView.getTranslationY();
                        if (-toolbarHeight < currentHeaderTranslationY && toolbarHeight < scrollY) {
                            mBaseTranslationY = scrollY;
                        }
                    }
                    float headerTranslationY = ScrollUtils.getFloat(-(scrollY - mBaseTranslationY), -toolbarHeight, 0);
                    mHeaderView.animate().cancel();
                    mHeaderView.setTranslationY(headerTranslationY);
                }
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ScrollState scrollState) {
                mBaseTranslationY = 0;

                float headerTranslationY = mHeaderView.getTranslationY();
                int toolbarHeight = getToolbar().getHeight();
                if (scrollState == ScrollState.UP) {
                    if (toolbarHeight < mPostCollectionView.getRecyclerView().getCurrentScrollY()) {
                        if (headerTranslationY != -toolbarHeight) {
                            mHeaderView.animate().cancel();
                            mHeaderView.animate().translationY(-toolbarHeight).setDuration(200).start();
                        }
                    }
                } else if (scrollState == ScrollState.DOWN) {
                    if (toolbarHeight < mPostCollectionView.getRecyclerView().getCurrentScrollY()) {
                        if (headerTranslationY != 0) {
                            mHeaderView.animate().cancel();
                            mHeaderView.animate().translationY(0).setDuration(200).start();
                        }
                    }
                }
            }
        });
    }

    private void setupPageControlListener() {
        mOnPagerControlListener = new PagerControl.OnPagerControlListener() {
            @Override
            public void onBackwardClick() {
                if(mCurrentPage - 1 >= 1 && mCurrentPage - 1 <= mPageCount)
                    getPresenter().loadPostList(mThreadId, mCurrentPage - 1);
            }

            @Override
            public void onPageIndicatorClick() {MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(PostListActivity.this)
                        .title(R.string.dialog_post_list_choose_page)
                        .items(mPageIndicatorItems)
                        .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                        .itemsCallbackSingleChoice(mCurrentPage - 1, (materialDialog, view, i, charSequence) -> {
                            if(i + 1 == mCurrentPage) return;
                            getPresenter().loadPostList(mThreadId, i + 1);
                        });
                MaterialDialog dialog = dialogBuilder.build();
                dialog.show();
            }

            @Override
            public void onForwardClick() {
                if(mCurrentPage + 1 >= 1 && mCurrentPage + 1 <= mPageCount)
                    getPresenter().loadPostList(mThreadId, mCurrentPage + 1);
            }
        };
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(DataContract.BUNDLE_CONTENT_LIST_STORE)) {
            mThreadId = savedInstanceState.getLong(DataContract.BUNDLE_THREAD_ID);
            if(savedInstanceState.containsKey(DataContract.BUNDLE_THREAD_INFO_OBJECT)) {
                mThreadSubject = savedInstanceState.getString(DataContract.BUNDLE_THREAD_SUBJECT);
                mThreadModel = savedInstanceState.getParcelable(DataContract.BUNDLE_THREAD_INFO_OBJECT);
                mCurrentPage = savedInstanceState.getInt(DataContract.BUNDLE_THREAD_CURRENT_PAGE);
                mPageCount = savedInstanceState.getInt(DataContract.BUNDLE_THREAD_PAGE_COUNT);
                mPageIndicatorItems = savedInstanceState.getStringArray(DataContract.BUNDLE_THREAD_PAGE_INDICATOR_ITEMS);
                ArrayList<PostModel> list = savedInstanceState.getParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE);
                mCollectionAdapter.addAll(list);
                setTitle(mThreadSubject);
                updatePageIndicator();
            }
        } else {
            mPostCollectionView.getSwipeToRefresh().measure(1,1);
            mPostCollectionView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().loadThreadInfo(mThreadId);
            // getPresenter().loadThreadList(mForumId, mCurrentListType, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DataContract.BUNDLE_THREAD_ID, mThreadId);
        if(mThreadModel != null) {
            outState.putString(DataContract.BUNDLE_THREAD_SUBJECT, mThreadSubject);
            outState.putParcelable(DataContract.BUNDLE_THREAD_INFO_OBJECT, mThreadModel);
            outState.putInt(DataContract.BUNDLE_THREAD_CURRENT_PAGE, mCurrentPage);
            outState.putInt(DataContract.BUNDLE_THREAD_PAGE_COUNT, mPageCount);
            outState.putStringArray(DataContract.BUNDLE_THREAD_PAGE_INDICATOR_ITEMS, mPageIndicatorItems);
            outState.putParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE, mCollectionAdapter.getItemArrayList());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_theme:
                setNightMode(!isNightMode());
                return true;
            /*case android.R.id.home:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();
                else
                    finish();
                return true;*/
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    private void updatePageIndicator() {
        this.mHeaderPagerControl.setPageIndicatorText(getString(R.string.format_post_list_page_indicator, mCurrentPage, mPageCount));
        this.mFooterPagerControl.setPageIndicatorText(getString(R.string.format_post_list_page_indicator, mCurrentPage, mPageCount));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().unbindView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
    }

    @Override
    public void showPostList(int page, List<PostModel> posts) {
        this.mCurrentPage = page;
        updatePageIndicator();
        mCollectionAdapter.replaceWith(posts);
        mPostCollectionView.getRecyclerView().smoothScrollToPosition(0);
        mFooterPagerControl.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadThreadInfoComplete(ThreadInfoModel threadInfoModel) {
        mThreadModel = threadInfoModel;
        mThreadSubject = threadInfoModel.getSubject();
        setTitle(mThreadSubject);

        // Calculate page here.
        int replyCount = mThreadModel.getReplies();
        mPageCount = replyCount == 0 ? 1 : (int)Math.ceil((double) replyCount / 20d);

        mPageIndicatorItems = new String[mPageCount];
        for(int i = 1; i <= mPageCount; i++) {
            mPageIndicatorItems[i - 1] = getString(R.string.format_post_list_page_indicator_detail, i, (i - 1) * 20 + 1, i * 20);
        }

        if(mPageCount > 0)
            getPresenter().loadPostList(mThreadId, 1);
    }

    @Override
    public void setLoading(Boolean value) {
        this.mPostCollectionView.getSwipeToRefresh().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return null;
    }

    @Override
    public void showToast(int text_value, int toastType) {
        ToastProxy.showToast(this, getString(text_value), toastType);
    }

    public PostListPresenter getPresenter() {
        return mPresenter;
    }
}
