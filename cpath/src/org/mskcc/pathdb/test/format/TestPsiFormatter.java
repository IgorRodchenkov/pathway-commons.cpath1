package org.mskcc.pathdb.test.format;

import junit.framework.TestCase;
import org.mskcc.pathdb.format.PsiFormatter;
import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.test.TestConstants;
import org.mskcc.pathdb.xml.psi.CvType;
import org.mskcc.pathdb.xml.psi.DbReferenceType;
import org.mskcc.pathdb.xml.psi.Entry;
import org.mskcc.pathdb.xml.psi.EntrySet;
import org.mskcc.pathdb.xml.psi.ExperimentList;
import org.mskcc.pathdb.xml.psi.ExperimentListItem;
import org.mskcc.pathdb.xml.psi.ExperimentType;
import org.mskcc.pathdb.xml.psi.InteractionElementType;
import org.mskcc.pathdb.xml.psi.InteractionList;
import org.mskcc.pathdb.xml.psi.InteractorList;
import org.mskcc.pathdb.xml.psi.ProteinInteractorType;
import org.mskcc.pathdb.xml.psi.XrefType;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Tests the PSI-MI XML Formatter.
 *
 * @author Ethan Cerami
 */
public class TestPsiFormatter extends TestCase {

    /**
     * Tests the PSI Formatter.
     * @throws Exception Any Error.
     */
    public void testFormatter() throws Exception {
        GridInteractionService service = new GridInteractionService
                (TestConstants.DB_HOST, TestConstants.USER,
                        TestConstants.PASSWORD);
        ArrayList interactions =
                service.getInteractions("YHR119W");
        PsiFormatter formatter = new PsiFormatter(interactions);
        EntrySet entrySet = formatter.getPsiXml();
        Entry entry = entrySet.getEntry(0);
        validateProteinInteractor(entry);

        boolean isValid = entry.isValid();
        assertTrue("PSI XML is Valid", isValid);
    }

    /**
     * In GRID, the protein YGR162W is involved in a synthetic
     * lethality interaction.  However, this should *not* appear
     * in the PSI.
     * @throws Exception All Exceptions.
     */
    public void testFiltering() throws Exception {
        GridInteractionService service = new GridInteractionService
                (TestConstants.DB_HOST, TestConstants.USER,
                        TestConstants.PASSWORD);
        ArrayList interactions =
                service.getInteractions("YGR162W");
        PsiFormatter formatter = new PsiFormatter(interactions);
        EntrySet entrySet = formatter.getPsiXml();
        Entry entry = entrySet.getEntry(0);
        InteractionList list = entry.getInteractionList();
        for (int i = 0; i < list.getInteractionCount(); i++) {
            InteractionElementType interaction = list.getInteraction(i);
            ExperimentList expList = interaction.getExperimentList();
            int count = expList.getExperimentListItemCount();
            if (count > 0) {
                ExperimentListItem item = expList.getExperimentListItem(0);
                ExperimentType type = item.getExperimentDescription();
                CvType cvType = type.getInteractionDetection();
                String shortLabel = cvType.getNames().getShortLabel();
                if (shortLabel.equals("Synthetic Lethality")) {
                    fail("Synthetic Lethality Interaction found.");
                }
            }
        }
    }

    /**
     * Validate Protein Interactor.
     * @param entry Castor Entry object.
     */
    private void validateProteinInteractor(Entry entry) {
        HashSet set = new HashSet();
        InteractorList interactorList = entry.getInteractorList();
        ProteinInteractorType protein = interactorList.getProteinInteractor(0);
        XrefType xref = protein.getXref();
        DbReferenceType primaryRef = xref.getPrimaryRef();
        String db = primaryRef.getDb();
        String id = primaryRef.getId();
        String key = this.generateXRefKey(db, id);
        set.add(key);

        assertEquals("GRID", db);
        assertEquals("YAR003W", id);

        DbReferenceType secondaryRef = xref.getSecondaryRef(0);
        assertEquals("Entrez GI", secondaryRef.getDb());
        assertEquals("349751", secondaryRef.getId());
        validateNonRedundantXRefs(xref, set);
    }

    /**
     * Validates that no Redundant XRefs occur.
     * @param xref Castor XRef.
     * @param set Set of External References.
     */
    private void validateNonRedundantXRefs(XrefType xref, HashSet set) {
        int count = xref.getSecondaryRefCount();
        for (int i = 0; i < count; i++) {
            DbReferenceType secondaryRef = xref.getSecondaryRef(i);
            String db = secondaryRef.getDb();
            String id = secondaryRef.getId();
            String key = this.generateXRefKey(db, id);
            if (set.contains(key)) {
                fail("Redundant External References found");
            } else {
                set.add(key);
            }
        }
    }

    /**
     * Generates XRef Key.
     * @param db Database String.
     * @param id Database ID.
     * @return Key.
     */
    private String generateXRefKey(String db, String id) {
        String key = db + "." + id;
        return key;
    }
}