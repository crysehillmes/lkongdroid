package org.cryse.lkong.utils.share;

import android.content.Context;

import org.cryse.lkong.R;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.utils.LKongUrlBuilder;
import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;

public class ShareContentBuilder {
    public static String buildSharePostContent(Context context, ThreadInfoModel threadInfo, int page, PostModel postModel) {
        StringBuilder stringBuilder = new StringBuilder();
        String threadTitle = threadInfo.getSubject();
        String postAuthor = postModel.getAuthor().getUserName();
        Document document = Jsoup.parseBodyFragment(postModel.getMessage());
        HtmlToPlainText htmlToPlainText = new HtmlToPlainText();
        String plainMessage = htmlToPlainText.getPlainText(document);
        /*if(plainMessage.length() > 200) {
            plainMessage = plainMessage.substring(0, 200) + "...";
        }*/
        stringBuilder
                .append(context.getString(R.string.format_share_title, threadTitle))
                .append(" ")
                .append(postAuthor)
                .append(": ")
                .append(plainMessage)
                .append(" ")
                .append(LKongUrlBuilder.buildPostUrl(threadInfo.getTid(), page, postModel.getPid()))
                .append(context.getString(R.string.text_share_from));
        return stringBuilder.toString();
    }

    public static String buildShareThreadContent(Context context, ThreadInfoModel threadInfo) {
        StringBuilder stringBuilder = new StringBuilder();
        String threadTitle = threadInfo.getSubject();
        stringBuilder
                .append(threadTitle)
                .append(" ")
                .append(LKongUrlBuilder.buildThreadUrl(threadInfo.getTid()))
                .append(" ")
                .append(context.getString(R.string.text_share_from));
        return stringBuilder.toString();
    }
}
