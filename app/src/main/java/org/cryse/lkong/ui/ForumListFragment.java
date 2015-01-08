package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;
import org.cryse.lkong.ui.common.MainActivityFragment;

import butterknife.ButterKnife;

public class ForumListFragment extends MainActivityFragment {
    public static ForumListFragment newInstance(Bundle args) {
        ForumListFragment fragment = new ForumListFragment();
        if(args != null)
            fragment.setArguments(args);
        return fragment;
    }

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_forum_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public String getFragmentTitle() {
        return getString(R.string.drawer_item_forum_list);
    }

    @Override
     public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
