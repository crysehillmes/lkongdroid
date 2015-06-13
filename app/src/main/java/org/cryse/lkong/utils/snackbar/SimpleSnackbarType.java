package org.cryse.lkong.utils.snackbar;

import android.support.design.widget.Snackbar;

public enum SimpleSnackbarType {
    INFO(0),
    CONFIRM(1),
    WARNING(2),
    ERROR(3);

    int mSnackbarType;

    public static final int LENGTH_SHORT = Snackbar.LENGTH_SHORT;
    public static final int LENGTH_LONG = Snackbar.LENGTH_LONG;

    private SimpleSnackbarType(int snackbarType) {
        this.mSnackbarType = snackbarType;
    }

    public int getSnackbarType() {
        return this.mSnackbarType;
    }
}
