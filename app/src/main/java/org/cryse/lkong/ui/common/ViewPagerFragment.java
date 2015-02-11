package org.cryse.lkong.ui.common;

import android.os.Bundle;

public abstract class ViewPagerFragment extends AbstractFragment {
    public abstract String getFragmentTitle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}