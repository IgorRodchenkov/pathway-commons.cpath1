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
package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;

/**
 * Gets a Complete BioPAX Record from cPath.
 *
 * @author Ethan Cerami
 */
class GetBioPaxCommand extends Query {
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
        //  that the query parameter is a long number.
        long id = Long.parseLong(request.getQuery());
        xdebug.logMsg(this, "Retrieving BioPAX Record with cPath ID:  "
                + id);

        //  Before proceeding, make sure that the requested record
        //  is actually of type BioPAX.
        DaoCPath dao = DaoCPath.getInstance();
        CPathRecord record = dao.getRecordById(id);
        XmlRecordType xmlType = record.getXmlType();
        if (!xmlType.equals(XmlRecordType.BIO_PAX)) {
            throw new QueryException("cPath Record: " + id
                    + " is not of type:  BioPAX");
        }
        xdebug.logMsg(this, "Checking that record is of type:  BioPAX --> OK");

        //  Go ahead and create the assembly.
        XmlAssembly assembly = XmlAssemblyFactory.createXmlAssembly
                (record, 1, XmlAssemblyFactory.XML_FULL, xdebug);
        return assembly;
    }
}