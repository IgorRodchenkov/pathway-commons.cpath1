package org.mskcc.pathdb.xmlrpc;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcHandler;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;

import java.util.Vector;

/**
 * XML-RPC Service for Submitting New Data to the CPath Database.
 *
 * @author Ethan Cerami
 */
public class SubmitData implements XmlRpcHandler {

    /**
     * Execute XML-RPC Service.
     *
     * @param methodName Method Name.
     * @param params     Vector of Parameters.
     * @return Return object.
     * @throws XmlRpcException Indicates Error, propogaged back to Client.
     */
    public Object execute(String methodName, Vector params)
            throws XmlRpcException {
        //  Get Data in First Parameter.
        String data = (String) params.get(0);

        try {
            //  Import to Database.
            DaoImport dbImport = new DaoImport();
            dbImport.addRecord("Cytoscape Submission", data);
        } catch (DaoException e) {
            throw new XmlRpcException(1, e.toString());
        }
        return new String("Data Submission Successful!");
    }
}