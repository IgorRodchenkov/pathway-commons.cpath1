package org.mskcc.pathdb.test.format;

import junit.framework.TestCase;
import org.mskcc.pathdb.format.PsiFormatter;
import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.test.TestConstants;
import org.mskcc.pathdb.xml.psi.Entry;
import org.mskcc.pathdb.xml.psi.InteractorList;
import org.mskcc.pathdb.xml.psi.ProteinInteractor;
import org.mskcc.pathdb.xml.psi.Xref;
import org.mskcc.pathdb.xml.psi.PrimaryRef;
import org.mskcc.pathdb.xml.psi.SecondaryRef;

import java.io.StringWriter;
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
        Entry entry = formatter.getPsiXml();

        validateProteinInteractor(entry);

        boolean isValid = entry.isValid();
        assertTrue ("PSI XML is Valid", isValid);
    }

    /**
     * Validate Protein Interactor.
     * @param entry Castor Entry object.
     */
    private void validateProteinInteractor(Entry entry) {
        HashSet set = new HashSet();
        InteractorList interactorList = entry.getInteractorList();
        ProteinInteractor protein = interactorList.getProteinInteractor(0);
        Xref xref = protein.getXref();
        PrimaryRef primaryRef = xref.getPrimaryRef();
        String db = primaryRef.getDb();
        String id = primaryRef.getId();
        String key = this.generateXRefKey(db, id);
        set.add (key);
        assertEquals ("Entrez GI", db);
        assertEquals ("349751", id);
        validateNonRedundantXRefs(xref, set);
    }

    /**
     * Validates that no Redundant XRefs occur.
     * @param xref Castor XRef.
     * @param set Set of External References.
     */
    private void validateNonRedundantXRefs(Xref xref, HashSet set) {
        int count = xref.getSecondaryRefCount();
        for (int i=0; i<count; i++) {
            SecondaryRef secondaryRef = xref.getSecondaryRef(i);
            String db = secondaryRef.getDb();
            String id = secondaryRef.getId();
            String key = this.generateXRefKey(db, id);
            if (set.contains(key)) {
                fail ("Redundant External References found");
            } else {
                set.add(key);
            }
        }
    }

    /**
     * Generates XRef Key.
     * @param db Database String.
     * @return id ID String.
     */
    private String generateXRefKey(String db, String id) {
        String key = db + "." + id;
        return key;
    }
}