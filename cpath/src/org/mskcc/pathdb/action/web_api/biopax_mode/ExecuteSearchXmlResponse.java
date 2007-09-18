package org.mskcc.pathdb.action.web_api.biopax_mode;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.lucene.queryParser.ParseException;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.lucene.LuceneQuery;
import org.mskcc.pathdb.action.web_api.WebApiUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

/**
 * Executes Search:  Returns XML.
 *
 * @author Ethan Cerami
 */
public class ExecuteSearchXmlResponse {

    /**
     * Processes Client Request.
     *
     * @param xdebug          XDebug Object.
     * @param protocolRequest Protocol Request Object.
     * @param request         Http Servlet Request Object.
     * @param response        Http Servlet Response Object.
     * @param mapping         Struts Action Mapping Object.
     * @return Struts Action Forward Object.
     * @throws org.mskcc.pathdb.sql.query.QueryException
     *                                    Query Error.
     * @throws java.io.IOException        I/O Error.
     * @throws org.mskcc.pathdb.sql.assembly.AssemblyException
     *                                    XML Assembly Error.
     * @throws org.apache.lucene.queryParser.ParseException
     *                                    Lucene Parsing Error.
     * @throws org.mskcc.pathdb.protocol.ProtocolException
     *                                    Protocol Error.
     * @throws org.mskcc.pathdb.sql.dao.DaoException
     *                                    Database Error.
     * @throws CloneNotSupportedException Cloning Error.
     */
    public ActionForward processRequest(XDebug xdebug, ProtocolRequest protocolRequest,
            HttpServletRequest request, HttpServletResponse response, ActionMapping mapping)
            throws QueryException, IOException, AssemblyException, ParseException, ProtocolException,
            DaoException, CloneNotSupportedException {
        GlobalFilterSettings filterSettings = new GlobalFilterSettings();
        ArrayList<String> entityTypes = new ArrayList<String>();
        entityTypes.add("protein");
        filterSettings.setEntityTypeSelected(entityTypes);
        LuceneQuery search = new LuceneQuery(protocolRequest, filterSettings, xdebug);
        long cpathIds[] = search.executeSearch();
        List<List<String>> textFragments = search.getTextFragments();
        if (cpathIds.length > 0) {
            Document doc = createXmlDocument (search.getTotalNumHits(), cpathIds, textFragments, xdebug);
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            StringWriter writer = new StringWriter();
            outputter.output(doc, writer);
            WebApiUtil.returnXml(response, writer.toString());
        } else {
            throw new ProtocolException (ProtocolStatusCode.NO_RESULTS_FOUND,
                    "Sorry.  No results found for:  " + protocolRequest.getQuery() + ".");
        }
        return null;
    }

    /**
     * Creates JDOM Representation of XML Document.
     *
     * @return JDOM Document object.
     */
    private Document createXmlDocument(int totalNumHits, long cpathIds[], List<List<String>> textFragments,
            XDebug xdebug)
            throws DaoException {
        Document document = new Document();
        Element rootElement = new Element("search_results");
        rootElement.setAttribute("total_num_hits", Integer.toString(totalNumHits));
        document.setRootElement(rootElement);
        DaoCPath dao = DaoCPath.getInstance();

        for (int i=0; i<cpathIds.length; i++) {
            long cpathId = cpathIds[i];
            CPathRecord record = dao.getRecordById(cpathId);
            Element itemElement = new Element ("item");
            itemElement.setAttribute("local_id", Long.toString(record.getId()));
            Element nameElement = new Element ("name");
            nameElement.setText(record.getName());
            Element typeElement = new Element ("type");
            typeElement.setText(record.getSpecificType());

            int taxId = record.getNcbiTaxonomyId();
            Element orgElement = new Element ("organism");
            if (taxId > 0) {
                DaoOrganism daoOrganism = new DaoOrganism();
                Organism org = daoOrganism.getOrganismByTaxonomyId(taxId);
                orgElement.setText(org.getSpeciesName());
                orgElement.setAttribute("ncbi_taxonomy_id", Integer.toString(taxId));
            } else {
                orgElement.setText("N/A");
            }

            List<String> fragments = textFragments.get(i);
            Element fragmentElement = new Element ("excerpt");
            if (fragments != null && fragments.size() > 0) {
                String frag = fragments.get(0);
                fragmentElement.setText(frag);
            }

            DaoInternalLink daoLinker = new DaoInternalLink();
            GlobalFilterSettings filter = new GlobalFilterSettings();

            ArrayList parentTypes = daoLinker.getParentTypes(cpathId, -1,
                    getSnapshotFilter(filter), xdebug);

            itemElement.addContent(nameElement);
            itemElement.addContent(typeElement);
            itemElement.addContent(fragmentElement);
            itemElement.addContent(orgElement);

            for (int j=0; j<parentTypes.size(); j++) {
                TypeCount typeCount = (TypeCount) parentTypes.get(j);
                Element typeCountElement = new Element (typeCount.getType());
                typeCountElement.setText(Integer.toString(typeCount.getCount()));
                itemElement.addContent(typeCountElement);
            }

            rootElement.addContent(itemElement);
        }
        return document;
    }

    /**
     * Determines the current data source filter.
     * @param filterSettings        GlobalFilterSettings Object.
     * @return array of snapshot IDs.
     */
    protected long[] getSnapshotFilter (GlobalFilterSettings filterSettings) {
        Set snapshotSet = filterSettings.getSnapshotIdSet();
        long snapshotIds [] = new long[snapshotSet.size()];
        Iterator snapshotIterator = snapshotSet.iterator();
        int index = 0;
        while (snapshotIterator.hasNext()) {
            Long snapshotId = (Long) snapshotIterator.next();
            snapshotIds[index++] = snapshotId;
        }
        return snapshotIds;
    }
}