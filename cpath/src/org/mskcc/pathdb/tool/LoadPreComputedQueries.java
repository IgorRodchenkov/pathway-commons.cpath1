// $Id: LoadPreComputedQueries.java,v 1.14 2006-02-21 22:51:11 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.protocol.NeedsHelpException;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolValidator;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.query.QueryFileReader;
import org.mskcc.pathdb.sql.query.QueryManager;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Loads PreComputed Queries.
 *
 * @author Ethan Cerami
 */
public class LoadPreComputedQueries {

    /**
     * Processes all Queries in Specified File.
     *
     * @param file   File Name
     * @param xdebug XDebug Object.
     * @throws DaoException       Date Access Error.
     * @throws IOException        File Input Error.
     * @throws NeedsHelpException Protocol Error.
     * @throws ProtocolException  Protocol Error.
     * @throws QueryException     Query Error.
     */
    public void preCompute(String file, XDebug xdebug)
            throws DaoException, IOException,
            NeedsHelpException, ProtocolException, QueryException {
        QueryFileReader reader = new QueryFileReader();
        System.out.println("Clearing Database Cache");
        DaoXmlCache dao = new DaoXmlCache(xdebug);
        dao.deleteAllRecords();
        ArrayList list = reader.getProtocolRequests(file);
        for (int i = 0; i < list.size(); i++) {
            ProtocolRequest request = (ProtocolRequest) list.get(i);
            ProtocolValidator validator = new ProtocolValidator(request);
            validator.validate();
            System.out.print("Running Query:  " + request.getUri());
            QueryManager executeQueryManager = new QueryManager(xdebug);
            executeQueryManager.executeQuery(request, false);
            System.out.println(" -->  OK");
        }
    }
}
