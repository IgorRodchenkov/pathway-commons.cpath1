package org.mskcc.pathdb.test.taglib;

import junit.framework.TestCase;
import org.mskcc.dataservices.schemas.psi.NamesType;
import org.mskcc.pathdb.taglib.TagUtil;

/**
 * Tests the TagUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestTagUtil extends TestCase {

    /**
     * Tests the getLabel method.
     */
    public void testGetLabel() {
        //  Test 1
        NamesType name = new NamesType();
        name.setShortLabel("TNFB");
        name.setFullName("TNF-Beta");
        String label = TagUtil.getLabel(name);
        assertEquals("TNFB: TNF-Beta", label);

        //  Test 2
        name = new NamesType();
        name.setShortLabel("TNFB");
        label = TagUtil.getLabel(name);
        assertEquals("TNFB", label);

        //  Test 3
        name = new NamesType();
        name.setFullName("TNF-Beta");
        name.setShortLabel("");
        label = TagUtil.getLabel(name);
        assertEquals("TNF-Beta", label);

        //  Test 4
        name = new NamesType();
        label = TagUtil.getLabel(name);
        assertEquals(TagUtil.NAME_NOT_AVAILABLE, label);

        //  Test 5
        label = TagUtil.getLabel(null);
        assertEquals(TagUtil.NAME_NOT_AVAILABLE, label);

        // Test 6
        name = new NamesType();
        name.setFullName("TNF-Beta\n   Alpha");
        label = TagUtil.getLabel(name);
        assertEquals("TNF-Beta Alpha", label);
    }

    /**
     * Tests the truncateLabel method.
     */
    public void testTruncateLabel() {
        //  Test 1
        String label = TagUtil.truncateLabel
                ("This is a test of a long protein name with a very long name");
        assertEquals("This is a test of a long protein name wi...", label);

        //  Test 2
        label = TagUtil.truncateLabel("A Short Protein");
        assertEquals("A Short Protein", label);
    }

    public void testCreateLink() {
        String link = TagUtil.createLink("Tip", "http://www.yahoo.com",
                "Click for yahoo");
        assertEquals("<A TITLE='Tip' HREF='http://www.yahoo.com'>"
                + "Click for yahoo</A>", link);
    }
}