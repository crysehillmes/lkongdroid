package org.cryse.lkong.ui.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.lkong.R;

import java.util.ArrayList;
import java.util.List;

public class NavigationDrawerAdapter extends BindableAdapter<NavigationDrawerItem> {

    private List<NavigationDrawerItem> items = new ArrayList<NavigationDrawerItem>();

    public NavigationDrawerAdapter(Context context) {
        super(context);
    }

    public void addItem(NavigationDrawerItem items) {
        this.items.add(items);
        notifyDataSetChanged();
    }

    @Override
    public NavigationDrawerItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View newView(LayoutInflater inflater, int position, ViewGroup container) {
        return inflater.inflate(R.layout.navigation_drawer_item, container, false);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public void bindView(NavigationDrawerItem item, int position, View view) {
        ((NavigationDrawerItemView) view).bindTo(item, position);

    }
}
