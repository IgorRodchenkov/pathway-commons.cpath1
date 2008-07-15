package org.mskcc.pathdb.action.web_api.biopax_mode;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.lucene.queryParser.ParseException;
import org.apache.log4j.Logger;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolException;
import org.mskcc.pathdb.protocol.ProtocolStatusCode;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.assembly.AssemblyException;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.lucene.LuceneQuery;
import org.mskcc.pathdb.lucene.LuceneResults;
import org.mskcc.pathdb.action.web_api.WebApiUtil;
import org.mskcc.pathdb.schemas.search_response.*;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.taglib.ReactomeCommentUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.Marshaller;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.math.BigInteger;

/**
 * Executes Search:  Returns XML.
 *
 * @author Ethan Cerami
 */
public class ExecuteSearchXmlResponse {
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
            throws QueryException, IOException, AssemblyException, ParseException,
            ProtocolException, DaoException, CloneNotSupportedException, JAXBException,
            BioPaxRecordSummaryException {
        log.info ("Processing Search XML Response");
        GlobalFilterSettings filterSettings = new GlobalFilterSettings();
        ArrayList<String> entityTypes = new ArrayList<String>();
        entityTypes.add("protein");
        filterSettings.setEntityTypeSelected(entityTypes);

        //  Extract and set Organism Taxonomy ID
        String organism = protocolRequest.getOrganism();
        if (organism != null && organism.length() > 0) {
            List <Integer> organismTaxonomyIds = new ArrayList<Integer>();
            try {
                Integer taxId = new Integer(organism);
                organismTaxonomyIds.add(taxId);
                filterSettings.setOrganismSelected(organismTaxonomyIds);
            } catch (NumberFormatException e) {
                //  no-op
            }
        }

        String q = protocolRequest.getQuery();

        //  Special cases, used for local debugging only
        if (q != null && q.equalsIgnoreCase("test_slow")) {
            protocolRequest.setQuery("TNF");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        } else if (q != null && q.equalsIgnoreCase("test_500")) {
            throw new ProtocolException (ProtocolStatusCode.INTERNAL_ERROR, "Server is currently "
               + "down for maintenance");
        }

        LuceneQuery search = new LuceneQuery(protocolRequest, filterSettings, xdebug);
        long cpathIds[] = search.executeSearch();
        LuceneResults luceneResults = search.getLuceneResults();
        List<List<String>> textFragments = luceneResults.getFragments();
        if (cpathIds.length > 0) {
            SearchResponseType searchResponse = createXmlDocument
                    (search.getLuceneResults().getNumHits(), cpathIds, textFragments, xdebug);
            StringWriter writer = new StringWriter();
            Marshaller marshaller = createMarshaller("org.mskcc.pathdb.schemas.search_response");

            //  Work-around suggested by:
            //  http://weblogs.java.net/blog/kohsuke/archive/2006/03/why_does_jaxb_p.html
            QName qName = new QName ("", "search_response");
            marshaller.marshal(new JAXBElement(qName, SearchResponseType.class, searchResponse),
                    writer);
            WebApiUtil.returnXml(response, writer.toString());
        } else {
            throw new ProtocolException(ProtocolStatusCode.NO_RESULTS_FOUND,
                    "Sorry.  No results found for:  " + protocolRequest.getQuery() + ".");
        }
        return null;
    }

    /**
     * Creates the JAXB Marshaller.
     * @param schema            Path to XML Schema.
     * @return                  Marshaller Object.
     * @throws JAXBException    JAXB Error.
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
     * @return SearchResponseType Document object.
     */
    private SearchResponseType createXmlDocument(int totalNumHits, long cpathIds[],
            List<List<String>> textFragments, XDebug xdebug) throws DaoException,
            BioPaxRecordSummaryException {

        DaoCPath dao = DaoCPath.getInstance();
        ObjectFactory factory = new ObjectFactory();
        SearchResponseType searchResponse = factory.createSearchResponseType();

        //  Set total number of hits
        searchResponse.setTotalNumHits((long) totalNumHits);

        //  Output search hits
        List<ExtendedRecordType> searchHits = searchResponse.getSearchHit();
        for (int i = 0; i < cpathIds.length; i++) {
            long cpathId = cpathIds[i];
            CPathRecord record = dao.getRecordById(cpathId);
            BioPaxRecordSummary recordSummary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
            ExtendedRecordType searchHit = factory.createExtendedRecordType();
            searchHits.add(searchHit);
            searchHit.setPrimaryId(record.getId());
            searchHit.setName(record.getName());
            searchHit.setEntityType(record.getSpecificType());
            OrganismType organism = setOrganismInfo(factory, record);
            searchHit.setOrganism(organism);
            setSynonyms(recordSummary, searchHit);
            setXrefs(recordSummary, searchHit);
            setExcerpts (textFragments, searchHit, i);
            setPathwayInfo(searchHit, cpathId, dao, factory);
        }
        return searchResponse;
    }

    /**
     * Sets all External Refs.
     */
    private void setXrefs (BioPaxRecordSummary recordSummary, ExtendedRecordType searchHit) {
        ObjectFactory factory = new ObjectFactory();
        List <ExternalLinkRecord> xrefs = recordSummary.getExternalLinks();
		if (xrefs == null) return;
        List <XRefType> xrefList = searchHit.getXref();
        for (ExternalLinkRecord link:  xrefs) {
            ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
            String id = link.getLinkedToId();
            String db = dbRecord.getName();
            String url = link.getWebLink();
            XRefType xrefType = factory.createXRefType();
            xrefType.setDb(db);
            xrefType.setId(id);
            if (url != null) {
                xrefType.setUrl(url);
            }
            xrefList.add(xrefType);
        }
    }

    /**
     * Sets all Synonyms.
     */
    private void setSynonyms (BioPaxRecordSummary recordSummary, ExtendedRecordType searchHit) {
        List <String> synonyms = recordSummary.getSynonyms();
        List <String> synList = searchHit.getSynonym();
        if (synonyms != null && synonyms.size() > 0) {
            for (String synonym : synonyms) {
                synList.add(synonym);
            }
        }
    }

    private void setExcerpts (List<List<String>> masterTextFragments, ExtendedRecordType searchHit,
            int i) {
        List <String> excerptList = searchHit.getExcerpt();
        List <String> textFragments = masterTextFragments.get(i);
        if (textFragments != null && textFragments.size() > 0) {
            for (String excerpt: textFragments) {
                excerptList.add(excerpt);
            }
        }
    }

    /**
     * Outputs all pathway info.
     */
    private void setPathwayInfo(ExtendedRecordType searchHit, long cpathId, DaoCPath dao,
            ObjectFactory factory) throws DaoException {
        DaoInternalFamily daoFamily = new DaoInternalFamily();
        PathwayListType pathwayListRoot = factory.createPathwayListType();
        searchHit.setPathwayList(pathwayListRoot);
        List<PathwayType> pathwayList = pathwayListRoot.getPathway();
        long pathwayIds[] = daoFamily.getAncestorIds(cpathId, CPathRecordType.PATHWAY);
        for (int j=0; j<pathwayIds.length; j++) {
            CPathRecord pathwayRecord = dao.getRecordById(pathwayIds[j]);
            PathwayType pathway = factory.createPathwayType();
            pathway.setName(pathwayRecord.getName());
            pathway.setPrimaryId(pathwayRecord.getId());

            DataSourceType dataSource = factory.createDataSourceType();
            long snapshotId = pathwayRecord.getSnapshotId();
            DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    daoSnapshot.getDatabaseSnapshot(snapshotId);
            dataSource.setName(snapshotRecord.getExternalDatabase().getName());
            pathway.setDataSource(dataSource);
            pathwayList.add(pathway);
        }
    }

    /**
     * Outputs all organism info.
     */
    private OrganismType setOrganismInfo(ObjectFactory factory, CPathRecord record)
            throws DaoException {
        OrganismType organism = factory.createOrganismType();
        organism.setCommonName("N/A");
        organism.setSpeciesName("N/A");
        organism.setNcbiOrganismId(BigInteger.valueOf(-1));

        int taxId = record.getNcbiTaxonomyId();
        if (taxId > 0) {
            DaoOrganism daoOrganism = new DaoOrganism();
            Organism org = daoOrganism.getOrganismByTaxonomyId(taxId);
            if (org.getSpeciesName() != null && org.getSpeciesName().length() > 0) {
                organism.setSpeciesName(org.getSpeciesName());
            }
            if (org.getCommonName() != null && org.getCommonName().length() > 0) {
                organism.setCommonName(org.getCommonName());
            }
            organism.setNcbiOrganismId(BigInteger.valueOf(taxId));
        }
        return organism;
    }
}