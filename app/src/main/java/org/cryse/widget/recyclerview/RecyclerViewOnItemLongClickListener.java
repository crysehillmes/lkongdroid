package org.cryse.widget.recyclerview;

import android.view.View;

public interface RecyclerViewOnItemLongClickListener {
    boolean onItemLongClick(View view, int position, long id);
}