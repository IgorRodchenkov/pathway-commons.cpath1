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
package org.mskcc.pathdb.taglib;

import org.mskcc.dataservices.schemas.psi.NamesType;
import org.mskcc.pathdb.util.XmlUtil;

/**
 * Misc Utililty Method Useful for Multiple Tag Library Classes.
 *
 * @author Ethan Cerami
 */
public class TagUtil {
    /**
     * Name Not Available Label.
     */
    public static final String NAME_NOT_AVAILABLE = "Name Not Available";

    /**
     * Max Number of Chars to Display for Labels.
     */
    public static final int MAX_LENGTH = 40;

    /**
     * Gets Label for Name Object.
     *
     * @param names NamesType Object.
     * @return Label for Display in UI.
     */
    public static String getLabel(NamesType names) {
        if (names == null) {
            return NAME_NOT_AVAILABLE;
        }
        String shortLabel = names.getShortLabel();
        String fullName = names.getFullName();
        boolean hasShortLabel = checkForExistence(shortLabel);
        boolean hasFullName = checkForExistence(fullName);

        //  Normalize Text (removes unnecessary whitespace).
        if (hasShortLabel) {
            shortLabel = XmlUtil.normalizeText(shortLabel);
        }
        if (hasFullName) {
            fullName = XmlUtil.normalizeText(fullName);
        }

        if (hasShortLabel && hasFullName) {
            return new String(shortLabel + ": " + fullName);
        } else if (hasShortLabel) {
            return shortLabel;
        } else if (hasFullName) {
            return fullName;
        } else {
            return NAME_NOT_AVAILABLE;
        }
    }

    /**
     * Truncates the Specified label to MAX_LENGTH Chars.
     *
     * @param label Text label.
     * @return Text label (possibly truncated).
     */
    public static String truncateLabel(String label) {
        String newLabel = label;
        if (label.length() > MAX_LENGTH) {
            newLabel = label.substring(0, MAX_LENGTH) + "...";
        }
        return newLabel;
    }

    public static String createLink(String toolTip, String href, String label) {
        StringBuffer html = new StringBuffer();
        html.append("<A TITLE='" + toolTip + "' HREF='"
                + href + "'>" + label + "</A>");
        return html.toString();
    }

    private static boolean checkForExistence(String label) {
        if (label != null && label.length() > 0) {
            return true;
        } else {
            return false;
        }
    }
}
