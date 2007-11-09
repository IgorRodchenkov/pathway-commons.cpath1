// $Id: GetBioPaxCommand.java,v 1.9 2007-11-09 20:42:33 cerami Exp $
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
package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.apache.log4j.Logger;

/**
 * Gets a Complete BioPAX Record from cPath.
 *
 * @author Ethan Cerami
 */
class GetBioPaxCommand extends Query {
    private Logger log = Logger.getLogger(GetBioPaxCommand.class);
    private ProtocolRequest request;

    /**
     * Constructor.
     * Only available via Factory Class.
     *
     * @param request ProtocolRequest Object.
     */
    GetBioPaxCommand(ProtocolRequest request) {
        this.request = request;
    }

    protected XmlAssembly executeSub() throws DaoException,
            AssemblyException, QueryException {

        //  Extract the cPath ID
        //  If we get here, the protocol validator has already verified
        //  that the query parameter has long numbers.
        long ids[] = convertQueryToLongs(request.getQuery());
        xdebug.logMsg(this, "Retrieving BioPAX Record with cPath ID(s):  "
                + request.getQuery());

        //  Go ahead and create the assembly.
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly(ids, XmlRecordType.BIO_PAX, 1,
                XmlAssemblyFactory.XML_FULL, true, xdebug);
        return assembly;
    }

        /**
     * Checks that the query is an integer value.
     */
    private long[] convertQueryToLongs(String q) {
        if (q != null) {
            try {
                String idStrs[] = q.split(",");
                long ids[] = new long[idStrs.length];
                for (int i=0; i< idStrs.length; i++) {
                    ids[i] = Long.parseLong(idStrs[i]);
                }
                return ids;
            } catch (NumberFormatException e) {
            }
        }
        return null;
    }
}
