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
package org.mskcc.pathdb.test.sql.assembly;

import junit.framework.TestCase;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.xdebug.XDebug;

/**
 * Tests the XML Assembly Functionality.
 * <p/>
 * <B>Note:</B>  In order for this test to succeed, the dbData/bootstrap.sql
 * file must already be loaded into the database.  To do so, simply type:
 * "ant boot" at the command line.
 *
 * @author Ethan Cerami
 */
public class TestAssembly extends TestCase {

    /**
     * Tests the XML Assembly Functionality.
     *
     * @throws Exception All Exceptions
     */
    public void testAssembly() throws Exception {
        PropertyManager pManager = PropertyManager.getInstance();
        pManager.setProperty(CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION,
                "http://psidev.sourceforge.net/mi/xml/src/MIF.xsd");

        //  Assemble Interaction with specified cPath ID (hard-coded value)
        XDebug xdebug = new XDebug();
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (4, 1, xdebug);
        String xmlAssembly = assembly.getXmlString();

        //  Verify that Assembled XML Record contains both interactors
        //  and references to those interactors.
        int interactor1 = xmlAssembly.indexOf("<fullName>60 kDa chaperonin");
        int interactor2 = xmlAssembly.indexOf("<fullName>major prion");
        int interactorRef1 = xmlAssembly.indexOf
                ("<proteinInteractorRef ref=\"2\"/>");
        int interactorRef2 = xmlAssembly.indexOf
                ("<proteinInteractorRef ref=\"3\"/>");
        assertTrue(interactor1 >= 1);
        assertTrue(interactor2 >= 1);
        assertTrue(interactorRef1 >= 1);
        assertTrue(interactorRef2 >= 1);

        //  Verify Schema Location.
        int index = xmlAssembly.indexOf
                ("http://psidev.sourceforge.net/mi/xml/src/MIF.xsd");
        assertTrue(index > 0);

        System.out.println(xmlAssembly);
    }
}