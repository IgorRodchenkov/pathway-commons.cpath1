package org.mskcc.pathdb.test.util;

import junit.framework.TestCase;
import org.mskcc.pathdb.util.PsiUtil;
import org.mskcc.pathdb.util.ExternalReferenceUtil;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.dataservices.bio.ExternalReference;

/**
 * Tests the ExternalReferenceUtil Class.
 *
 * @author Ethan Cerami
 */
public class TestExternalReferenceUtil extends TestCase {

    /**
     * Tests the Filter OutNonIdReferences() Method.
     * @throws Exception All Exceptions.
     */
    public void testFilterOutNonIdReferences () throws Exception {
        ExternalReference refs[] = new ExternalReference[7];
        refs[0] = new ExternalReference ("SwissProt", "P25300");
        refs[1] = new ExternalReference ("GO", "ABCD");
        refs[2] = new ExternalReference ("InterPro", "ABCD");
        refs[3] = new ExternalReference ("PubMed", "ABCD");
        refs[4] = new ExternalReference ("PDB", "ABCD");
        refs[5] = new ExternalReference ("Unigene", "ABCD");
        refs[6] = new ExternalReference ("REF_SEQ", "ABCD");
        ExternalReference filteredRefs[] =
                ExternalReferenceUtil.filterOutNonIdReferences(refs);
        //  Before filtering, we have 7 references.
        assertEquals (7, refs.length);

        //  After filtering, we have 2 references.
        assertEquals (2, filteredRefs.length);
    }
}
