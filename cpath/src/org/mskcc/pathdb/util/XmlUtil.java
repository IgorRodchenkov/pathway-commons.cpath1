package org.mskcc.pathdb.util;

import org.jdom.Text;

/**
 * Various XML Utility Methods.
 *
 * @author Ethan Cerami
 */
public class XmlUtil {

    /**
     * Normalizes Text.
     * Replaces all whitespace characters with a single whitespace.
     *
     * @param str Text to Normalize.
     * @return Normalized Text.
     */
    public static String normalizeText(String str) {
        Text text = new Text(str);
        return text.getTextNormalize();
    }

}
