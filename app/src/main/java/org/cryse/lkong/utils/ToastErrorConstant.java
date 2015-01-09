package org.cryse.lkong.utils;

import org.cryse.lkong.R;

public class ToastErrorConstant {
    public static final int TOAST_FAILURE_USER_INFO = 19;
    public static final int TOAST_FAILURE_FORUM_LIST = 20;

    public static int errorCodeToStringRes(int errorCode) {
        switch (errorCode) {
            case TOAST_FAILURE_USER_INFO:
                return R.string.toast_failure_get_user_info;
            case TOAST_FAILURE_FORUM_LIST:
                return R.string.toast_failure_get_forum_list;
            default:
                throw new IllegalArgumentException("Unknown error code.");
        }
    }
}
