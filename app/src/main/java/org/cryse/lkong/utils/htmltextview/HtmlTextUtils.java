package org.cryse.lkong.utils.htmltextview;

import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import org.apache.commons.lang3.StringEscapeUtils;

public class HtmlTextUtils {
    public static Spanned htmlToSpanned(String html, Html.ImageGetter imageGetter, Html.TagHandler tagHandler) {
        if(TextUtils.isEmpty(html)) {
            return new SpannableString("");
        } else {
            return HtmlToSpannedConverter.fromHtml(html, imageGetter, tagHandler);
        }
    }

    public static String spannedToHtml(Spanned text) {
        return StringEscapeUtils.unescapeHtml4(android.text.Html.toHtml(text));
    }
}
