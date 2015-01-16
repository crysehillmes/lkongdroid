package org.cryse.lkong.view;

import org.cryse.lkong.utils.ToastSupport;

public interface ContentView extends ToastSupport {
    public void setLoading(Boolean value);
    public Boolean isLoading();
}
