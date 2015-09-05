package org.cryse.lkong.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;

import org.cryse.lkong.R;

public class FloatingActionButtonEx extends FloatingActionButton {
    private int mScrollThreshold;
    public FloatingActionButtonEx(Context context) {
        super(context);
    }

    public FloatingActionButtonEx(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatingActionButtonEx(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScrollThreshold = getResources().getDimensionPixelOffset(R.dimen.fab_scroll_threshold);

    }

    public void attachToSuperRecyclerView(@NonNull SuperRecyclerView recyclerView) {
        attachToSuperRecyclerView(recyclerView, null);
    }

    public void attachToSuperRecyclerView(@NonNull SuperRecyclerView recyclerView, ScrollDirectionListener listener) {
        RecyclerViewScrollDetectorImpl scrollDetector = new RecyclerViewScrollDetectorImpl();
        scrollDetector.setListener(listener);
        scrollDetector.setScrollThreshold(mScrollThreshold);
        recyclerView.setOnScrollListener(scrollDetector);
    }

    private class RecyclerViewScrollDetectorImpl extends RecyclerViewScrollDetector {
        private ScrollDirectionListener mListener;

        private void setListener(ScrollDirectionListener scrollDirectionListener) {
            mListener = scrollDirectionListener;
        }

        @Override
        public void onScrollDown() {
            show();
            if (mListener != null) {
                mListener.onScrollDown();
            }
        }

        @Override
        public void onScrollUp() {
            hide();
            if (mListener != null) {
                mListener.onScrollUp();
            }
        }
    }

    abstract class RecyclerViewScrollDetector extends RecyclerView.OnScrollListener {
        private int mScrollThreshold;

        abstract void onScrollUp();

        abstract void onScrollDown();

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            boolean isSignificantDelta = Math.abs(dy) > mScrollThreshold;
            if (isSignificantDelta) {
                if (dy > 0) {
                    onScrollUp();
                } else {
                    onScrollDown();
                }
            }
        }

        public void setScrollThreshold(int scrollThreshold) {
            mScrollThreshold = scrollThreshold;
        }
    }
}
