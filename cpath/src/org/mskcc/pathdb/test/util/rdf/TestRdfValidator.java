/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.test.util.rdf;

import junit.framework.TestCase;
import org.mskcc.pathdb.util.rdf.RdfErrorHandler;
import org.mskcc.pathdb.util.rdf.RdfValidator;
import org.xml.sax.SAXParseException;

import java.io.FileReader;
import java.util.ArrayList;

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
    public void testRdfValidator() throws Exception {
        FileReader reader = new FileReader
                ("testData/biopax/biopax1_sample3.owl");
        RdfValidator rdfValidator = new RdfValidator(reader);

        //  Validate that we have indeed found an error
        assertTrue(rdfValidator.hasErrorsOrWarnings());

        //  Validate the actual error
        RdfErrorHandler errorHandler = rdfValidator.getErrorHandler();
        ArrayList errorList = errorHandler.getErrorList();
        assertEquals(1, errorList.size());
        SAXParseException exception = (SAXParseException) errorList.get(0);
        assertEquals("{W105} Redefinition of ID: smallMolecule23",
                exception.getMessage());
        assertEquals(390, exception.getLineNumber());
        assertEquals(48, exception.getColumnNumber());

        //  Validate the Readble Error List
        String userMsg = rdfValidator.getReadableErrorList();
        assertTrue(userMsg.indexOf("W105} Redefinition of ID: smallMolecule23 "
                + "[Line=  390, Column=  48]") > 0);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the RDF Validator Utility Class";
    }
}
