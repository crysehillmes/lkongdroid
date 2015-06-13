package org.cryse.lkong.utils.snackbar;

public interface SnackbarSupport {
    void showSnackbar(CharSequence text, SimpleSnackbarType type, Object... args);
    void showSnackbar(int errorCode, SimpleSnackbarType type, Object... args);
}
