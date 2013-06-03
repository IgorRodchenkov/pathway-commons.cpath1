package org.mskcc.pathdb.util;

/**
 * URL Utility Class.
 */
public class UrlUtil {

    /**
     * Rewrites a URL.
     * example:  originalURL:  http://pathwaycommons.org/pc/record2.do
     * example:  newPath:  stable.do
     * return:   http://pathwaycommons.org/pc/stable.do
     * @param originalUrl   Original URL.
     * @param newPath       New Path.
     * @return revised URL.
     */
    public static String rewriteUrl (String originalUrl, String newPath) {
        int lastSlash = originalUrl.lastIndexOf("/");
        return originalUrl.substring(0, lastSlash) + "/" + newPath;
    }
}
