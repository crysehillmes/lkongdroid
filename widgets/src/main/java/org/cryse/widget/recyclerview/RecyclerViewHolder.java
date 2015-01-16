package org.cryse.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public abstract class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public RecyclerViewHolder(View itemView) {
        super(itemView);
    }

    public View getOnClickView() {
        return itemView;
    }
}
