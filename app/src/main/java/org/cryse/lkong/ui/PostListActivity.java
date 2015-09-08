package org.cryse.lkong.ui;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.DynamicLayout;
import android.text.Html;
import android.text.InputType;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.bumptech.glide.Glide;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.account.UserAccountManager;
import org.cryse.lkong.application.qualifier.PrefsImageDownloadPolicy;
import org.cryse.lkong.application.qualifier.PrefsReadFontSize;
import org.cryse.lkong.application.qualifier.PrefsScrollByVolumeKey;
import org.cryse.lkong.application.qualifier.PrefsUseInAppBrowser;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.EditPostDoneEvent;
import org.cryse.lkong.event.NewPostDoneEvent;
import org.cryse.lkong.event.ThemeColorChangedEvent;
import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.PostDisplayCache;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.presenter.PostListPresenter;
import org.cryse.lkong.ui.adapter.PostListAdapter;
import org.cryse.lkong.ui.adapter.PostRateAdapter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.EmptyImageGetter;
import org.cryse.lkong.utils.LKongUrlDispatcher;
import org.cryse.lkong.utils.QuickReturnUtils;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.htmltextview.ClickableImageSpan;
import org.cryse.lkong.utils.htmltextview.EmoticonImageSpan;
import org.cryse.lkong.utils.htmltextview.HtmlTagHandler;
import org.cryse.lkong.utils.htmltextview.HtmlTextUtils;
import org.cryse.lkong.utils.share.ShareContentBuilder;
import org.cryse.lkong.utils.snackbar.SimpleSnackbarType;
import org.cryse.lkong.view.PostListView;
import org.cryse.lkong.widget.FloatingActionButtonEx;
import org.cryse.lkong.widget.PagerControl;
import org.cryse.lkong.widget.PostItemView;
import org.cryse.utils.ColorUtils;
import org.cryse.utils.DateFormatUtils;
import org.cryse.utils.preference.BooleanPreference;
import org.cryse.utils.preference.StringPreference;
import org.cryse.widget.recyclerview.PtrRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Bind;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PostListActivity extends AbstractThemeableActivity implements PostListView {
    public static final String LOG_TAG = PostListActivity.class.getName();
    AppNavigation mNavigation = new AppNavigation();
    private int mCurrentPage = -1;
    private int mPageCount = 0;
    private ThreadInfoModel mThreadModel;

    @Inject
    PostListPresenter mPresenter;

    @Inject
    UserAccountManager mUserAccountManager;

    @Inject
    @PrefsImageDownloadPolicy
    StringPreference mImageDownloadPolicy;
    @Inject
    @PrefsReadFontSize
    StringPreference mReadFontSizePref;
    @Inject
    @PrefsUseInAppBrowser
    BooleanPreference mUseInAppBrowser;
    @Inject
    @PrefsScrollByVolumeKey
    BooleanPreference mScrollByVolumeKey;


    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.activity_post_list_recyclerview)
    PtrRecyclerView mPostCollectionView;
    @Bind(R.id.fab)
    FloatingActionButtonEx mFab;
    @Bind(R.id.activity_post_list_page_control)
    PagerControl mFooterPagerControl;
    @Bind(R.id.loading_progressbar)
    ProgressBar mProgressBar;

    View mTopPaddingHeaderView;
    View mBottomPaddingFooterView;
    View mThreadIntroHeaderView;
    TextView mThreadTitleTextView;
    TextView mThreadDetailCountTextView;
    TextView mForumNameTextView;
    QuickReturnUtils mToolbarQuickReturn;
    MenuItem mFavoriteMenuItem;
    MenuItem mChangeThemeMenuItem;
    private PagerControl.OnPagerControlListener mOnPagerControlListener;

    private PostListAdapter mCollectionAdapter;

    List<PostModel> mItemList = new ArrayList<PostModel>();

    LKongUrlDispatcher mUrlDispatcher;

    private long mThreadId = -1;
    private long mTargetPostId = -1;
    private int mTargetOrdinal = -1;
    private String mThreadSubject = "";
    private Boolean mIsFavorite = null;
    private int mBaseTranslationY = 0;
    private String[] mPageIndicatorItems;
    private int mAccentColor;
    private int mTextSecondaryColor;
    private String mTodayPrefix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);
        ButterKnife.bind(this);
        setUpToolbar(mToolbar);
        setupPageControlListener();
        setTitle(R.string.activity_title_post_list);

        initRecyclerView();
        initTextPaint();
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_THREAD_ID)) {
            mThreadId = intent.getLongExtra(DataContract.BUNDLE_THREAD_ID, -1);
        } else if(intent.hasExtra(DataContract.BUNDLE_POST_ID)) {
            mTargetPostId = intent.getLongExtra(DataContract.BUNDLE_POST_ID, -1);
        }
        mCurrentPage = intent.getIntExtra(DataContract.BUNDLE_THREAD_CURRENT_PAGE, 1);
        if(mThreadId == -1 && mTargetPostId == -1)
            throw new IllegalStateException("PostListActivity missing extra in intent.");
        mUrlDispatcher = new LKongUrlDispatcher(mUrlCallback);
    }

    private void initRecyclerView() {
        mPostCollectionView.setMode(PullToRefreshBase.Mode.BOTH);
        mPostCollectionView.getRefreshableView().setLayerType(View.LAYER_TYPE_NONE, null);
        mPostCollectionView.getRefreshableView().setDrawingCacheEnabled(false);
        mPostCollectionView.getRefreshableView().setAnimationCacheEnabled(false);
        // mPostCollectionView.getRefreshableView().setItemViewCacheSize(20);
        mPostCollectionView.getRefreshableView().setItemAnimator(new DefaultItemAnimator());
        mPostCollectionView.getRefreshableView().setLayoutManager(new LinearLayoutManager(this));
        mCollectionAdapter = new PostListAdapter(this, mItemList, mUserAccountManager.getCurrentUserAccount().getUserId(), Integer.valueOf(mImageDownloadPolicy.get()));
        mPostCollectionView.getRefreshableView().setAdapter(mCollectionAdapter);

        mTopPaddingHeaderView = getLayoutInflater().inflate(R.layout.layout_empty_recyclerview_top_padding, null);
        // ((TextView)mTopPaddingHeaderView).setText(getString(R.string.text_load_prev_page));
        RecyclerView.LayoutParams topPaddingLP = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.calculateActionBarSize(this) + getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
        mTopPaddingHeaderView.setLayoutParams(topPaddingLP);
        mCollectionAdapter.addHeaderView(mTopPaddingHeaderView);

        mBottomPaddingFooterView = getLayoutInflater().inflate(R.layout.layout_empty_recyclerview_top_padding, null);
        // ((TextView)mBottomPaddingFooterView).setText(getString(R.string.text_load_next_page));
        RecyclerView.LayoutParams bottomPaddingLP = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, UIUtils.calculateActionBarSize(this) + UIUtils.dp2px(this, 16f * 2));
        mBottomPaddingFooterView.setLayoutParams(bottomPaddingLP);
        mCollectionAdapter.addFooterView(mBottomPaddingFooterView);

        mThreadIntroHeaderView = getLayoutInflater().inflate(R.layout.layout_post_intro_header, null);
        RecyclerView.LayoutParams threadIntroHeaderLP = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mThreadIntroHeaderView.setLayoutParams(threadIntroHeaderLP);
        mThreadTitleTextView = (TextView) mThreadIntroHeaderView.findViewById(R.id.layout_post_intro_header_textview_title);
        mThreadDetailCountTextView = (TextView) mThreadIntroHeaderView.findViewById(R.id.layout_post_intro_header_textview_detail_count);
        mForumNameTextView = (TextView) mThreadIntroHeaderView.findViewById(R.id.layout_post_intro_header_textview_forum_name);

        mCollectionAdapter.addHeaderView(mThreadIntroHeaderView);

        mFooterPagerControl.setOnPagerControlListener(mOnPagerControlListener);
        mToolbarQuickReturn = new QuickReturnUtils(mToolbar, QuickReturnUtils.ANIMATE_DIRECTION_UP);
        mPostCollectionView.getRefreshableView().addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean dragging = false;
            int mNegativeDyAmount = 0;
            private int mAmountScrollY = 0;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                dragging = RecyclerView.SCROLL_STATE_DRAGGING == newState;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && isRecyclerViewAtBottom(recyclerView)) {
                    mFooterPagerControl.show();
                    mFab.show();
                    mToolbarQuickReturn.show();
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if(!isActivityDestroyed())
                        Glide.with(PostListActivity.this).resumeRequests();
                } else {
                    Glide.with(PostListActivity.this).pauseRequests();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //Glide.with(PostListActivity.this).pauseRequests();

                mAmountScrollY = mAmountScrollY + dy;
                int toolbarHeight = mToolbar.getHeight();
                if (dy > 0) {
                    mNegativeDyAmount = 0;
                    if (mAmountScrollY - mBaseTranslationY - toolbarHeight > toolbarHeight) {
                        mToolbarQuickReturn.hide();
                        mFooterPagerControl.hide();
                        mFab.hide();
                    }
                } else if (dy < 0) {
                    mAmountScrollY = 0;
                    mNegativeDyAmount = mNegativeDyAmount + dy;
                    if (Math.abs(mNegativeDyAmount - mBaseTranslationY) > toolbarHeight) {
                        mToolbarQuickReturn.show();
                        mFooterPagerControl.show();
                        mFab.show();
                    }
                }
            }
        });
        mCollectionAdapter.setOnItemButtonClickListener(new PostListAdapter.OnItemButtonClickListener() {
            @Override
            public void onPostTextLongClick(View view, int position) {
                PostModel postItem = mCollectionAdapter.getItem(position - mCollectionAdapter.getHeaderViewCount());
                if(postItem != null) {
                    MaterialDialog materialDialog = new MaterialDialog.Builder(PostListActivity.this)
                            .title(R.string.dialog_title_copy_content)
                            .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                            .content(postItem.getPostDisplayCache().getSpannableStringBuilder())
                            .show();
                    materialDialog.getContentView().setTextIsSelectable(true);
                }
            }

            @Override
            public void onRateClick(View view, int position) {
                int itemPosition = position - mCollectionAdapter.getHeaderViewCount();
                if(itemPosition >= 0 && itemPosition < mCollectionAdapter.getItemCount()) {
                    PostModel postModel = mCollectionAdapter.getItem(itemPosition);
                    view.post(() -> openRateDialog(postModel));
                }
            }

            @Override
            public void onRateTextClick(View view, int position) {
                int itemPosition = position - mCollectionAdapter.getHeaderViewCount();
                if(itemPosition >= 0 && itemPosition < mCollectionAdapter.getItemCount()) {
                    PostModel postModel = mCollectionAdapter.getItem(itemPosition);
                    view.post(() -> openRateLogDialog(postModel));
                }
            }

            @Override
            public void onShareClick(View view, int position) {
                int itemPosition = position - mCollectionAdapter.getHeaderViewCount();
                if(itemPosition >= 0 && itemPosition < mCollectionAdapter.getItemCount()) {
                    PostModel postModel = mCollectionAdapter.getItem(itemPosition);
                    view.post(() -> sendSharePostIntent(postModel));
                }
            }

            @Override
            public void onReplyClick(View view, int position) {
                if (mUserAccountManager.isSignedIn()) {
                    PostModel postItem = mCollectionAdapter.getItem(position - mCollectionAdapter.getHeaderViewCount());
                    mNavigation.openActivityForReplyToPost(PostListActivity.this, mThreadId, postItem.getAuthor().getUserName(), postItem.getPid());
                } else {
                    mNavigation.navigateToSignInActivity(PostListActivity.this, false);
                }
            }

            @Override
            public void onEditClick(View view, int position) {
                if (mUserAccountManager.isSignedIn()) {
                    PostModel postItem = mCollectionAdapter.getItem(position - mCollectionAdapter.getHeaderViewCount());
                    String content;
                    if(postItem.getMessage().contains("</blockquote>")) {
                        int indexOfQuota = postItem.getMessage().indexOf("</blockquote>");
                        content = postItem.getMessage().substring(indexOfQuota + 13);
                    } else {
                        content = postItem.getMessage();
                    }
                    if(postItem.getOrdinal() == 1) {
                        String title = mThreadSubject;
                        mNavigation.openActivityForEditThread(PostListActivity.this, mThreadId, postItem.getPid(), title, content);
                    } else {
                        mNavigation.openActivityForEditPost(PostListActivity.this, mThreadId, postItem.getAuthor().getUserName(), postItem.getPid(), content);
                    }
                } else {
                    mNavigation.navigateToSignInActivity(PostListActivity.this, false);
                }
            }

            @Override
            public void onProfileImageClick(View view, int position) {
                PostModel postItem = mCollectionAdapter.getItem(position - mCollectionAdapter.getHeaderViewCount());
                int[] startingLocation = new int[2];
                view.getLocationOnScreen(startingLocation);
                startingLocation[0] += view.getWidth() / 2;
                mNavigation.openActivityForUserProfile(PostListActivity.this, startingLocation, postItem.getAuthorId());
            }
        });
        mCollectionAdapter.setOnSpanClickListener(new PostItemView.OnSpanClickListener() {
            @Override
            public boolean onImageSpanClick(long postId, ClickableImageSpan span, ArrayList<String> urls, String initUrl) {
                Intent intent = new Intent(PostListActivity.this, PhotoViewPagerActivity.class);
                intent.putExtra(DataContract.BUNDLE_POST_IMAGE_URL_LIST, urls);
                intent.putExtra(DataContract.BUNDLE_POST_IMAGE_INIT_URL, initUrl);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onUrlSpanClick(long postId, URLSpan span, String target) {
                urlParse(target);
                return true;
            }
        });
        mPostCollectionView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> recyclerViewPullToRefreshBase) {
                goToPrevPage(false);
                recyclerViewPullToRefreshBase.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> recyclerViewPullToRefreshBase) {
                goToNextPage(false);
                recyclerViewPullToRefreshBase.onRefreshComplete();
            }
        });
        mFab.setOnClickListener(view -> {
            if (mUserAccountManager.isSignedIn()) {
                mNavigation.openActivityForReplyToThread(this, mThreadId, mThreadSubject);
            } else {
                mNavigation.navigateToSignInActivity(this, false);
            }
        });
        setColorToViews(getThemeEngine().getPrimaryColor(), getThemeEngine().getPrimaryDarkColor());
    }

    private void setupPageControlListener() {
        mOnPagerControlListener = new PagerControl.OnPagerControlListener() {
            @Override
            public void onBackwardClick() {
                goToPrevPage(true);
            }

            @Override
            public void onPageIndicatorClick() {
                MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(PostListActivity.this)
                        .title(R.string.dialog_post_list_choose_page)
                        .items(mPageIndicatorItems)
                        .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                        .itemsCallbackSingleChoice(mCurrentPage - 1, (materialDialog, view, i, charSequence) -> {
                            goToPage(i + 1);
                            return true;
                        });
                MaterialDialog dialog = dialogBuilder.build();
                dialog.show();
            }

            @Override
            public void onForwardClick() {
                goToNextPage(true);
            }
        };
    }

    private void goToPage(int page) {
        if(page == mCurrentPage || page < 1 || page > mPageCount) return;
            getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, page, true, SHOW_MODE_REPLACE);
    }

    private void refreshCurrentPage() {
        getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, mCurrentPage, true, SHOW_MODE_REPLACE_SIMPLE);
    }

    private void goToNextPage(boolean resetPosition) {
        if(mCurrentPage + 1 >= 1 && mCurrentPage + 1 <= mPageCount)
            getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, mCurrentPage + 1, resetPosition, SHOW_MODE_NEXT_PAGE);
    }

    private void goToPrevPage(boolean resetPosition) {
        if(mCurrentPage - 1 >= 1 && mCurrentPage - 1 <= mPageCount)
            getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, mCurrentPage - 1, resetPosition, SHOW_MODE_PREV_PAGE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(DataContract.BUNDLE_CONTENT_LIST_STORE)) {
            mThreadId = savedInstanceState.getLong(DataContract.BUNDLE_THREAD_ID);
            if(savedInstanceState.containsKey(DataContract.BUNDLE_THREAD_INFO_OBJECT)) {
                if(savedInstanceState.containsKey(DataContract.BUNDLE_THREAD_IS_FAVORITE))
                    mIsFavorite = savedInstanceState.getBoolean(DataContract.BUNDLE_THREAD_IS_FAVORITE);
                mThreadSubject = savedInstanceState.getString(DataContract.BUNDLE_THREAD_SUBJECT);
                mThreadModel = savedInstanceState.getParcelable(DataContract.BUNDLE_THREAD_INFO_OBJECT);
                mCurrentPage = savedInstanceState.getInt(DataContract.BUNDLE_THREAD_CURRENT_PAGE);
                mPageCount = savedInstanceState.getInt(DataContract.BUNDLE_THREAD_PAGE_COUNT);
                mPageIndicatorItems = savedInstanceState.getStringArray(DataContract.BUNDLE_THREAD_PAGE_INDICATOR_ITEMS);
                ArrayList<PostModel> list = savedInstanceState.getParcelableArrayList(DataContract.BUNDLE_CONTENT_LIST_STORE);
                // mCollectionAdapter.addAll(list);
                setThreadSubjectSpanned(mThreadModel);
                showPostList(mCurrentPage, list, false, SHOW_MODE_REPLACE);

                if(savedInstanceState.containsKey("listview_index") && savedInstanceState.containsKey("listview_top")) {
                    // Restore last state for checked position.
                    final int firstVisibleItemPosition = savedInstanceState.getInt("listview_index", -1);
                    final int firstVisibleItemTop = savedInstanceState.getInt("listview_top", 0);

                    mPostCollectionView.getRefreshableView().post(() -> {
                        if (firstVisibleItemPosition != -1) {
                            RecyclerView.LayoutManager layoutManager = mPostCollectionView.getRefreshableView().getLayoutManager();
                            if (layoutManager instanceof GridLayoutManager) {
                                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                                gridLayoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, firstVisibleItemTop);
                            } else if (layoutManager instanceof LinearLayoutManager) {
                                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                                linearLayoutManager.scrollToPositionWithOffset(firstVisibleItemPosition, firstVisibleItemTop);
                            } else {
                                throw new IllegalStateException();
                            }
                            mPostCollectionView.getRefreshableView().stopScroll();
                        }
                    });
                }
            }
        } else {
            /*mPostCollectionView.getSwipeToRefresh().measure(1,1);
            mPostCollectionView.getSwipeToRefresh().setRefreshing(true);*/
            if(mThreadId != -1) {
                getPresenter().loadThreadInfo(mUserAccountManager.getAuthObject(), mThreadId);
            } else if(mTargetPostId != -1) {
                getPresenter().getPostLocation(mUserAccountManager.getAuthObject(), mTargetPostId, true);
            }
            // getPresenter().loadThreadList(mForumId, mCurrentListType, false);
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        try {
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
                        updatePageIndicator();
                    }
                    if (newPageCount == mCurrentPage) {
                        getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, mCurrentPage, false, SHOW_MODE_REPLACE);
                    }
                }

            } else if(event instanceof ThemeColorChangedEvent) {
                setColorToViews(((ThemeColorChangedEvent) event).getNewPrimaryColor(), ((ThemeColorChangedEvent) event).getNewPrimaryDarkColor());
            } else if(event instanceof EditPostDoneEvent) {
                // refreshCurrentPage();
                getPresenter().loadThreadInfo(mUserAccountManager.getAuthObject(), mThreadId);
            }
        } catch (Exception ex) {
            Timber.e(ex, ex.getMessage(), LOG_TAG);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(DataContract.BUNDLE_THREAD_ID, mThreadId);
        if(mThreadModel != null) {
            if(mIsFavorite != null)
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
        RecyclerView.ViewHolder firstVisibleViewHolder = mPostCollectionView.getRefreshableView().findViewHolderForPosition(firstVisiblePosition);
        if(firstVisibleViewHolder != null) {
            View firstView = firstVisibleViewHolder.itemView;
            int top = (firstView == null) ? 0 : firstView.getTop();

            outState.putInt("listview_index", firstVisiblePosition);
            outState.putInt("listview_top", top);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_list, menu);
        mFavoriteMenuItem = menu.findItem(R.id.action_thread_favorite);
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mChangeThemeMenuItem != null) {
            if (isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_light_theme);
            else
                mChangeThemeMenuItem.setTitle(R.string.action_dark_theme);
        }
        if(mItemList.size() == 0 || mUserAccountManager.getAuthObject() == null) mFavoriteMenuItem.setVisible(false);
        else if(mItemList.size() > 0 && mIsFavorite != null) {
            mFavoriteMenuItem.setVisible(true);
            if(mIsFavorite) {
                mFavoriteMenuItem.setIcon(R.drawable.ic_action_favorite);
                mFavoriteMenuItem.setTitle(R.string.action_thread_remove_favorite);
            }
            else {
                mFavoriteMenuItem.setIcon(R.drawable.ic_action_favorite_outline);
                mFavoriteMenuItem.setTitle(R.string.action_thread_add_favorite);
            }
        } else if(mIsFavorite == null) {
            mFavoriteMenuItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                closeActivityWithTransition();
                return true;
            case R.id.action_thread_goto_floor:
                onClickGotoFloor();
                return true;
            case R.id.action_share_thread:
                sendShareThreadIntent();
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
    public void showPostList(int page, List<PostModel> posts, boolean refreshPosition, int showMode) {
        setLoading(true);
        createSpan(page, posts, refreshPosition, showMode);
    }

    private void showPostListInternal(int page, List<PostModel> posts, boolean refreshPosition, int showMode) {
        setLoading(false);
        // Resume tag when display new items.
        if(!isActivityDestroyed())
            Glide.with(PostListActivity.this).resumeRequests();
        this.mCurrentPage = page;
        updatePageIndicator();
        int currentItemCount = mItemList.size();
        switch (showMode) {
            case SHOW_MODE_REPLACE_SIMPLE:
                mCollectionAdapter.replaceWith(posts);
                break;
            case SHOW_MODE_REPLACE:
                mCollectionAdapter.replaceWith(posts);
                scrollToPosition(0);
                break;
            case SHOW_MODE_PREV_PAGE:
                mCollectionAdapter.addAll(0, posts);
                if(currentItemCount > 0)
                    mCollectionAdapter.rangeRemove(posts.size(), posts.size() + currentItemCount);
                scrollToPosition(posts.size() - 1);
                break;
            case SHOW_MODE_NEXT_PAGE:
                mCollectionAdapter.addAll(posts);
                if(currentItemCount > 0)
                    mCollectionAdapter.rangeRemove(0, currentItemCount);
                scrollToPosition(0);
                break;
        }


        if(refreshPosition) {
            mPostCollectionView.getRefreshableView().scrollToPosition(0);
            mToolbarQuickReturn.show();
        }
        if(page == 1 && posts.size() > 0 && mItemList.get(0).getOrdinal() == 1) {
            mIsFavorite = mItemList.get(0).isFavorite();
        } else if(page != 1 && mIsFavorite == null) {
            mIsFavorite = null;
        }
        if(mTargetOrdinal !=  -1) {
            scrollToOrdinal(mTargetOrdinal);
            mTargetOrdinal = -1;
        }
        invalidateOptionsMenu();
        mPostCollectionView.onRefreshComplete();
    }

    @Override
    public void onGetPostLocationComplete(DataItemLocationModel locationModel, boolean loadThreadInfo) {
        if(locationModel != null && locationModel.isLoad() && locationModel.getLocation().startsWith("thread_")) {
            String idString = locationModel.getLocation();
            int firstIndex = idString.indexOf("_");
            int lastIndex = idString.lastIndexOf("_");
            if(lastIndex > firstIndex + 1)
                mThreadId = Long.valueOf(idString.substring(firstIndex + 1, lastIndex));
            else
                mThreadId = Long.valueOf(idString.substring(firstIndex + 1));
            mTargetOrdinal = locationModel.getOrdinal();
            if(loadThreadInfo)
                getPresenter().loadThreadInfo(mUserAccountManager.getAuthObject(), mThreadId);
            else
                calculatePageAndLoad();
        }
    }

    @Override
    public void onLoadThreadInfoComplete(ThreadInfoModel threadInfoModel) {
        mThreadModel = threadInfoModel;
        mThreadSubject = threadInfoModel.getSubject();
        setThreadSubjectSpanned(mThreadModel);

        calculatePageAndLoad();
    }

    private void calculatePageAndLoad() {
        // Calculate page here.
        int replyCount = mThreadModel.getReplies() + 1; // 楼主本身的一楼未计算
        mPageCount = replyCount == 0 ? 1 : (int)Math.ceil((double) replyCount / 20d);

        mPageIndicatorItems = new String[mPageCount];
        for(int i = 1; i <= mPageCount; i++) {
            mPageIndicatorItems[i - 1] = getString(R.string.format_post_list_page_indicator_detail, i, (i - 1) * 20 + 1, i * 20);
        }

        if(mPageCount > 0) {
            if(mTargetOrdinal == -1) {
                getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, ((mCurrentPage >= 1 && mCurrentPage <= mPageCount) ? mCurrentPage : 1), true, SHOW_MODE_REPLACE);
            } else {
                int page = replyCount == 0 ? 1 : (int)Math.ceil((double) mTargetOrdinal / 20d);
                getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, page, true, SHOW_MODE_REPLACE);
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
    public void onRatePostComplete(PostModel.PostRate postRate) {
        long pid = postRate.getPid();
        List<PostModel> itemList = mCollectionAdapter.getItemList();
        for (int i = 0; i < itemList.size(); i++) {
            PostModel postModel = itemList.get(i);
            if(postModel.getPid() == pid) {
                postModel.getRateLog().add(0, postRate);
                postModel.setRateScore(postModel.getRateScore() + postRate.getScore());
                mCollectionAdapter.notifyItemChanged(i + mCollectionAdapter.getHeaderViewCount());
            }
        }
    }

    @Override
    public void setLoading(Boolean value) {
        mProgressBar.setVisibility(value ? View.VISIBLE : View.INVISIBLE);
        // this.mPostCollectionView.getRefreshableView().setRefreshing(value);
    }

    @Override
    public Boolean isLoading() {
        return null;
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
        String detailCount = getString(
                R.string.format_post_header_detail_count,
                threadInfoModel.getViews(),
                threadInfoModel.getReplies()
        );
        mThreadDetailCountTextView.setText(detailCount);
        mForumNameTextView.setText(TextUtils.isEmpty(threadInfoModel.getForumName()) ? "" : threadInfoModel.getForumName());
    }

    private void onClickGotoFloor() {
        final int[] inputFloorNumber = {0};
        new MaterialDialog.Builder(this)
                .title(R.string.action_thread_goto_floor)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .positiveText(android.R.string.ok)
                .alwaysCallInputCallback() // this forces the callback to be invoked with every input change
                .input(R.string.hint_goto_floor, 0, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        if (TextUtils.isEmpty(input) || !TextUtils.isDigitsOnly(input)) {
                            dialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
                            return;
                        }
                        inputFloorNumber[0] = Integer.valueOf(input.toString());
                    }
                })
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                            super.onPositive(dialog);
                        int floor = inputFloorNumber[0];

                        int replyCount = mThreadModel.getReplies() + 1; // 楼主本身的一楼未计算
                        if (floor > 0 && floor <= replyCount) {
                            int page = (replyCount - 1 == 0) ? 1 : (int) Math.ceil((double) floor / 20d);
                            if (page == mCurrentPage) {
                                scrollToOrdinal(floor);
                            } else {
                                mTargetOrdinal = floor;
                                getPresenter().loadPostList(mUserAccountManager.getAuthObject(), mThreadId, page, true, SHOW_MODE_REPLACE);
                            }
                        } else {
                            showSnackbar(
                                    getString(R.string.toast_error_invalid_floor),
                                    SimpleSnackbarType.ERROR,
                                    SimpleSnackbarType.LENGTH_SHORT
                            );
                        }
                    }
                }).show();
    }

    private void scrollToOrdinal(int targetOrdinal) {
        int position = targetOrdinal - (mCurrentPage - 1) * 20 + mCollectionAdapter.getHeaderViewCount();
        LinearLayoutManager layoutManager = (LinearLayoutManager)mPostCollectionView.getRefreshableView().getLayoutManager();
        layoutManager.scrollToPositionWithOffset(position - 1 > 0 ? position - 1 : position, UIUtils.calculateActionBarSize(this));
    }

    private void scrollToPosition(int position) {
        position = position + mCollectionAdapter.getHeaderViewCount();
        LinearLayoutManager layoutManager = (LinearLayoutManager)mPostCollectionView.getRefreshableView().getLayoutManager();
        layoutManager.scrollToPositionWithOffset(position, UIUtils.calculateActionBarSize(this));
    }

    private void setColorToViews(int primaryColor, int primaryDarkColor) {
        mFab.setColorNormal(primaryColor);
        mFab.setColorPressed(primaryDarkColor);
        mFooterPagerControl.findViewById(R.id.widget_pager_control_container).setBackgroundColor(primaryColor);
    }

    private void openRateLogDialog(PostModel postModel) {
            MaterialDialog rateListDialog = new MaterialDialog.Builder(this)
                    .title(R.string.dialog_title_rate)
                    .adapter(new PostRateAdapter(this, postModel.getRateLog()), (materialDialog, view, i, charSequence) -> {

                    })
                    .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                    .positiveText(android.R.string.ok)
                    .build();
        rateListDialog.show();
    }

    private void sendSharePostIntent(PostModel postModel) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        //sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, postModel.getMessage());
        String shareContent = ShareContentBuilder.buildSharePostContent(
                this,
                mThreadModel,
                mCurrentPage,
                postModel
        );
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.text_share_post_title)));
    }

    private void sendShareThreadIntent() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        //sendIntent.putExtra(Intent.EXTRA_HTML_TEXT, postModel.getMessage());
        String shareContent = ShareContentBuilder.buildShareThreadContent(
                this,
                mThreadModel
        );
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.text_share_post_title)));
    }

    private void openRateDialog(PostModel postModel) {
        if(mUserAccountManager.getCurrentUserAccount().getUserId() != postModel.getAuthorId()) {
            MaterialDialog ratePostDialog = new MaterialDialog.Builder(PostListActivity.this)
                    .title(R.string.dialog_title_rate)
                    .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                    .customView(R.layout.dialog_input_score, false)
                    .positiveText(android.R.string.ok).callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog ratePostDialogRef) {
                            super.onPositive(ratePostDialogRef);
                            EditText reasonEditText = (EditText) ratePostDialogRef.getCustomView().findViewById(R.id.edit_reason);
                            EditText scoreEditText = (EditText) ratePostDialogRef.getCustomView().findViewById(R.id.edit_score);
                            String reason = reasonEditText.getText().toString();
                            String scoreText = scoreEditText.getText().toString();
                            if(!TextUtils.isEmpty(scoreText) && TextUtils.isDigitsOnly(scoreText)) {
                                int score = Integer.valueOf(scoreText);
                                getPresenter().ratePost(mUserAccountManager.getAuthObject(), postModel.getPid(), score, reason);
                            } else {
                                showSnackbar(
                                        getString(R.string.toast_error_rate_score_empty),
                                        SimpleSnackbarType.ERROR,
                                        SimpleSnackbarType.LENGTH_SHORT
                                );
                            }
                        }
                    })
                    .build();
            ratePostDialog.show();
        } else {
            showSnackbar(
                    getString(R.string.toast_error_rate_self),
                    SimpleSnackbarType.ERROR,
                    SimpleSnackbarType.LENGTH_SHORT
            );
        }
    }

    public void createSpan(int page, final List<PostModel> posts, boolean refreshPosition, int showMode) {
        int mMaxImageWidth = 256;
        Html.ImageGetter imageGetter = new EmptyImageGetter();
        Observable<List<PostModel>> createSpanObservable = Observable.create(subscriber -> {
            Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.placeholder_loading, null);
            for (PostModel postModel : posts) {
                Spanned spannedText = HtmlTextUtils.htmlToSpanned(postModel.getMessage(), imageGetter, new HtmlTagHandler());
                replaceImageSpan(new SpannableString(spannedText), postModel, drawable);
            }
            subscriber.onNext(posts);
            subscriber.onCompleted();
        });
        createSpanObservable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            showPostListInternal(page, posts, refreshPosition, showMode);
                        },
                        error -> {
                            showSnackbar(
                                    getString(R.string.notification_content_network_error),
                                    SimpleSnackbarType.ERROR,
                                    SimpleSnackbarType.LENGTH_SHORT
                            );
                            Timber.e(error, "PostListActivity::createSpan() onError().", LOG_TAG);
                        },
                        () -> {
                            Timber.d("PostListActivity::createSpan() onComplete().", LOG_TAG);
                        }
                );
    }

    private CharSequence replaceImageSpan(CharSequence sequence, PostModel postModel, Drawable initPlaceHolder) {
        SpannableStringBuilder spannable;
        if(sequence instanceof SpannableStringBuilder)
            spannable = (SpannableStringBuilder)sequence;
        else
            spannable = new SpannableStringBuilder(sequence);
        ImageSpan[] imageSpans = spannable.getSpans(0, sequence.length(), ImageSpan.class );
        URLSpan[] urlSpans = spannable.getSpans(0, sequence.length(), URLSpan.class );

        PostDisplayCache postDisplayCache = new PostDisplayCache();
        postDisplayCache.getImportantSpans().addAll(Arrays.asList(urlSpans));
        postDisplayCache.setUrlSpanCount(urlSpans.length);


        for(ImageSpan imageSpan : imageSpans) {
            int spanStart = spannable.getSpanStart(imageSpan);
            int spanEnd = spannable.getSpanEnd(imageSpan);
            int spanFlags = spannable.getSpanFlags(imageSpan);
            if (!TextUtils.isEmpty(imageSpan.getSource()) && !imageSpan.getSource().contains("http://img.lkong.cn/bq/")) {
                spannable.removeSpan(imageSpan);
                ClickableImageSpan clickableImageSpan = new ClickableImageSpan(
                        this,
                        null,
                        Long.toString(postModel.getPid()),
                        PostListAdapter.POST_PICASSO_TAG,
                        imageSpan.getSource(),
                        R.drawable.placeholder_loading,
                        R.drawable.placeholder_error,
                        256,
                        256,
                        DynamicDrawableSpan.ALIGN_BOTTOM,
                        initPlaceHolder);
                spannable.setSpan(clickableImageSpan,
                        spanStart,
                        spanEnd,
                        spanFlags);
                postDisplayCache.getImportantSpans().add(clickableImageSpan);
                postDisplayCache.getImageUrls().add(imageSpan.getSource());
            } else if(!TextUtils.isEmpty(imageSpan.getSource()) && imageSpan.getSource().contains("http://img.lkong.cn/bq/")){
                spannable.removeSpan(imageSpan);
                EmoticonImageSpan emoticonImageSpan = new EmoticonImageSpan(
                        this,
                        null,
                        Long.toString(postModel.getPid()),
                        PostListAdapter.POST_PICASSO_TAG,
                        imageSpan.getSource(),
                        R.drawable.placeholder_loading,
                        R.drawable.placeholder_error,
                        (int)mContentTextPaint.getTextSize() * 2
                );
                spannable.setSpan(emoticonImageSpan,
                        spanStart,
                        spanEnd,
                        spanFlags);
                postDisplayCache.getEmoticonSpans().add(emoticonImageSpan);
            }
        }
        postDisplayCache.setSpannableStringBuilder(spannable);

        // Generate content StaticLayout
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        UIUtils.InsetsValue padding = UIUtils.getCardViewPadding((int)(4.0 * dm.density), (int)(2.0 * dm.density));
        int contentWidth = dm.widthPixels - UIUtils.dp2px(this, 16f) * 2 - padding.getLeft() - padding.getRight();
        DynamicLayout layout = new DynamicLayout(spannable, mContentTextPaint, contentWidth, Layout.Alignment.ALIGN_NORMAL, 1.3f, 0.0f, false);
        postDisplayCache.setTextLayout(layout);

        // Generate author StaticLayout
        SpannableStringBuilder autherNameSpannable = new SpannableStringBuilder();
        autherNameSpannable.append(postModel.getAuthorName());
        if(postModel.getAuthorId() == mThreadModel.getAuthorId()) {
            String threadAuthorIndicator = getString(R.string.indicator_thread_author);
            autherNameSpannable.append(threadAuthorIndicator);
            autherNameSpannable.setSpan(new ForegroundColorSpan(mAccentColor),
                    postModel.getAuthorName().length(),
                    postModel.getAuthorName().length() + threadAuthorIndicator.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        autherNameSpannable.append('\n');
        String datelineString = DateFormatUtils.formatFullDateDividByToday(
                postModel.getDateline(),
                mTodayPrefix,
                getResources().getConfiguration().locale
        );
        int start = autherNameSpannable.length();
        int end = autherNameSpannable.length() + datelineString.length();
        autherNameSpannable.append(datelineString);
        autherNameSpannable.setSpan(new ForegroundColorSpan(mTextSecondaryColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        autherNameSpannable.setSpan(new AbsoluteSizeSpan((int)mDatelineTextSize), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        int authorWidth = dm.widthPixels - UIUtils.dp2px(this, 72f) - padding.getLeft() - padding.getRight();
        StaticLayout authorLayout = new StaticLayout(autherNameSpannable, mAuthorTextPaint, authorWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        postDisplayCache.setAuthorLayout(authorLayout);
        postModel.setPostDisplayCache(postDisplayCache);
        return spannable;
    }

    private void urlParse(String url) {
        mUrlDispatcher.parseUrl(url);
    }

    private LKongUrlDispatcher.UrlCallback mUrlCallback = new LKongUrlDispatcher.UrlCallback() {
        @Override
        public void onThreadByPostId(long threadId, long postId) {
            if(threadId == mThreadId) {
                mTargetPostId = postId;
                getPresenter().getPostLocation(mUserAccountManager.getAuthObject(), mTargetPostId, false);
            } else {
                mNavigation.openActivityForPostListByPostId(PostListActivity.this, postId);
            }
        }

        @Override
        public void onThreadByThreadId(long threadId) {
            if(threadId == mThreadId) {
                showSnackbar(
                        getString(R.string.toast_error_link_to_current_thread),
                        SimpleSnackbarType.INFO,
                        SimpleSnackbarType.LENGTH_SHORT
                );
            } else {
                mNavigation.openActivityForPostListByThreadId(PostListActivity.this, threadId);
            }
        }

        @Override
        public void onThreadByThreadId(long threadId, int page) {
            if(threadId == mThreadId) {
                showSnackbar(
                        getString(R.string.toast_error_link_to_current_thread),
                        SimpleSnackbarType.INFO,
                        SimpleSnackbarType.LENGTH_SHORT
                );
                goToPage(page);
            } else {
                mNavigation.openActivityForPostListByThreadId(PostListActivity.this, threadId, page);
            }
        }

        @Override
        public void onFailed(String url) {
            mNavigation.openUrl(PostListActivity.this, url, mUseInAppBrowser.get());
        }
    };

    TextPaint mAuthorTextPaint;
    TextPaint mContentTextPaint;
    float mDatelineTextSize;
    private void initTextPaint() {
        mTextSecondaryColor = ColorUtils.getColorFromAttr(this, R.attr.theme_text_color_secondary);
        mAccentColor = ColorUtils.getColorFromAttr(this, R.attr.colorAccent);
        mTodayPrefix = getString(R.string.datetime_today);
        mContentTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        float contentTextSize =  UIUtils.getFontSizeFromPreferenceValue(this, mReadFontSizePref.get());
        mContentTextPaint.setTextSize(contentTextSize);
        mContentTextPaint.setColor(ColorUtils.getColorFromAttr(this, R.attr.theme_text_color_primary));
        mContentTextPaint.linkColor = ColorUtils.getColorFromAttr(this, R.attr.colorAccent);
        mAuthorTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        float authorTextSize =  UIUtils.getSpDimensionPixelSize(this, R.dimen.text_size_subhead);
        mAuthorTextPaint.setTextSize(authorTextSize);
        mAuthorTextPaint.setColor(ColorUtils.getColorFromAttr(this, R.attr.theme_text_color_primary));
        mAuthorTextPaint.linkColor = ColorUtils.getColorFromAttr(this, R.attr.colorAccent);
        mDatelineTextSize = UIUtils.getSpDimensionPixelSize(this, R.dimen.text_size_body1);
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        boolean isFlipPageByVolumeKey = mScrollByVolumeKey.get();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if(isFlipPageByVolumeKey) {
                    scrollDown();
                    return true;
                }
                return false;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if(isFlipPageByVolumeKey) {
                    scrollUp();
                    return true;
                }
                return false;
        }
        return super.onKeyDown (keyCode, event);
    }

    private int calcScrollDistance() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return displayMetrics.heightPixels * 3 / 5;
    }

    private void scrollDown() {
        mPostCollectionView.getRefreshableView().smoothScrollBy(0, calcScrollDistance());
    }

    private void scrollUp() {
        mPostCollectionView.getRefreshableView().smoothScrollBy(0, -calcScrollDistance());
    }
}
