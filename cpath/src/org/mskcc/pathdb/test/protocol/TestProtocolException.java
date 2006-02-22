// $Id: TestProtocolException.java,v 1.7 2006-02-22 22:47:51 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.test.protocol;

import junit.framework.TestCase;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;

/**
 * Tests the ProtocolException object.
 *
 * @author Ethan Cerami
 */
public class TestProtocolException extends TestCase {
    /**
     * Expected snippet of XML Error message.
     */
    private static final String EXPECTED_ERROR_MSG =
            "<error_msg>Bad Command (command not recognized)</error_msg>";

    /**
     * Verifies that ProtocolException returns an XML document.
     *
     * @throws Exception General Error.
     */
    public void testProtocolException() throws Exception {
        ProtocolException pe = new ProtocolException
                (ProtocolStatusCode.BAD_COMMAND);
        String xml = pe.toXml();
        int index = xml.indexOf(EXPECTED_ERROR_MSG);
        assertTrue("Verifying XML Error Document Contents", index > 0);
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the Protocol Exception Object";
    }
}
