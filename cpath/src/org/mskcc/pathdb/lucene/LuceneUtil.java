// $Id: LuceneUtil.java,v 1.5 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.lucene;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lucene Utilities.
 *
 * @author Manda Wilson.
 */
public class LuceneUtil {
    private static final Pattern WORD_WITH_PLUS_MINUS =
            Pattern.compile("(\\s*)(\\w+:)?(\\S+[+-]\\S*)(\\s*)");
    private static final Pattern UNQUOTED_STRINGS =
            Pattern.compile("([^\"]*)(\"[^\"]*\")([^\"]*)");

    /**
     * Takes query - looks for all parts of query that are not in quotes
     * and puts them in quotes if they contain a - or +.
     *
     * @param query String Query
     * @return cleaned up query
     */
    public static String cleanQuery(String query) {
        // find sections of query that are not already in quotes
        StringBuffer newQuery = new StringBuffer();
        // we need to store all the bits of query
        Matcher matcher = UNQUOTED_STRINGS.matcher(query);
        boolean quoteFound = false;
        // assume we don't find any quotes at all - in this case we will
        // check original string for + -
        while (matcher.find()) { // while we can find this pattern
            quoteFound = true;
            // groups 1 and 3 of regex must be checked for + - because
            // they are not in quotes
            newQuery.append(quoteIfPlusMinus(matcher.group(1)));
            newQuery.append(matcher.group(2));
            newQuery.append(quoteIfPlusMinus(matcher.group(3)));
        }
        if (!quoteFound) {
            return quoteIfPlusMinus(query);
        }
        return newQuery.toString();
    }

    private static String quoteIfPlusMinus(String str) {
        Matcher matcher = WORD_WITH_PLUS_MINUS.matcher(str);
        // group 2 is "fieldname:" - this may or may not be there
        // group 3 is the string that needs quotes around it
        String newStr = matcher.replaceAll("$1$2\"$3\"$4");
        return newStr;
    }
}
