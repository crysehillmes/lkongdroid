package org.apache.commons.lang3;

import org.apache.commons.lang3.text.translate.AggregateTranslator;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.EntityArrays;
import org.apache.commons.lang3.text.translate.LookupTranslator;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;

public class StringEscapeUtils {
public static final String unescapeHtml4(String input) {
        return UNESCAPE_HTML4.translate(input);
}

    public static final CharSequenceTranslator UNESCAPE_HTML4 =
                    new AggregateTranslator(
                        new LookupTranslator(EntityArrays.BASIC_UNESCAPE()),
                        new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE()),
                        new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE()),
                        new NumericEntityUnescaper()
            );
}
