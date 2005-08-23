package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.protocol.ProtocolRequest;

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
            throw new QueryException ("cPath Record: " + id
                    + " is not of type:  BioPAX");
        }
        xdebug.logMsg(this, "Checking that record is of type:  BioPAX --> OK");

        //  Go ahead and create the assembly.
        XmlAssembly assembly =
                XmlAssemblyFactory.createXmlAssembly(id,
                        XmlRecordType.BIO_PAX, 1, xdebug);
        return assembly;
    }
}