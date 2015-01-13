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
        for (Element element : fixedDoc.select("*")) {
            if (!element.hasText() && element.isBlock()) {
                element.remove();
            }
        }
        return fixedDoc.html();
    }
}
