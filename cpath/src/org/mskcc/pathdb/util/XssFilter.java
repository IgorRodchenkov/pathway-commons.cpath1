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
package org.mskcc.pathdb.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * General Filter to preventing Cross-Site Scripting Attacks.
 * The filter works by only accepting those characters which have been
 * deemed safe, e.g. [A-Za-z0-9*@.' _-]
 * All other unsafe characters, e.g. angle brackets, parenthesis, ampersand,
 * etc. are filtered out and replaced with the _ character.
 * <p/>
 * This follows the recommended CERT practice:
 * "The recommended method is to select the set of characters that is
 * known to be safe rather than excluding the set of characters that
 * might be bad."
 * <p/>
 * This class can be used to filter incoming data from a client request,
 * or outgoing data from the server.  Again, according to CERT:
 * <p/>
 * "The filtering process can be done as part of the data input process,
 * the data output process, or both. Filtering the data during the
 * output process, just before it is rendered as part of the dynamic page,
 * is recommended. Done correctly, this approach ensures that all dynamic
 * content is filtered. Filtering on the input side is less effective
 * because dynamic content can be entered into a web sites database(s)
 * via methods other than HTTP."
 * <p/>
 * Full details regarding the CERT recommendations are available at:
 * http://www.cert.org/tech_tips/malicious_code_mitigation.html
 *
 * @author Ethan Cerami
 */
public class XssFilter {

    /**
     * Filters for safe character data only.
     *
     * @param data Data from client request, e.g. URL parameter, cookie, etc.
     * @return same string with safe characters only.
     */
    public static String filter(String data) {
        String filtered = data.replaceAll("[<>]", "_");
        return filtered;
    }

    /**
     * Recreates the Incoming User URL, and returns safe character data only.
     * This is useful if you need to recreate the incoming link.
     * For example, the cPath "printer friendly" links need to recreate the
     * incoming URL, and append a stylesheet property.  This method
     * ensures that the printer friendly link contains safe character data only.
     *
     * @param base     URL Base, e.g. "/home.do".
     * @param paramMap Parameter Map of all URL parameters.
     * @return a safe URL.
     */
    public static StringBuffer getUrlFiltered(String base, Map paramMap) {
        // Strip leading slash, if it exists.
        if (base.startsWith("/")) {
            base = base.substring(1);
        }

        //  Append all Form Parameters
        StringBuffer url = new StringBuffer(base);
        url.append("?");

        HashMap filteredMap = filterAllParameters(paramMap);
        Iterator names = filteredMap.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            String value = (String) filteredMap.get(name);
            url.append(name + "=" + value + "&");
        }
        return url;
    }

    /**
     * Filters all Parameters to include only safe characters.
     * <p/>
     * Given a HashMap of String arrays, this method extracts the zeroeth
     * element, and normalizes everything to a single filtered String.
     * <p/>
     * Important Note:  If a parameter has more than one value, e.g. a
     * checkbox, this method can result is loss of data.
     *
     * @param paramMap Map of Name/Value Pairs (array string values).
     * @return HashMap of Name/Value Pairs (single string values).
     */
    public static HashMap filterAllParameters(Map paramMap) {
        HashMap newMap = new HashMap();
        Iterator names = paramMap.keySet().iterator();
        while (names.hasNext()) {
            String name = (String) names.next();
            String values[] = (String[]) paramMap.get(name);
            //  Extract Only the Zeroeth Value;  Ignore all others.
            if (values[0] != null) {
                //  Filter to safe characters only
                String value = XssFilter.filter(values[0]);
                newMap.put(name, value);
            }
        }
        return newMap;
    }
}