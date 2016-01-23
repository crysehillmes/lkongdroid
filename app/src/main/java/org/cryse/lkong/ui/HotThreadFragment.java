package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.malinskiy.superrecyclerview.OnMoreListener;

import org.cryse.lkong.R;
import org.cryse.lkong.account.LKAuthObject;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.model.HotThreadModel;
import org.cryse.lkong.presenter.HotThreadPresenter;
import org.cryse.lkong.ui.adapter.HotThreadAdapter;
import org.cryse.lkong.ui.navigation.AppNavigation;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.view.SimpleCollectionView;

import java.util.List;

import javax.inject.Inject;

public class HotThreadFragment extends SimpleCollectionFragment<
        HotThreadModel,
        HotThreadAdapter,
        HotThreadPresenter> implements SimpleCollectionView<HotThreadModel> {
    private static final String LOG_TAG = ForumsFragment.class.getName();
    private static final String KEY_IS_DIGEST = "key_is_digest";
    AppNavigation mNavigation = new AppNavigation();
    private boolean mIsDigest;

    @Inject
    HotThreadPresenter mPresenter;

    public static HotThreadFragment newInstance(boolean isDigest) {
        HotThreadFragment fragment = new HotThreadFragment();
        Bundle args = new Bundle();
        args.putBoolean(KEY_IS_DIGEST, isDigest);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
        Bundle args = getArguments();
        if(args != null && args.containsKey(KEY_IS_DIGEST)) {
            mIsDigest = args.getBoolean(KEY_IS_DIGEST);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        return R.layout.fragment_simple_collection;
    }

    @Override
    protected HotThreadPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    protected HotThreadAdapter createAdapter(List<HotThreadModel> itemList) {
        return new HotThreadAdapter(getActivity(), mATEKey, mItemList);
    }

    @Override
    protected void loadData(LKAuthObject authObject, long start, boolean isLoadingMore, Object... extraArgs) {
        getPresenter().loadHotThreads(mIsDigest);
    }

    @Override
    protected void onItemClick(View view, int position, long id) {
        int itemIndex = position - mCollectionAdapter.getHeaderViewCount();
        if(itemIndex >= 0 && itemIndex < mCollectionAdapter.getItemList().size()) {
            HotThreadModel item = mCollectionAdapter.getItem(position);
            mNavigation.openActivityForPostListByThreadId(getActivity(), item.tid);
        }
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
    }

    @Override
    protected OnMoreListener getOnMoreListener() {
        return null;
    }

    @Override
    protected RecyclerView.LayoutManager getRecyclerViewLayoutManager() {
        return new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.forumlist_detail_column_count));
    }

    @Override
    protected UIUtils.InsetsValue getRecyclerViewInsets() {
        return null;
    }
}