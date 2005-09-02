package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.TestCase;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.mskcc.pathdb.schemas.biopax.BioPaxUtil;
import org.mskcc.pathdb.schemas.biopax.BioPaxElementFilter;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.xml.XmlUtil;
import org.jdom.Element;

/**
 * Tests the BioPaxElement Filter Class.
 *
 * @author Ethan Cerami
 */
public class TestBioPaxElementFilter extends TestCase {

    /**
     * Tests the BioPAX Element Filter.
     * @throws Exception All Errors.
     */
    public void testBioPaxElementFilter() throws Exception {
        FileReader file = new FileReader
                ("testData/biopax/biopax1_sample1.owl");
        BioPaxUtil util = new BioPaxUtil(file, new ProgressMonitor());

        ArrayList pathwayList = util.getPathwayList();
        assertEquals(1, pathwayList.size());

        Element pathway = (Element) pathwayList.get(0);
        List children = pathway.getChildren();
        assertEquals (14, children.size());

        BioPaxElementFilter.retainCoreElementsOnly(pathway);
        children = pathway.getChildren();
        assertEquals (7, children.size());
        for (int i=0; i<children.size(); i++) {
            Element e = (Element) children.get(i);
            String name = e.getName();
            switch (i) {
                case 0:
                    assertEquals (BioPaxConstants.COMMENT_ELEMENT, name);
                    break;
                case 1:
                    assertEquals (BioPaxConstants.XREF_ELEMENT, name);
                    break;
                case 2:
                    assertEquals (BioPaxConstants.XREF_ELEMENT, name);
                    break;
                case 3:
                    assertEquals (BioPaxConstants.ORGANISM_ELEMENT, name);
                    break;
                case 4:
                    assertEquals (BioPaxConstants.NAME_ELEMENT, name);
                    break;
                case 5:
                    assertEquals (BioPaxConstants.SHORT_NAME_ELEMENT, name);
                    break;
                case 6:
                    assertEquals (BioPaxConstants.XREF_ELEMENT, name);
                    break;

            }
        }
    }

    /**
     * Gets Test Description.
     *
     * @return Description.
     */
    public String getName() {
        return "Test that we can filter BioPAX Documents for core "
            + "elements only";
    }
}
