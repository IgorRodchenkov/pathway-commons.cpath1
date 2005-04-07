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
package org.mskcc.pathdb.test.util;


import junit.framework.TestCase;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.util.xml.XmlValidator;

import java.util.ArrayList;

/**
 * Tests the XmlValidator Class.
 *
 * @author Ethan Cerami
 */
public class TestXmlValidator extends TestCase {

    /**
     * Tests XML Validator.
     *
     * @throws Exception All Exceptions.
     */
    public void testValidator() throws Exception {
        XmlValidator validator = new XmlValidator();
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/psi_mi/dip_sample.xml");
        ArrayList errors = validator.validate(xml,
                "net:sf:psidev:mi http://www.cbio.mskcc.org/cpath/xml/MIF.xsd");
        assertEquals(0, errors.size());

        xml = reader.retrieveContent("testData/psi_mi/invalid.xml");
        errors = validator.validate(xml,
                "net:sf:psidev:mi http://www.cbio.mskcc.org/cpath/xml/MIF.xsd");
        assertTrue(errors.size() > 0);

        xml = reader.retrieveContent
                ("testData/psi_mi/dip_no_schema_location.xml");
        errors = validator.validate(xml,
                "net:sf:psidev:mi http://psidev.sourceforge.net/mi/"
                + "xml/src/MIF.xsd");
        assertEquals(0, errors.size());
    }
}