package org.mskcc.pathdb.test.sql.assembly;

import junit.framework.TestCase;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.xdebug.XDebug;

/**
 * Tests the XML Assembly Functionality.
 *
 * <B>Note:</B>  In order for this test to succeed, the dbData/bootstrap.sql
 * file must already be loaded into the database.  To do so, simply type:
 * "ant boot" at the command line.
 *
 * @author Ethan Cerami
 */
public class TestAssembly extends TestCase {

    /**
     * Tests the XML Assembly Functionality.
     * @throws Exception All Exceptions
     */
    public void testAssembly() throws Exception {

        //  Assemble Interaction with specified cPath ID (hard-coded value)
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.getXmlAssembly(4, xdebug);
        String xmlAssembly = assembly.getXmlAssembly();

        //  Verify that Assembled XML Record contains both interactors
        //  and references to those interactors.
        int interactor1 = xmlAssembly.indexOf("<fullName>60 kDa chaperonin");
        int interactor2 = xmlAssembly.indexOf("<fullName>major prion");
        int interactorRef1 = xmlAssembly.indexOf
                ("<proteinInteractorRef ref=\"2\"/>");
        int interactorRef2 = xmlAssembly.indexOf
                ("<proteinInteractorRef ref=\"3\"/>");
        assertTrue (interactor1 >= 1);
        assertTrue (interactor2 >= 1);
        assertTrue (interactorRef1 >= 1);
        assertTrue (interactorRef2 >= 1);
    }
}