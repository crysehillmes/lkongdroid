package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.application.qualifier.PrefsImageDownloadPolicy;
import org.cryse.lkong.event.NewPostDoneEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.presenter.PostListPresenter;
import org.cryse.lkong.ui.adapter.PostListAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AndroidNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.QuickReturnUtils;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.PostListView;
import org.cryse.lkong.widget.FloatingActionButtonEx;
import org.cryse.lkong.widget.PagerControl;
import org.cryse.utils.ColorUtils;
import org.cryse.utils.preference.StringPreference;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class PostListActivity extends AbstractThemeableActivity implements PostListView {
    public static final String LOG_TAG = PostListActivity.class.getName();
    private int mCurrentPage = -1;
    private int mPageCount = 0;
    private ThreadInfoModel mThreadModel;
    @Inject
    PostListPresenter mPresenter;

    @Inject
    AndroidNavigation mAndroidNavigation;

    @Inject
    UserAccountManager mUserAccountManager;

    @Inject
    RxEventBus mEventBus;

    @Inject
    @PrefsImageDownloadPolicy
    StringPreference mImageDownloadPolicy;

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
    private long mTargetPostId = -1;
    private int mTargetOrdinal = -1;
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
        } else if(intent.hasExtra(DataContract.BUNDLE_POST_ID)) {
            mTargetPostId = intent.getLongExtra(DataContract.BUNDLE_POST_ID, -1);
        }
        if(mThreadId == -1 && mTargetPostId == -1)
            throw new IllegalStateException("PostListActivity missing extra in intent.");
    }

    private void initRecyclerView() {
        /*UIUtils.InsetsValue insetsValue = UIUtils.getInsets(this, mPostCollectionView.getRecyclerView(), true);
        mPostCollectionView.getRecyclerView().setPadding(insetsValue.getLeft(), insetsValue.getTop(), insetsValue.getRight(), insetsValue.getBottom());*/
        mPostCollectionView.getSwipeToRefresh().setProgressViewEndTarget(
                true,
                UIUtils.calculateActionBarSize(this) * 3);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;

        mPostCollectionView.setItemAnimator(new DefaultItemAnimator());
        mPostCollectionView.setLayoutManager(new LinearLayoutManager(this));
        mCollectionAdapter = new PostListAdapter(this, mItemList, Integer.valueOf(mImageDownloadPolicy.get()), (width * 4 / 5));
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
                    if(Math.abs(mNegativeDyAmount - mBaseTranslationY) > toolbarHeight) {
                        mToolbarQuickReturn.show();
                        mFooterPagerControl.show();
                        mFab.show();
                    }
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

    private void setupPageControlListener() {
        mOnPagerControlListener = new PagerControl.OnPagerControlListener() {
            @Override
            public void onBackwardClick() {
                if(mCurrentPage - 1 >= 1 && mCurrentPage - 1 <= mPageCount)
                    getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, mCurrentPage - 1, true);
            }

            @Override
            public void onPageIndicatorClick() {MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(PostListActivity.this)
                        .title(R.string.dialog_post_list_choose_page)
                        .items(mPageIndicatorItems)
                        .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                        .itemsCallbackSingleChoice(mCurrentPage - 1, (materialDialog, view, i, charSequence) -> {
                            if(i + 1 == mCurrentPage) return;
                            getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, i + 1, true);
                        });
                MaterialDialog dialog = dialogBuilder.build();
                dialog.show();
            }

            @Override
            public void onForwardClick() {
                if(mCurrentPage + 1 >= 1 && mCurrentPage + 1 <= mPageCount)
                    getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, mCurrentPage + 1, true);
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
                setThreadSubjectSpanned(mThreadModel);
                showPostList(mCurrentPage, list, false);

                // Restore last state for checked position.
                final int firstVisibleItemPosition = savedInstanceState.getInt("listview_index", -1);
                final int firstVisibleItemTop = savedInstanceState.getInt("listview_top", 0);

                mPostCollectionView.getRecyclerView().post(() -> {
                    if (firstVisibleItemPosition != -1) {
                        RecyclerView.LayoutManager layoutManager = mPostCollectionView.getRecyclerView().getLayoutManager();
                        if (layoutManager instanceof GridLayoutManager) {
                            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                            gridLayoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, firstVisibleItemTop);
                        } else if (layoutManager instanceof LinearLayoutManager) {
                            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                            linearLayoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, firstVisibleItemTop);
                        } else {
                            throw new IllegalStateException();
                        }
                        mPostCollectionView.getRecyclerView().stopScroll();
                    }
                });

            }
        } else {
            mPostCollectionView.getSwipeToRefresh().measure(1,1);
            mPostCollectionView.getSwipeToRefresh().setRefreshing(true);
            if(mThreadId != -1) {
                getPresenter().loadThreadInfo(mUserAccountManager.getAuthObject(), mThreadId);
            } else if(mTargetPostId != -1) {
                getPresenter().getPostLocation(mUserAccountManager.getAuthObject(), mTargetPostId);
            }
            // getPresenter().loadThreadList(mForumId, mCurrentListType, false);
        }
        mEventBus.toObservable().subscribe(event -> {
            if (event instanceof NewPostDoneEvent) {
                NewPostDoneEvent doneEvent = (NewPostDoneEvent) event;
                long tid = doneEvent.getPostResult().getTid();
                if (tid == mThreadId) {
                    int newReplyCount = doneEvent.getPostResult().getReplyCount(); // 这里楼主本身的一楼是被计算了的
                    if (newReplyCount > mThreadModel.getReplies())
                        mThreadModel.setReplies(newReplyCount);
                    int newPageCount = newReplyCount == 0 ? 1 : (int) Math.ceil((double) newReplyCount / 20d);
                    if (newPageCount > mPageCount) {
                        mPageCount = newPageCount;
                        mPageIndicatorItems = new String[mPageCount];
                        for (int i = 1; i <= mPageCount; i++) {
                            mPageIndicatorItems[i - 1] = getString(R.string.format_post_list_page_indicator_detail, i, (i - 1) * 20 + 1, i * 20);
                        }
                        runOnUiThread(this::updatePageIndicator);
                    }
                    if (newPageCount == mCurrentPage) {
                        runOnUiThread(() -> getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, mCurrentPage, false));
                    }
                }

            }
        });
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

        // 保存列表位置
        int firstVisiblePosition = mPostCollectionView.getFirstVisiblePosition();
        RecyclerView.ViewHolder firstVisibleViewHolder = mPostCollectionView.getRecyclerView().findViewHolderForPosition(firstVisiblePosition);
        View firstView = firstVisibleViewHolder.itemView;
        int top = (firstView == null) ? 0 : firstView.getTop();

        outState.putInt("listview_index", firstVisiblePosition);
        outState.putInt("listview_top", top);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_list, menu);
        mFavoriteMenuItem = menu.findItem(R.id.action_thread_favorite);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mItemList.size() == 0 || mUserAccountManager.getAuthObject() == null) mFavoriteMenuItem.setVisible(false);
        else if(mItemList.size() > 0) {
            mFavoriteMenuItem.setVisible(true);
            if(mIsFavorite) {
                mFavoriteMenuItem.setIcon(R.drawable.ic_action_favorite);
                mFavoriteMenuItem.setTitle(R.string.action_thread_remove_favorite);
            }
            else {
                mFavoriteMenuItem.setIcon(R.drawable.ic_action_favorite_outline);
                mFavoriteMenuItem.setTitle(R.string.action_thread_add_favorite);
            }
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
            case R.id.action_thread_goto_floor:
                onClickGotoFloor();
                return true;
            case R.id.action_change_theme:
                setNightMode(!isNightMode());
                return true;
            case R.id.action_thread_favorite:
                getPresenter().addOrRemoveFavorite(mUserAccountManager.getAuthObject(), mThreadId, mIsFavorite);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
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
    public void showPostList(int page, List<PostModel> posts, boolean refreshPosition) {
        this.mCurrentPage = page;
        updatePageIndicator();
        // mPostCollectionView.getRecyclerView().stopScroll();
        mCollectionAdapter.replaceWith(posts);
        if(refreshPosition) {
            mPostCollectionView.getRecyclerView().scrollToPosition(0);
            mToolbarQuickReturn.show();
        }
        if(page == 1 && posts.size() > 0 && mItemList.get(0).getOrdinal() == 1) {
            mIsFavorite = mItemList.get(0).isFavorite();
        }
        if(mTargetOrdinal !=  -1) {
            scrollToOrdinal(mTargetOrdinal);
            mTargetOrdinal = -1;
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onGetPostLocationComplete(DataItemLocationModel locationModel) {
        if(locationModel != null && locationModel.isLoad() && locationModel.getLocation().startsWith("thread_")) {
            String idString = locationModel.getLocation();
            int firstIndex = idString.indexOf("_");
            int lastIndex = idString.lastIndexOf("_");
            if(lastIndex > firstIndex + 1)
                mThreadId = Long.valueOf(idString.substring(firstIndex + 1, lastIndex));
            else
                mThreadId = Long.valueOf(idString.substring(firstIndex + 1));
            mTargetOrdinal = locationModel.getOrdinal();
            getPresenter().loadThreadInfo(mUserAccountManager.getAuthObject(), mThreadId);
        }
    }

    @Override
    public void onLoadThreadInfoComplete(ThreadInfoModel threadInfoModel) {
        mThreadModel = threadInfoModel;
        mThreadSubject = threadInfoModel.getSubject();
        setThreadSubjectSpanned(mThreadModel);
        mCollectionAdapter.notifyItemChanged(1);
        mCollectionAdapter.setThreadAuthorId(threadInfoModel.getAuthorId());

        // Calculate page here.
        int replyCount = mThreadModel.getReplies() + 1; // 楼主本身的一楼未计算
        mPageCount = replyCount == 0 ? 1 : (int)Math.ceil((double) replyCount / 20d);

        mPageIndicatorItems = new String[mPageCount];
        for(int i = 1; i <= mPageCount; i++) {
            mPageIndicatorItems[i - 1] = getString(R.string.format_post_list_page_indicator_detail, i, (i - 1) * 20 + 1, i * 20);
        }

        if(mPageCount > 0) {
            if(mTargetOrdinal == -1) {
                getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, 1, true);
            } else {
                int page = replyCount == 0 ? 1 : (int)Math.ceil((double) mTargetOrdinal / 20d);
                getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, page, true);
            }
        }
    }

    @Override
    public void onAddOrRemoveFavoriteComplete(boolean isFavorite) {
        if(mCurrentPage == 1 && mItemList.size() > 0) {
            mItemList.get(0).setFavorite(isFavorite);
        }
        mIsFavorite = isFavorite;
        invalidateOptionsMenu();
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

    private void setThreadSubjectSpanned(ThreadInfoModel threadInfoModel) {
        SpannableStringBuilder spannableTitle = new SpannableStringBuilder();
        if(threadInfoModel.isDigest()) {
            String digestIndicator = getString(R.string.indicator_thread_digest);
            spannableTitle.append(digestIndicator);
            spannableTitle.setSpan(new ForegroundColorSpan(ColorUtils.getColorFromAttr(this, R.attr.colorAccent)), 0, digestIndicator.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        spannableTitle.append(android.text.Html.fromHtml(threadInfoModel.getSubject()));
        mThreadTitleTextView.setText(spannableTitle);
    }

    private void onClickGotoFloor() {
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title(R.string.action_thread_goto_floor)
                .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                .customView(R.layout.dialog_input_floor, false)
                .positiveText(android.R.string.ok).callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        super.onPositive(dialog);
                        EditText editText = (EditText) dialog.getCustomView().findViewById(R.id.edit_floor);
                        String content = editText.getText().toString();
                        if(TextUtils.isDigitsOnly(content)) {
                            int floor = Integer.valueOf(content);

                            int replyCount = mThreadModel.getReplies() + 1; // 楼主本身的一楼未计算
                            if(floor > 0 && floor <= replyCount) {
                                int page = (replyCount - 1 == 0) ? 1 : (int)Math.ceil((double) floor / 20d);
                                if(page == mCurrentPage)  {
                                    scrollToOrdinal(floor);
                                } else {
                                    mTargetOrdinal = floor;
                                    getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, page, true);
                                }
                            } else {
                                ToastProxy.showToast(PostListActivity.this, getString(R.string.toast_error_invalid_floor), TOAST_ALERT);
                            }
                        }
                    }
                })
                .build();
        dialog.show();
    }

    private void scrollToOrdinal(int targetOrdinal) {
        int position = targetOrdinal - (mCurrentPage - 1) * 20 + mCollectionAdapter.getHeaderViewCount();
        LinearLayoutManager layoutManager = (LinearLayoutManager)mPostCollectionView.getRecyclerView().getLayoutManager();
        layoutManager.scrollToPositionWithOffset(position - 1 > 0 ? position - 1 : position, UIUtils.calculateActionBarSize(this));
    }
}
