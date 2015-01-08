package org.cryse.lkong.ui.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.cryse.lkong.R;
import org.cryse.lkong.ui.MainActivity;

public abstract class MainActivityFragment extends AbstractFragment {
    public abstract String getFragmentTitle();
    protected MenuItem mChangeThemeMenuItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mChangeThemeMenuItem = menu.findItem(R.id.action_change_theme);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if(mChangeThemeMenuItem != null) {
            if(isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_light_theme);
            else
                mChangeThemeMenuItem.setTitle(R.string.action_dark_theme);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_change_theme:
                getThemedActivity().setNightMode(!isNightMode());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            mainActivity.onSectionAttached(getFragmentTitle());
        }
    }
}
