package org.cryse.lkong.utils.snackbar;

import org.cryse.lkong.R;

public class ToastErrorConstant {
    public static final int TOAST_FAILURE_SIGNIN = 17;
    public static final int TOAST_FAILURE_USER_INFO = 19;
    public static final int TOAST_FAILURE_FORUM_LIST = 20;
    public static final int TOAST_FAILURE_RATE_POST = 55;
    public static final int TOAST_FAILURE_SAVE_IMAGE_AS = 68;

    public static int errorCodeToStringRes(int errorCode) {
        switch (errorCode) {
            case TOAST_FAILURE_SIGNIN:
                return R.string.toast_failure_signin;
            case TOAST_FAILURE_USER_INFO:
                return R.string.toast_failure_get_user_info;
            case TOAST_FAILURE_FORUM_LIST:
                return R.string.toast_failure_get_forum_list;
            case TOAST_FAILURE_RATE_POST:
                return R.string.toast_failure_rate_post;
            case TOAST_FAILURE_SAVE_IMAGE_AS:
                return R.string.toast_failure_save_image_as;
            default:
                return R.string.toast_error_universal_try_again;
        }
    }
}
