package org.cryse.lkong.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.NoticeCountEvent;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.model.TimelineModel;
import org.cryse.lkong.presenter.TimelinePresenter;
import org.cryse.lkong.ui.adapter.TimelineAdapter;
import org.cryse.lkong.utils.LKAuthObject;
import org.cryse.lkong.utils.UIUtils;

import java.util.List;

import javax.inject.Inject;

public class TimelineFragment extends SimpleCollectionFragment<
        TimelineModel,
        TimelineAdapter,
        TimelinePresenter> {
    private static final String LOG_TAG = TimelineFragment.class.getName();
    private static final String LOAD_IMAGE_TASK_TAG = "timeline_load_image_tag";

    private MenuItem mNotificationMenuItem;
    private boolean mHasNotification = false;
    @Inject
    TimelinePresenter mPresenter;

    protected MenuItem mChangeThemeMenuItem;

    public static TimelineFragment newInstance(Bundle args) {
        TimelineFragment fragment = new TimelineFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(getActivity()).lKongPresenterComponent().inject(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_timeline, menu);
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        mNotificationMenuItem = menu.findItem(R.id.action_open_notification);
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
            case R.id.action_open_notification:
                mAndroidNavigation.navigateToNotificationActivity(getActivity());
                return true;
            case R.id.action_change_theme:
                if(isNightMode() != null) {
                    getThemedActivity().setNightMode(!isNightMode());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_simple_collection;
    }

    @Override
    protected TimelinePresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected TimelineAdapter createAdapter(List<TimelineModel> itemList) {
        return new TimelineAdapter(getActivity(), mItemList, LOAD_IMAGE_TASK_TAG);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadTimeline(authObject, start, isLoadingMore);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            TimelineModel item = mCollectionAdapter.getItem(position);
            if(item.getId().startsWith("thread_")) {
                mAndroidNavigation.openActivityForPostListByThreadId(getActivity(), Long.valueOf(item.getId().substring(7)));
            } else if(item.getId().startsWith("post_")) {
                mAndroidNavigation.openActivityForPostListByPostId(getActivity(), Long.valueOf(item.getId().substring(5)));
            } else {
                mAndroidNavigation.openActivityForPostListByThreadId(getActivity(), item.getTid());
            }
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof NoticeCountEvent) {
            NoticeCountModel model = ((NoticeCountEvent) event).getNoticeCount();
            if(model.getFansNotice() != 0
                    || model.getMentionNotice()  != 0
                    || model.getNotice()  != 0
                    || model.getPrivateMessageNotice()  != 0
                    || model.getRateNotice() != 0
                    ) {
                mHasNotification = true;
            } else {
                mHasNotification = false;
            }
            getActivity().invalidateOptionsMenu();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setActivityTitle();
    }

    protected void setActivityTitle() {
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            mainActivity.onSectionAttached(getString(R.string.drawer_item_timeline));
        }
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return UIUtils.getInsets(getActivity(), mCollectionView, true, getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height));
    }


    @Override
    protected void onCollectionViewInitComplete() {
        super.onCollectionViewInitComplete();
        mCollectionView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Picasso.with(getActivity()).resumeTag(LOAD_IMAGE_TASK_TAG);
                } else {
                    Picasso.with(getActivity()).pauseTag(LOAD_IMAGE_TASK_TAG);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
}
