package org.cryse.lkong.utils.htmltextview;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

public class HtmlCleaner {
    public static String fixTagBalanceAndRemoveEmpty(String html, Whitelist whitelist) {
        Document originalDoc = Jsoup.parse(html);
        Cleaner cleaner = new Cleaner(whitelist);
        Document fixedDoc = cleaner.clean(originalDoc);
        for (Element liChild: fixedDoc.select("li br")) {
            liChild.remove();
        }
        for (Element element : fixedDoc.select("*")) {
            if (!element.hasText() && element.isBlock() && !element.tagName().equalsIgnoreCase("img")
                    && !element.tagName().equalsIgnoreCase("ul")
                    && !element.tagName().equalsIgnoreCase("li")
            ) {
                if(element.select("img").size() > 0)
                    continue;
                element.remove();
            }

        }
        // 不确定是否移除所有直属于 body 的 br 换行
        /*for (Element brChild: fixedDoc.select("body > br")) {
            brChild.remove();
        }*/
        return fixedDoc.html();
    }
}
