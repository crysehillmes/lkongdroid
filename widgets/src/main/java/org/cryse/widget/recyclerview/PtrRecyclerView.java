package org.cryse.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;

import org.cryse.widget.R;

public class PtrRecyclerView extends PullToRefreshBase<RecyclerView> {

    public PtrRecyclerView(Context context) {
        super(context);
    }

    public PtrRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PtrRecyclerView(Context context, Mode mode) {
        super(context, mode);
    }

    public PtrRecyclerView(Context context, Mode mode, Class<? extends LoadingLayout> loadingLayoutClazz) {
        super(context, mode, loadingLayoutClazz);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attributeSet) {
        RecyclerView recyclerView = new RecyclerView(context, attributeSet);
        recyclerView.setId(R.id.ptr_recyclerview);
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        RecyclerView recyclerView = getRefreshableView();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (null != adapter) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                return (gridLayoutManager.findLastCompletelyVisibleItemPosition() == (adapter.getItemCount() - 1));
            } else if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                return (linearLayoutManager.findLastCompletelyVisibleItemPosition() == (adapter.getItemCount() - 1));
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] positions = staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(null);
                for(int position : positions) {
                    if(position == adapter.getItemCount() - 1)
                        return true;
                }
            } else {
                throw new IllegalStateException();
            }
        }
        return false;
    }

    @Override
    protected boolean isReadyForPullStart() {
        RecyclerView recyclerView = getRefreshableView();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (null != adapter) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            if (layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
                return gridLayoutManager.findFirstCompletelyVisibleItemPosition() == 0;
            } else if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                return linearLayoutManager.findFirstCompletelyVisibleItemPosition() == 0 ;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                int[] positions = staggeredGridLayoutManager.findFirstCompletelyVisibleItemPositions(null);
                for(int position : positions) {
                    if(position == 0)
                        return true;
                }
            } else {
                throw new IllegalStateException();
            }
        }
        return false;
    }
}