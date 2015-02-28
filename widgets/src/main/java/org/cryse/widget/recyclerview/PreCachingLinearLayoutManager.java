package org.cryse.widget.recyclerview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class PreCachingLinearLayoutManager extends LinearLayoutManager {
    private int mExtraLayoutSpace = 0;
    public PreCachingLinearLayoutManager(Context context) {
        super(context);
    }

    public PreCachingLinearLayoutManager(Context context, int extraLayoutSpace) {
        super(context);
        this.mExtraLayoutSpace = extraLayoutSpace;
    }

    public PreCachingLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public PreCachingLinearLayoutManager(Context context, int orientation, boolean reverseLayout, int extraLayoutSpace) {
        super(context, orientation, reverseLayout);
        this.mExtraLayoutSpace = extraLayoutSpace;
    }

    public void setExtraLayoutSpace(int extraLayoutSpace) {
        this.mExtraLayoutSpace = extraLayoutSpace;
    }

    @Override
    protected int getExtraLayoutSpace(RecyclerView.State state) {
        if (mExtraLayoutSpace > 0) {
            return mExtraLayoutSpace;
        }
        return super.getExtraLayoutSpace(state);
    }
}
