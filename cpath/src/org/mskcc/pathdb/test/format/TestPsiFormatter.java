package org.mskcc.pathdb.test.format;

import junit.framework.TestCase;
import org.mskcc.pathdb.format.PsiFormatter;
import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.test.TestConstants;
import org.mskcc.pathdb.xml.psi.InteractorList;

import java.util.ArrayList;
import java.io.StringWriter;

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
                service.getInteractions(TestConstants.SAMPLE_ORF_1);
        PsiFormatter formatter = new PsiFormatter(interactions);
        InteractorList interactorList = formatter.getPsiXml();
        boolean isValid = interactorList.isValid();
        System.out.println("Is Valid?  " + isValid);
        StringWriter writer = new StringWriter();
        interactorList.marshal(writer);
        System.out.println(writer.toString());
    }
}