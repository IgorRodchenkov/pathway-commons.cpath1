package org.mskcc.pathdb.test.sql.references;

import org.mskcc.pathdb.sql.references.TabSpaceTokenizer;
import org.mskcc.pathdb.sql.references.IndexedToken;
import junit.framework.TestCase;

/**
 * Tests the TabSpaceTokenizer Object.
 *
 * @author Ethan Cerami.
 */
public class TestTabSpaceTokenizer extends TestCase {

    /**
     * Tests the TabSpaceTokenizer.
     */
    public void testTokenizer1() {
        String line = "A B \tC D\tE F\t\tG\tH I";
        TabSpaceTokenizer tokenizer = new TabSpaceTokenizer (line);
        int index = 0;
        while (tokenizer.hasMoreElements()) {
            IndexedToken token = (IndexedToken) tokenizer.nextElement();
            if (index ==0) {
                assertEquals ("A", token.getToken());
                assertEquals (0, token.getColumnNumber());
            } else if (index == 1) {
                assertEquals ("B", token.getToken());
                assertEquals (0, token.getColumnNumber());
            } else if (index == 2) {
                assertEquals ("C", token.getToken());
                assertEquals (1, token.getColumnNumber());
            } else if (index == 3) {
                assertEquals ("D", token.getToken());
                assertEquals (1, token.getColumnNumber());
            } else if (index == 4) {
                assertEquals ("E", token.getToken());
                assertEquals (2, token.getColumnNumber());
            } else if (index == 5) {
                assertEquals ("F", token.getToken());
                assertEquals (2, token.getColumnNumber());
            } else if (index == 6) {
                assertEquals ("G", token.getToken());
                assertEquals (4, token.getColumnNumber());
            } else if (index == 7) {
                assertEquals ("H", token.getToken());
                assertEquals (5, token.getColumnNumber());
            } else if (index == 8) {
                assertEquals ("I", token.getToken());
                assertEquals (5, token.getColumnNumber());
            }
            index++;
        }
    }

    /**
     * Tests the TabSpaceTokenizer.
     */
    public void testTokenizer2() {
        //  Try an input line with no tabs;  should work.
        String line = "A B C";
        TabSpaceTokenizer tokenizer = new TabSpaceTokenizer (line);
        int index = 0;
        while (tokenizer.hasMoreElements()) {
            IndexedToken token = (IndexedToken) tokenizer.nextElement();
            if (index ==0) {
                assertEquals ("A", token.getToken());
                assertEquals (0, token.getColumnNumber());
            } else if (index == 1) {
                assertEquals ("B", token.getToken());
                assertEquals (0, token.getColumnNumber());
            } else if (index == 2) {
                assertEquals ("C", token.getToken());
                assertEquals (0, token.getColumnNumber());
            }
            index++;
        }
    }
}
