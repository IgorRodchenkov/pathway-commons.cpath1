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
     * @throws IOException Input Output Exception.
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
