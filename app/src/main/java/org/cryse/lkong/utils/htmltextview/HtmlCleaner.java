package org.cryse.lkong.utils.htmltextview;

import org.jsoup.Jsoup;
import org.jsoup.examples.HtmlToPlainText;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;

public class HtmlCleaner {
    public static Document fixTagBalanceAndRemoveEmpty(String html, Whitelist whitelist) {
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
            if(elementTagNameEquals(pInLi.nextElementSibling(), "br"))
                pInLi.nextElementSibling().remove();
        }
        for (Element pInLi: fixedDoc.select("blockquote")) {
            if(elementTagNameEquals(pInLi.nextElementSibling(), "br"))
                pInLi.nextElementSibling().remove();
        }
        /*for (Element pHasImg: fixedDoc.select("p:has(img)")) {
            pHasImg.unwrap();
        }*/
        /*for (Element img: fixedDoc.select("img")) {
            if(img.nextElementSibling() != null && !img.nextElementSibling().tagName().equalsIgnoreCase("br"))
                img.after("<br>");
        }*/
        for (Element element : fixedDoc.select("*")) {
            if (!element.hasText() && element.isBlock() && elementTagNameNotEquals(element, "img")
                    && elementTagNameNotEquals(element, "ul")
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
        for (Element aAt: fixedDoc.select("a")) {
            if(aAt.hasText() && (aAt.children() == null || (aAt.children() != null && aAt.children().size() == 0))) {
                String content = aAt.html().trim();
                if(content.startsWith("@")) {
                    aAt.attr("href", "lkong://lkonguser_" + content.substring(1));
                }
            }
        }
        for (Element pInLi: fixedDoc.select("p")) {
                pInLi.after("<br>");
            pInLi.unwrap();
        }
        return fixedDoc;
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

    private static boolean elementTagNameEquals(Element element, String tagName) {
        return element != null && element.tagName().equalsIgnoreCase(tagName);
    }

    private static boolean elementTagNameNotEquals(Element element, String tagName) {
        return element != null && !element.tagName().equalsIgnoreCase(tagName);
    }

    public static String htmlToPlain(String html) {
        Document document = Jsoup.parseBodyFragment(html);
        HtmlToPlainText htmlToPlainText = new HtmlToPlainText();
        return htmlToPlainText.getPlainText(document);
    }

    public static String htmlToPlainReplaceImg(String html, String replaceTo) {
        Document document = Jsoup.parseBodyFragment(html);
        for (Element element : document.select("i")) {
            element.after(replaceTo);
            element.remove();
        }
        HtmlToPlainText htmlToPlainText = new HtmlToPlainText();
        return htmlToPlainText.getPlainText(document);
    }
}
