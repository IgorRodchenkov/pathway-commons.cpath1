package org.mskcc.pathdb.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.mskcc.pathdb.sql.DatabaseImport;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Vector;

/**
 * XML-RPC Service for Submitting New Data to the CPath Database.
 *
 * @author Ethan Cerami
 */
public class SubmitData implements XmlRpcHandler {

    /**
     * Execute XML-RPC Service.
     * @param methodName Method Name.
     * @param params Vector of Parameters.
     * @return Return object.
     * @throws XmlRpcException Indicates Error, propogaged back to Client.
     */
    public Object execute(String methodName, Vector params)
            throws XmlRpcException {
        //  Get Data in First Parameter.
        String data = (String) params.get(0);

        //  Import to Database.
        DatabaseImport dbImport = new DatabaseImport();

        try {
            dbImport.addImportRecord(data);
        } catch (NoSuchAlgorithmException e) {
            throw new XmlRpcException(1, e.toString());
        } catch (SQLException e) {
            throw new XmlRpcException(2, e.toString());
        } catch (ClassNotFoundException e) {
            throw new XmlRpcException(3, e.toString());
        } catch (IOException e) {
            throw new XmlRpcException(4, e.toString());
        }
        return new String("Data Submission Successful!");
    }
}