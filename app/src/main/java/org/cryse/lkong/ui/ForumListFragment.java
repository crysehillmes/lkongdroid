package org.cryse.lkong.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;
import org.cryse.lkong.ui.common.AbstractFragment;

import butterknife.ButterKnife;

public class ForumListFragment extends AbstractFragment {
    @Override
    protected void injectThis() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_forum_list, null);
        ButterKnife.inject(this, contentView);
        return contentView;
    }
}
