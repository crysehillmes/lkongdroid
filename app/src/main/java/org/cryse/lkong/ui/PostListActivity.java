package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.presenter.PostListPresenter;
import org.cryse.lkong.ui.adapter.PostListAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.QuickReturnUtils;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.PostListView;
import org.cryse.lkong.widget.FloatingActionButtonEx;
import org.cryse.lkong.widget.PagerControl;
import org.cryse.utils.ColorUtils;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
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

    @Inject
    AndroidNavigation mAndroidNavigation;

    @Inject
    UserAccountManager mUserAccountManager;

    @InjectView(R.id.activity_post_list_recyclerview)
    SuperRecyclerView mPostCollectionView;
    @InjectView(R.id.fab)
    FloatingActionButtonEx mFab;
    @InjectView(R.id.activity_post_list_page_control)
    PagerControl mFooterPagerControl;

    View mTopPaddingHeaderView;
    View mBottomPaddingHeaderView;
    View mThreadIntroHeaderView;
    TextView mThreadTitleTextView;
    QuickReturnUtils mToolbarQuickReturn;
    MenuItem mFavoriteMenuItem;
    private PagerControl.OnPagerControlListener mOnPagerControlListener;

    private PostListAdapter mCollectionAdapter;

    List<PostModel> mItemList = new ArrayList<PostModel>();

    private long mThreadId = -1;
    private String mThreadSubject = "";
    private boolean mIsFavorite;
    private int mBaseTranslationY = 0;
    private String[] mPageIndicatorItems;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        ButterKnife.inject(this);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        setupPageControlListener();
        setTitle(R.string.activity_title_post_list);

        initRecyclerView();
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_THREAD_ID)) {
            mThreadId = intent.getLongExtra(DataContract.BUNDLE_THREAD_ID, -1);
        }
        if(mThreadId == -1)
            throw new IllegalStateException("PostListActivity missing extra in intent.");
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

        mTopPaddingHeaderView = getLayoutInflater().inflate(R.layout.layout_empty_recyclerview_top_padding, null);
        RecyclerView.LayoutParams topPaddingLP = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.calculateActionBarSize(this) + getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
        mTopPaddingHeaderView.setLayoutParams(topPaddingLP);
        mCollectionAdapter.addHeaderView(mTopPaddingHeaderView);

        mBottomPaddingHeaderView = getLayoutInflater().inflate(R.layout.layout_empty_recyclerview_top_padding, null);
        RecyclerView.LayoutParams bottomPaddingLP = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.calculateActionBarSize(this) + UIUtils.dp2px(this, 16f * 2));
        mBottomPaddingHeaderView.setLayoutParams(bottomPaddingLP);
        mCollectionAdapter.addFooterView(mBottomPaddingHeaderView);

        mThreadIntroHeaderView = getLayoutInflater().inflate(R.layout.layout_post_intro_header, null);
        RecyclerView.LayoutParams threadIntroHeaderLP = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mThreadIntroHeaderView.setLayoutParams(threadIntroHeaderLP);
        mThreadTitleTextView = (TextView) mThreadIntroHeaderView.findViewById(R.id.layout_post_intro_header_textview_title);
        mCollectionAdapter.addHeaderView(mThreadIntroHeaderView);

        mFooterPagerControl.setOnPagerControlListener(mOnPagerControlListener);
        mToolbarQuickReturn = new QuickReturnUtils(getToolbar(), QuickReturnUtils.ANIMATE_DIRECTION_UP);
        mPostCollectionView.getRecyclerView().setOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean dragging = false;
            int mNegativeDyAmount = 0;
            private int mAmountScrollY = 0;
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                dragging = RecyclerView.SCROLL_STATE_DRAGGING == newState;
                if((newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) && isRecyclerViewAtBottom(recyclerView)) {
                    mFooterPagerControl.show();
                    mFab.show();
                    mToolbarQuickReturn.show();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                mAmountScrollY = mAmountScrollY + dy;
                int toolbarHeight = getToolbar().getHeight();
                if (dy > 0) {
                    mNegativeDyAmount = 0;
                    if(mAmountScrollY - mBaseTranslationY - toolbarHeight > toolbarHeight) {
                        mToolbarQuickReturn.hide();
                    }
                    mFooterPagerControl.hide();
                    mFab.hide();
                } else if(dy < 0) {
                    mAmountScrollY = 0;
                    mNegativeDyAmount = mNegativeDyAmount + dy;
                    mToolbarQuickReturn.show();
                    mFooterPagerControl.show();
                    mFab.show();
                }
            }
        });

        mCollectionAdapter.setOnItemReplyClickListener((view, position) -> {
            if(mUserAccountManager.isSignedIn()) {
                PostModel postItem = mCollectionAdapter.getItem(position - mCollectionAdapter.getHeaderViewCount());
                mAndroidNavigation.openActivityForReplyToPost(this, mThreadId, postItem.getAuthor().getUserName(), postItem.getPid());
            } else {
                mAndroidNavigation.navigateToSignInActivity(this);
            }
        });
        mFab.attachToSuperRecyclerView(mPostCollectionView);
        mFab.setOnClickListener(view -> {
            if (mUserAccountManager.isSignedIn()) {
                mAndroidNavigation.openActivityForReplyToThread(this, mThreadId, mThreadSubject);
            } else {
                mAndroidNavigation.navigateToSignInActivity(this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DataContract.REQUEST_ID_NEW_POST) {
            if(data != null && data.hasExtra(DataContract.BUNDLE_THREAD_PAGE_COUNT) && data.hasExtra(DataContract.BUNDLE_THREAD_REPLY_COUNT)) {
                int newPageCount = data.getIntExtra(DataContract.BUNDLE_THREAD_PAGE_COUNT, 0);
                int newReplyCount = data.getIntExtra(DataContract.BUNDLE_THREAD_REPLY_COUNT, 0);
                if(newReplyCount > mThreadModel.getReplies())
                    mThreadModel.setReplies(newReplyCount);
                if(newPageCount > mPageCount) {
                    mPageCount = newPageCount;
                }
                if(newPageCount == mCurrentPage) {
                    getPresenter().loadPostList(mThreadId, mCurrentPage);
                }
            }
        }
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
                mIsFavorite = savedInstanceState.getBoolean(DataContract.BUNDLE_THREAD_IS_FAVORITE);
                mThreadSubject = savedInstanceState.getString(DataContract.BUNDLE_THREAD_SUBJECT);
                mThreadModel = savedInstanceState.getParcelable(DataContract.BUNDLE_THREAD_INFO_OBJECT);
                mCurrentPage = savedInstanceState.getInt(DataContract.BUNDLE_THREAD_CURRENT_PAGE);
                mPageCount = savedInstanceState.getInt(DataContract.BUNDLE_THREAD_PAGE_COUNT);
                mPageIndicatorItems = savedInstanceState.getStringArray(DataContract.BUNDLE_THREAD_PAGE_INDICATOR_ITEMS);
                ArrayList<PostModel> list = savedInstanceState.getParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE);
                // mCollectionAdapter.addAll(list);
                showPostList(mCurrentPage, list);
                updatePageIndicator();
                mThreadTitleTextView.setText(mThreadSubject);
                mCollectionAdapter.notifyItemChanged(1);
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
            outState.putBoolean(DataContract.BUNDLE_THREAD_IS_FAVORITE, mIsFavorite);
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
        mFavoriteMenuItem = menu.findItem(R.id.action_thread_favorite);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mItemList.size() == 0) mFavoriteMenuItem.setVisible(false);
        else if(mItemList.size() > 0) {
            mFavoriteMenuItem.setVisible(true);
            if(mIsFavorite)
                mFavoriteMenuItem.setTitle(R.string.action_thread_remove_favorite);
            else
                mFavoriteMenuItem.setTitle(R.string.action_thread_add_favorite);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();
                else
                    finish();
                return true;
            case R.id.action_change_theme:
                setNightMode(!isNightMode());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    private void updatePageIndicator() {
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
        mPostCollectionView.getRecyclerView().stopScroll();
        mCollectionAdapter.replaceWith(posts);
        mPostCollectionView.getRecyclerView().scrollToPosition(0);
        mToolbarQuickReturn.show();
        if(page == 1 && posts.size() > 0)
            mIsFavorite = mItemList.get(0).isFavorite();
        invalidateOptionsMenu();
    }

    @Override
    public void onLoadThreadInfoComplete(ThreadInfoModel threadInfoModel) {
        mThreadModel = threadInfoModel;
        mThreadSubject = threadInfoModel.getSubject();
        mThreadTitleTextView.setText(mThreadSubject);
        mCollectionAdapter.notifyItemChanged(1);

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

    private boolean isRecyclerViewAtBottom(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager)layoutManager;
            return (gridLayoutManager.findLastCompletelyVisibleItemPosition() == (mCollectionAdapter.getItemCount() - 1));
        } else if(layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager)layoutManager;
            return (linearLayoutManager.findLastCompletelyVisibleItemPosition() == (mCollectionAdapter.getItemCount() - 1));
        } else {
            throw new IllegalStateException();
        }
    }
}
