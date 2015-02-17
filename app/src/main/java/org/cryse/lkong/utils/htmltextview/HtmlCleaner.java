package org.cryse.lkong.utils.htmltextview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class HtmlCleaner {
    public static String fixTagBalanceAndRemoveEmpty(String html, Whitelist whitelist) {
        Document originalDoc = Jsoup.parse(html);
        Cleaner cleaner = new Cleaner(whitelist);
        Document fixedDoc = cleaner.clean(originalDoc);
        // li 中有 br 和 p 会产生异常
        for (Element liChild: fixedDoc.select("li br")) {
            liChild.remove();
        }
        for (Element pInLi: fixedDoc.select("li p")) {
            pInLi.unwrap();
        }
        for (Element pInLi: fixedDoc.select("blockquote")) {
            if(pInLi.nextElementSibling() != null && pInLi.nextElementSibling().tagName().equalsIgnoreCase("br"))
                pInLi.nextElementSibling().remove();
        }
        for (Element pHasImg: fixedDoc.select("p:has(img)")) {
            pHasImg.unwrap();
        }
        /*for (Element img: fixedDoc.select("img")) {
            if(img.nextElementSibling() != null && !img.nextElementSibling().tagName().equalsIgnoreCase("br"))
                img.after("<br>");
        }*/
        for (Element element : fixedDoc.select("*")) {
            if (!element.hasText() && element.isBlock() && !element.tagName().equalsIgnoreCase("img")
                    && !element.tagName().equalsIgnoreCase("ul")
            ) {
                if(element.select("img").size() > 0)
                    continue;
                element.remove();
            }

        }
        // 不确定是否移除所有直属于 body 的 br 换行
        /*Element bodyElement = fixedDoc.body();
        Elements bodyChildren = bodyElement.children();
        for (int i = bodyChildren.size() - 1; i >= 0; i--) {
            Element bodyChild = bodyChildren.get(i);
            if(bodyChild.tagName().equalsIgnoreCase("br")) {
                bodyChild.remove();
            } else {
                break;
            }
        }*/
        for (Element pInLi: fixedDoc.select("p")) {
                pInLi.after("<br>");
            pInLi.unwrap();
        }
        return fixedDoc.html();
    }

    public static String[] processNoticeData(String html, Whitelist whitelist) {
        String[] result = new String[2];
        Document originalDoc = Jsoup.parse(html);
        for (Element element : originalDoc.select("a[href]")) {
            String dataitem = element.attr("dataitem");
            if(dataitem.startsWith("thread_")) {
                result[1] = dataitem.substring(7);
            }

        }
        Cleaner cleaner = new Cleaner(whitelist);
        Document fixedDoc = cleaner.clean(originalDoc);
        result[0] = fixedDoc.html();
        return result;
    }
}
