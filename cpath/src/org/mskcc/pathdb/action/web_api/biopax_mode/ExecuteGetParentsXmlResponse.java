package org.mskcc.pathdb.action.web_api.biopax_mode;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.action.web_api.WebApiUtil;
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;
import org.mskcc.pathdb.schemas.summary_response.SummaryResponseType;
import org.mskcc.pathdb.schemas.summary_response.DataSourceType;
import org.mskcc.pathdb.schemas.summary_response.RecordType;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Executes Parent Summary Look Up:  Returns XML.
 *
 * @author Ethan Cerami
 */
public class ExecuteGetParentsXmlResponse {
    private Logger log = Logger.getLogger(ExecuteSearchXmlResponse.class);

    /**
     * Processes Client Request.
     *
     * @param xdebug          XDebug Object.
     * @param protocolRequest Protocol Request Object.
     * @param request         Http Servlet Request Object.
     * @param response        Http Servlet Response Object.
     * @param mapping         Struts Action Mapping Object.
     * @return Struts Action Forward Object.
     * @throws ProtocolException Protocol Error.
     * @throws DaoException      Database Error.
     * @throws JAXBException     XML Marshaling Error.
     */
    public ActionForward processRequest(XDebug xdebug, ProtocolRequest protocolRequest,
            HttpServletRequest request, HttpServletResponse response, ActionMapping mapping)
            throws DaoException, ProtocolException, JAXBException {
        log.info("Processing Parent Summary Look Up: XML Response");
        DaoInternalLink daoInternalLink = new DaoInternalLink();

        //  Get all parents/sources of this record.
        long cpathId = Long.parseLong(protocolRequest.getQuery());
        Set<Long> recordIDs = new HashSet<Long>();
        ArrayList<InternalLinkRecord> internalLinkRecords = daoInternalLink.getSources(cpathId);
        for (InternalLinkRecord linkRecord : internalLinkRecords) {
            long sourceID = linkRecord.getSourceId();
            recordIDs.add(sourceID);
        }
        if (recordIDs.size() > 0) {
            SummaryResponseType summaryResponse = createXmlDocument (recordIDs);
            StringWriter writer = new StringWriter();
            Marshaller marshaller = createMarshaller
                    ("org.mskcc.pathdb.schemas.summary_response");

            //  Work-around suggested by:
            //  http://weblogs.java.net/blog/kohsuke/archive/2006/03/why_does_jaxb_p.html
            QName qName = new QName("", "summary_response");
            marshaller.marshal(new JAXBElement(qName, SummaryResponseType.class,
                    summaryResponse), writer);
            WebApiUtil.returnXml(response, writer.toString());
        } else {
            throw new ProtocolException(ProtocolStatusCode.NO_RESULTS_FOUND,
                    "Sorry.  No results found for:  " + protocolRequest.getQuery() + ".");
        }
        return null;
    }

    /**
     * Creates the JAXB Marshaller.
     *
     * @param schema Path to XML Schema.
     * @return Marshaller Object.
     * @throws JAXBException JAXB Error.
     */
    private Marshaller createMarshaller(String schema) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance(schema);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        return marshaller;
    }

    /**
     * Creates JAXB Representation of XML Document.
     *
     * @return SummaryResponseType Object.
     */
    private SummaryResponseType createXmlDocument(Set<Long> IdSet) throws DaoException {
        SummaryResponseType summaryResponse = new SummaryResponseType();
        List<RecordType> summaryList = summaryResponse.getRecord();
        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoExternalDbSnapshot daoExternalDbSnapshot = new DaoExternalDbSnapshot();

        for (Long id : IdSet) {
            CPathRecord record = daoCPath.getRecordById(id);

            //  TODO: Eventually Add Experimental Evidence
            RecordType summary = new RecordType();
            summary.setPrimaryId(record.getId());

            if (record.getName() != null && record.getName().length() > 0) {
                summary.setName(record.getName());
            }
            if (record.getDescription() != null && record.getDescription().length() > 0) {
                summary.setDescription(record.getDescription());
            }

            summary.setType(record.getSpecificType());

            ExternalDatabaseSnapshotRecord snapshotRecord = daoExternalDbSnapshot.
                    getDatabaseSnapshot(record.getSnapshotId());
            ExternalDatabaseRecord externalDatabase = snapshotRecord.getExternalDatabase();
            DataSourceType dataSourceType = new DataSourceType();
            dataSourceType.setName(externalDatabase.getName());
            dataSourceType.setPrimaryId(externalDatabase.getMasterTerm());
            summary.setDataSource(dataSourceType);
            summaryList.add(summary);
        }
        return summaryResponse;
    }
}