package org.mskcc.pathdb.test.format;

import junit.framework.TestCase;
import org.mskcc.pathdb.format.PsiFormatter;
import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.test.TestConstants;
import org.mskcc.pathdb.xml.psi.Entry;

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
                service.getInteractions("YFR039C");
        PsiFormatter formatter = new PsiFormatter(interactions);
        Entry entry = formatter.getPsiXml();

        StringWriter writer = new StringWriter();
        entry.marshal(writer);
        System.out.println(writer.toString());

        boolean isValid = entry.isValid();
        System.out.println("Is Valid?  " + isValid);
    }
}