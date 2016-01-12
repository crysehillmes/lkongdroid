package org.cryse.lkong.ui;

import android.accounts.Account;
import android.animation.Animator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
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

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.broadcast.BroadcastConstants;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.event.FavoritesChangedEvent;
import org.cryse.lkong.event.NoticeCountEvent;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.ThreadModel;
import org.cryse.lkong.presenter.FavoritesPresenter;
import org.cryse.lkong.sync.SyncUtils;
import org.cryse.lkong.ui.adapter.ThreadListAdapter;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.ui.search.SuggestionsBuilder;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.animation.LayerEnablingAnimatorListener;
import org.cryse.lkong.view.FavoritesView;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;
import org.cryse.utils.preference.StringPrefs;
import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;

public class FavoritesFragment extends SimpleCollectionFragment<
        ThreadModel,
        ThreadListAdapter,
        FavoritesPresenter> implements FavoritesView<ThreadModel> {
    private static final String LOG_TAG = FavoritesFragment.class.getName();
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1023;
    private static final String SEARCH_FRAGMENT_TAG = "search_fragment_tag";

    boolean mNeedRefresh = false;
    @Inject
    FavoritesPresenter mPresenter;

    StringPrefs mAvatarDownloadPolicy;

    @Bind(R.id.searchview)
    PersistentSearchView mSearchView;
    @Bind(R.id.view_search_tint)
    View mSearchTintView;
    @Bind(R.id.search_fragment_container)
    FrameLayout mSearchContainer;

    protected MenuItem mChangeThemeMenuItem;
    private MenuItem mNotificationMenuItem;
    private MenuItem mSearchMenuItem;
    private boolean mHasNotification = false;

    public static FavoritesFragment newInstance(Bundle args) {
        FavoritesFragment fragment = new FavoritesFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        mAvatarDownloadPolicy = Prefs.getStringPrefs(PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY,
                PreferenceConstant.SHARED_PREFERENCE_AVATAR_DOWNLOAD_POLICY_VALUE);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        getThemedActivity().setSupportActionBar(mToolbar);
        mToolbar.setBackgroundColor(getPrimaryColor());
        final ActionBar actionBar = getThemedActivity().getSupportActionBar();
        if(actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setUpSearchView();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_favorites, menu);
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        mNotificationMenuItem = menu.findItem(R.id.action_open_notification);
        mSearchMenuItem = menu.findItem(R.id.action_open_search);
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
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_open_search:
                if (mSearchMenuItem != null) {
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
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActivityTitle();
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
                }
                if (!mSearchContainer.isShown()) {
                    final SearchFragment finalSearchFragment = searchFragment;
                    slideInToTop(mSearchContainer, true, () -> {
                        finalSearchFragment.search(string);
                    });
                } else {
                    searchFragment.search(string);
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
        if(mNeedRefresh) {
            mNeedRefresh = false;
            getPresenter().loadFavorites(mUserAccountManager.getAuthObject(), false);
        }
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
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favorites;
    }

    @Override
    protected FavoritesPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected ThreadListAdapter createAdapter(List<ThreadModel> itemList) {
        ThreadListAdapter adapter = new ThreadListAdapter(getActivity(), mATEKey, mItemList, Integer.valueOf(mAvatarDownloadPolicy.get()));
        adapter.setOnThreadItemClickListener(new ThreadListAdapter.OnThreadItemClickListener() {
            @Override
            public void onProfileAreaClick(View view, int position, long uid) {
                int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
                if (itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    ThreadModel model = mCollectionAdapter.getItem(itemIndex);
                    int[] startingLocation = new int[2];
                    view.getLocationOnScreen(startingLocation);
                    startingLocation[0] += view.getWidth() / 2;
                    mNavigation.openActivityForUserProfile(getActivity(), startingLocation, model.getUid());
                }
            }

            @Override
            public void onItemThreadClick(View view, int adapterPosition) {
                int itemIndex = adapterPosition - mCollectionAdapter.getHeaderViewCount();
                if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
                    ThreadModel item = mCollectionAdapter.getItem(itemIndex);
                    String idString = item.getId().substring(7);
                    long tid = Long.parseLong(idString);
                    mNavigation.openActivityForPostListByThreadId(getActivity(), tid);
                }
            }
        });
        return adapter;
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadFavorites(authObject, start, isLoadingMore);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            ThreadModel item = mCollectionAdapter.getItem(position);
            String idString = item.getId().substring(7);
            long tid = Long.parseLong(idString);
            mNavigation.openActivityForPostListByThreadId(getActivity(), tid);
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof FavoritesChangedEvent)
            mNeedRefresh = true;
        else if(event instanceof NoticeCountEvent) {
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
        } else if (event instanceof CurrentAccountChangedEvent) {
            loadData(mUserAccountManager.getAuthObject(), 0, false);
            checkNewNoticeCount();
        }
    }

    protected void setActivityTitle() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            mainActivity.onSectionAttached(getString(R.string.drawer_item_favorites));
        }
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return null;
    }

    protected void checkNewNoticeCount() {
        if (isAdded()) {
            Account account = mUserAccountManager.getCurrentUserAccount().getAccount();
            if(account != null)
                SyncUtils.manualSync(account, SyncUtils.SYNC_AUTHORITY_CHECK_NOTICE);
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
        }
    }

    @Override
    public void onCheckNoticeCountComplete(NoticeCountModel noticeCountModel) {
        if(noticeCountModel != null) {
            mHasNotification = noticeCountModel.hasNotification() && noticeCountModel.getUserId() == mUserAccountManager.getCurrentUserId();
            if(getActivity() != null)
                getActivity().invalidateOptionsMenu();
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

    private BroadcastReceiver mCheckNoticeCountDoneBroadcastReceiver = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            // update your views
            // loadData(null, 0, false);
            mPresenter.checkNoticeCountFromDatabase(mUserAccountManager.getCurrentUserId());
            abortBroadcast();
        }
    };
}
