/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.util.html;

import java.util.StringTokenizer;

/**
 * Various HTML Utilities.
 *
 * @author Ethan Cerami.
 */
public class HtmlUtil {

    /**
     * Utility Method for converting a piece of regular text,
     * and preserving contents by inserting BR tags, etc.
     *
     * @param str Regular Text.
     * @return HTML Text
     */
    public static String convertToHtml(String str) {
        String html = str.replaceAll("[\\n\\r]", "<BR>");
        html = html.replaceAll(" ", "&nbsp;");
        html = html.replaceAll("\\t", "&nbsp;.....");
        html = html.replaceAll("<", "&lt;");
        html = html.replaceAll(">", "&gt;");
        return html;
    }

    /**
     * Given a string, this method will truncate any very long words to
     * the maxLength value.  This is useful for truncating long DNA or Protein
     * sequences, so that the beginning of the sequence can be safely displayed.
     * <P>
     * For example, given the string: "here is a sequence: AAAAAAAAAAAAAAAAAA",
     * and maxLength set to 10, the returned string will look like this:
     * "here is a sequence:  AAAAAAAAAA [Cont.]"
     *
     * @param str           String.
     * @param maxLength     MaxLength of Each Individual Word
     * @return              Revised String with truncated words.
     */
    public static String truncateLongWords (String str, int maxLength) {
        StringBuffer revisedStr = new StringBuffer(" ");
        StringTokenizer tokenizer = new StringTokenizer(str);
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.length() > maxLength) {
                token = token.substring(0, maxLength) + " [Cont.]";
            }
            revisedStr.append(token + " ");
        }
        return revisedStr.toString().trim();
    }
}
