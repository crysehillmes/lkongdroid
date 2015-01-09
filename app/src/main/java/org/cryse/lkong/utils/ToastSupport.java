package org.cryse.lkong.utils;

public interface ToastSupport {
    public static final int TOAST_INFO = 0;
    public static final int TOAST_ALERT = 1;
    public static final int TOAST_CONFIRM = 2;


    public void showToast(String text, int toastType);
}
