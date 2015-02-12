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
        for (Element pInLi: fixedDoc.select("li p")) {
            pInLi.unwrap();
        }
        for (Element pHasImg: fixedDoc.select("p:has(img)")) {
            pHasImg.unwrap();
        }
        for (Element img: fixedDoc.select("img")) {
            if(img.nextElementSibling() != null && !img.nextElementSibling().tagName().equalsIgnoreCase("br"))
                img.after("<br>");
        }
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
        /*for (Element brChild: fixedDoc.select("body > br")) {
            brChild.remove();
        }*/
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
