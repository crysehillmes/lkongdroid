package org.cryse.lkong.ui.common;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import org.cryse.lkong.R;
import org.cryse.lkong.ui.MainActivity;
import org.cryse.lkong.ui.navigation.AppNavigation;

public abstract class InActivityFragment extends AbstractFragment {
    AppNavigation mNavigation = new AppNavigation();

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
            if(isNightMode() == null)
                mChangeThemeMenuItem.setVisible(false);
            else if(isNightMode() != null && isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_light_theme);
            else if(isNightMode() != null && !isNightMode())
                mChangeThemeMenuItem.setTitle(R.string.action_dark_theme);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_change_theme:
                if(isNightMode() != null) {
                    getThemedActivity().setNightMode(!isNightMode());
                }
                return true;
            case R.id.action_open_notification:
                mNavigation.navigateToNotificationActivity(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
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
            mainActivity.onSectionAttached(getFragmentTitle());
        }
    }

    public abstract String getFragmentTitle();
}
