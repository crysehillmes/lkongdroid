package org.cryse.lkong.view;

import org.cryse.lkong.utils.snackbar.SnackbarSupport;

public interface ContentView extends SnackbarSupport {
    void setLoading(Boolean value);
    Boolean isLoading();
}
