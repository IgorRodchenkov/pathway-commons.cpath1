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
package org.mskcc.pathdb.test.xmlrpc;

import junit.framework.TestCase;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * Tests XML-RPC Data Submission Service.
 *
 * @author Ethan Cerami.
 */
public class TestDataSubmission extends TestCase {

    /**
     * Tests Data Submission Service via XML-RPC Client.
     *
     * @throws IOException     Input Output Exception.
     * @throws XmlRpcException XML-RPC Exception.
     */
    public void testDataSubmission() throws IOException, XmlRpcException {
        XmlRpcClient xmlrpc = new XmlRpcClient
                ("http://localhost:8080/cpath/xmlrpc");
        Vector params = new Vector();
        params.addElement(this.getTextFromSampleFile());
        // this method returns a string
        String result = (String) xmlrpc.execute("import.submitData", params);
        assertEquals("Data Submission Successful!", result);
    }

    /**
     * Gets Sample PSI File from local directory.
     *
     * @return Sample PSI-MI Record.
     * @throws IOException Input Output Exception.
     */
    private String getTextFromSampleFile() throws IOException {
        FileReader fileReader = new FileReader
                ("testData/HpDemoDataset_15PPI.xml");
        StringBuffer data = new StringBuffer();
        BufferedReader reader = new BufferedReader(fileReader);
        String line = reader.readLine();
        while (line != null) {
            data.append(line + "\n");
            line = reader.readLine();
        }
        return data.toString();
    }
}
