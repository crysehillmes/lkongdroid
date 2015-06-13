package org.cryse.lkong.utils.snackbar;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;

public class SnackbarUtils {
    public static final int LENGTH_SHORT = Snackbar.LENGTH_SHORT;
    public static final int LENGTH_LONG = Snackbar.LENGTH_LONG;

    public static int typeToFontColor(SimpleSnackbarType type) {
        int fontColor = Color.WHITE;
        switch (type) {
            case CONFIRM:
                fontColor = Color.GREEN;
                break;
            case WARNING:
                fontColor = Color.YELLOW;
                break;
            case ERROR:
                fontColor = Color.RED;
                break;
            case INFO:
            default:
                fontColor = Color.WHITE;
                break;
        }
        return fontColor;
    }

    public static Snackbar makeSimple(View view, CharSequence text, SimpleSnackbarType type, int duration) {
        SpannableString content = null;
        if(text instanceof SpannableString) {
            content = (SpannableString) text;
        } else {
            content = new SpannableString(text);
        }
        int start = 0;
        int end = content.length();
        int foregroundColor = typeToFontColor(type);
        content.setSpan(new ForegroundColorSpan(foregroundColor), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return Snackbar.make(view, content, duration);
    }

    public static Snackbar makeSimple(View view, int resId, SimpleSnackbarType type, int duration) {
        return makeSimple(view, view.getResources().getText(resId), type, duration);
    }
}
