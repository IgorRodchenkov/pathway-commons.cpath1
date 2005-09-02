package org.mskcc.pathdb.schemas.biopax;

import org.jdom.Element;

import java.util.List;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * Examines a BioPAX Document and retains the following core elements:
 * NAME, SHORT-NAME, ORGANISM, COMMENT and XREFs.  All other elements are
 * stripped out.
 *
 * @author Ethan Cerami
 */
public class BioPaxElementFilter {
    private static HashSet coreElements;

    /**
     * Retains the following core elements:  NAME, SHORT-NAME, ORGANISM,
     * COMMENT and XREFs.  All other elements are stripped out.
     * @param e Element Object.
     */
    public static void retainCoreElementsOnly (Element e) {
        if (coreElements == null) {
            initCoreElements();
        }
        List children = e.getChildren();
        List scheduledForRemoval = new ArrayList();
        for (int i=0; i<children.size(); i++) {
            Element child = (Element) children.get(i);
            String name = child.getName();
            //  If this is not a core element, schedule it for removal
            if (!coreElements.contains(name)) {
                scheduledForRemoval.add(child);
            }
        }
        for (int i=0; i<scheduledForRemoval.size(); i++) {
            Element child = (Element) scheduledForRemoval.get(i);
            e.removeContent(child);
        }
    }

    private static void initCoreElements() {
        coreElements = new HashSet();
        coreElements.add(BioPaxConstants.NAME_ELEMENT);
        coreElements.add(BioPaxConstants.SHORT_NAME_ELEMENT);
        coreElements.add(BioPaxConstants.ORGANISM_ELEMENT);
        coreElements.add(BioPaxConstants.XREF_ELEMENT);
        coreElements.add(BioPaxConstants.COMMENT_ELEMENT);
    }
}
