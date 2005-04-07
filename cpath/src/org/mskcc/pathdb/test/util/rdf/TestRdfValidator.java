package org.mskcc.pathdb.test.util.rdf;

import org.mskcc.pathdb.util.rdf.RdfValidator;
import org.mskcc.pathdb.util.rdf.RdfErrorHandler;
import org.xml.sax.SAXParseException;

import java.io.FileReader;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * Tests the RDF Validator Utility Class.
 *
 * @author Ethan Cerami.
 */
public class TestRdfValidator extends TestCase {

    /**
     * Tests the RDF Validator against an invalid file.
     * testData/biopax/biopax1_sample3.owl contains a redundant RDF ID,
     * and should result in an RDF Error.
     *
     * @throws Exception All Exceptions.
     */
    public void testRdfValidator () throws Exception {
        FileReader reader = new FileReader
                ("testData/biopax/biopax1_sample3.owl");
        RdfValidator rdfValidator = new RdfValidator(reader);

        //  Validate that we have indeed found an error
        assertTrue (rdfValidator.hasErrorsOrWarnings());

        //  Validate the actual error
        RdfErrorHandler errorHandler = rdfValidator.getErrorHandler();
        ArrayList errorList  = errorHandler.getErrorList();
        assertEquals (1, errorList.size());
        SAXParseException exception = (SAXParseException) errorList.get(0);
        assertEquals("{W105} Redefinition of ID: smallMolecule23",
                exception.getMessage());
        assertEquals(390, exception.getLineNumber());
        assertEquals(48, exception.getColumnNumber());

        //  Validate the Readble Error List
        String userMsg = rdfValidator.getReadableErrorList();
        assertEquals("Error:  {W105} Redefinition of ID: smallMolecule23 "
            + "[Line=  390, Column=  48]\n", userMsg);
    }
}
