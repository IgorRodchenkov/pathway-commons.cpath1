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