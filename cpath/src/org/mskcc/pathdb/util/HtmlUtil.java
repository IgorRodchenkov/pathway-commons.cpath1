package org.mskcc.pathdb.util;

public class HtmlUtil {

    /**
     * Utility Method for converting a piece of regular text,
     * and preserving contents by inserting BR tags, etc.
     * @param str Regular Text.
     * @return HTML TExt
     */
    public static String convertToHtml (String str) {
        String html = str.replaceAll("[\\n\\r]", "<BR>");
        html = html.replaceAll(" ", "&nbsp;");
        html = html.replaceAll("\\t", "&nbsp;.....");
        return html;
    }
}
