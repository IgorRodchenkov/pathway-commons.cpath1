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
     * @param query
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
        if (!quoteFound) return quoteIfPlusMinus(query);
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