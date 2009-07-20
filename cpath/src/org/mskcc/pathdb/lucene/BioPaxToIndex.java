// $Id: BioPaxToIndex.java,v 1.38 2009-07-20 17:23:58 cerami Exp $
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
package org.mskcc.pathdb.lucene;

import org.apache.lucene.document.Field;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;

import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.pathdb.util.xml.XmlStripper;
import org.mskcc.pathdb.model.*;
import org.mskcc.pathdb.sql.dao.*;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.util.ExternalDatabaseConstants;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;

import java.io.IOException;
import java.util.*;

/**
 * Encapsulates a BioPAX Record scheduled for indexing in Lucene.
 *
 * @author Ethan Cerami, Benjamin Gross.
 */
public class BioPaxToIndex implements ItemToIndex {
    private float boost = 1.0f;

    /**
     * Internal List of all Fields scheduled for Indexing.
     */
    private ArrayList fields = new ArrayList();

    /**
     * Constructor.
     * Only available within the Lucene package.
     * The only way to construct the object is via the Factory class.
	 *
	 * NOTE: IF MORE FIELDS ARE INDEXED, LuceneResults.addTerm SHOULD BE UPDATES
     *
     * @param xmlAssembly XmlAssembly.
     * @throws IOException Input Output Error.
     * @throws JDOMException
	 * @throws DaoException
	 * @throws BioPaxRecordSummaryException
     */
    BioPaxToIndex(long cpathId, XmlAssembly xmlAssembly)
		throws IOException, JDOMException, DaoException, BioPaxRecordSummaryException {

		// get cpath record
        DaoCPath cpath = DaoCPath.getInstance();
		CPathRecord record = cpath.getRecordById(cpathId);

        //  Index All Terms -->  FIELD_ALL
        String xml = xmlAssembly.getXmlString();
        String terms = XmlStripper.stripTags(xml, true, true);

        //  Remove cPath IDs, part of bug:  #798
        terms = removecPathIds(terms);

		// Index "all field"
		fields.add(new Field(LuceneConfig.FIELD_ALL, terms, Field.Store.YES, Field.Index.TOKENIZED));

        //  Index cPath ID --> FIELD_CPATH_ID
		fields.add(new Field(LuceneConfig.FIELD_CPATH_ID, Long.toString(cpathId),
                Field.Store.YES, Field.Index.UN_TOKENIZED));

		// index record type --> FIELD_RECORD_TYPE
		fields.add(new Field(LuceneConfig.FIELD_RECORD_TYPE, record.getType().toString(),
                Field.Store.YES, Field.Index.TOKENIZED));

		// index specific type --> FIELD_SPECIFIC_TYPE
		fields.add(new Field(LuceneConfig.FIELD_SPECIFIC_TYPE, record.getSpecificType(),
                Field.Store.YES, Field.Index.TOKENIZED));

        // data source --> FIELD_DATA_SOURCE
		String dataSource = getDataSources(record);
		fields.add(new Field(LuceneConfig.FIELD_DATA_SOURCE, dataSource, Field.Store.YES, Field.Index.TOKENIZED));

		// create the summary
		BioPaxRecordSummary summary =
			BioPaxRecordUtil.createBioPaxRecordSummary(record);

        //  Index Name/Short Name --> FIELD_NAME
		fields.add(new Field(LuceneConfig.FIELD_NAME, getNamesForField(summary),
                Field.Store.YES, Field.Index.TOKENIZED));

        //  Index Organism Data --> FIELD_ORGANISM
        indexOrganismData(xmlAssembly);

		// Index Synonyms --> FIELD_SYNONYMS
		fields.add(new Field(LuceneConfig.FIELD_SYNONYMS, getSynonymsForField(summary),
                Field.Store.YES, Field.Index.TOKENIZED));

        // Index Gene Symbol(s)
        fields.add(new Field(LuceneConfig.FIELD_GENE_SYMBOLS, getGeneSymbol(summary), Field.Store.YES,
                Field.Index.TOKENIZED));

        // Index Ext Refs --> FIELD_EXTERNAL_REFS
		fields.add(new Field(LuceneConfig.FIELD_EXTERNAL_REFS, getExternalRefsForField(summary),
                Field.Store.YES, Field.Index.TOKENIZED));

		// Index Descendents --> FIELD_DESCENDENTS
		indexDescendents(cpath, record);
        indexNumParents(record);

        // * NOTE: IF MORE FIELDS ARE INDEXED, LuceneResults.addTerm SHOULD BE UPDATES *
    }

    /**
     * Indexes the Number of Parents and automatically boosts the document, based on the number of parents.
     * Boosting is directly proportional to number of parents.
     *
     * @param record CPathRecord.
     * @throws DaoException Database Error.
     */
    private void indexNumParents(CPathRecord record) throws DaoException {
        DaoInternalLink daoInternalLink = new DaoInternalLink();
        List<InternalLinkRecord>  parentList = daoInternalLink.getSources(record.getId());
        fields.add(new Field(LuceneConfig.FIELD_NUM_PARENTS, Integer.toString(parentList.size()),
                Field.Store.YES, Field.Index.NO));

		// populate parent pathway - we use internal family table because we will only
        // get interaction links via internal link table
		// and the task that populates this table gets run before indexing
		DaoInternalFamily daoInternalFamily = new DaoInternalFamily();
		Set<Integer> organismIds = new HashSet<Integer>();
		organismIds.add(record.getNcbiTaxonomyId());
		StringBuffer numParentPathways = new StringBuffer();
		String[] dataSources = ((String)(getDataSources(record))).split(" ");
		// the following maps will be used below when summing interactions
		// but we set them up here since we are looping over data sources now
		HashMap<String, Set<Long>> dataSourcesToSnapshotIdMap = new HashMap<String, Set<Long>>();
		HashMap<String, Integer> dataSourcesToInteractionCountMap = new HashMap<String, Integer>();
		for (String dataSource : dataSources) {
			if (dataSource == null || dataSource.length() == 0) {
				continue;
			}
			Set<Long> snapshotIds = getSnapshotIDs(dataSource);
			dataSourcesToSnapshotIdMap.put(dataSource, snapshotIds);
			dataSourcesToInteractionCountMap.put(dataSource, 0);
			Integer count = daoInternalFamily.getAncestorIdCount(record.getId(),
                    CPathRecordType.PATHWAY, snapshotIds, organismIds);
            boost += count;
			numParentPathways.append(dataSource + ":" + count + "\t");
		}
        fields.add(new Field(LuceneConfig.FIELD_NUM_PARENT_PATHWAYS, numParentPathways.toString().trim(),
							 Field.Store.YES, Field.Index.NO));

		// interate over parent list to populate parent interaction field
		StringBuffer numParentInteractions = new StringBuffer();
		DaoCPath cpath = DaoCPath.getInstance();
		HashMap<String,Integer> num_parent_interactions = new HashMap<String, Integer>();
		for (InternalLinkRecord internalLinkRecord : parentList) {
			// get cpath record
			CPathRecord srcRecord = cpath.getRecordById(internalLinkRecord.getSourceId());
			if (srcRecord.getType().equals(CPathRecordType.INTERACTION)) {
				// loop over all data sources
				for (String dataSource : dataSourcesToSnapshotIdMap.keySet()) {
					Set<Long> snapshotIds = dataSourcesToSnapshotIdMap.get(dataSource);
					if (snapshotIds.contains(srcRecord.getSnapshotId())) {
						dataSourcesToInteractionCountMap.put(dataSource,
															 dataSourcesToInteractionCountMap.get(dataSource) + 1);
						break;
					}
				}
			}
		}
		// create string of total interactions by data source
		for (String dataSource : dataSourcesToInteractionCountMap.keySet()) {
			Integer numInteractionForDataSource = dataSourcesToInteractionCountMap.get(dataSource);
			numParentInteractions.append(dataSource + ":" + numInteractionForDataSource + "\t");
            boost += numInteractionForDataSource;
		}
        fields.add(new Field(LuceneConfig.FIELD_NUM_PARENT_INTERACTIONS, numParentInteractions.toString().trim(),
							 Field.Store.YES, Field.Index.NO));
    }

    /**
     * Gets Total Number of Fields to Index.
     *
     * @return total number of fields to index.
     */
    public int getNumFields() {
        return fields.size();
    }

    /**
     * Gets Field at specified index.
     *
     * @param index Index value.
     * @return Lucene Field Object.
     */
    public Field getField(int index) {
        return (Field) fields.get(index);
    }

    /**
     * Gets the Document Boost Factor;  Default is 0.5.
     *
     * @return boost factor.
     */
    public float getBoost() {
        return boost;
    }

    /**
     * Removes CPATH IDs from an abritrary String.
     * For example, before:  "FOO CPATH 123 BAR"
     * after:  "FOO BAR"
     *
     * @param str Input String.
     * @return Output String.
     */
    public static String removecPathIds(String str) {
        return str.replaceAll(" CPATH \\d*", "");
    }

	/**
	 * Gets all snapshot ids for a given db master term.
	 *
	 * @param masterTerm String
	 * @return Set<Long>
	 * @throws DaoException
	 */
	private Set<Long> getSnapshotIDs(String masterTerm) throws DaoException {

		Set toReturn = new HashSet<Long>();
		
		// get external db id from master term
		DaoExternalDb daoExternalDb = new DaoExternalDb();
		ExternalDatabaseRecord externalRecord = daoExternalDb.getRecordByTerm(masterTerm);
		if (externalRecord == null) {
			return toReturn;
		}

		// given external db id, get all snapshots
		DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
		ArrayList<ExternalDatabaseSnapshotRecord> snapshots = daoSnapshot.getDatabaseSnapshot(externalRecord.getId());
		for (ExternalDatabaseSnapshotRecord record : snapshots) {
			toReturn.add(record.getId());
		}

		// outta here
		return toReturn;
	}

	/**
	 * Gets the data sources used to create this cpath record 
	 * as a string on space separated master terms.
	 *
	 * @param record CPathRecord
	 * @return String
	 * @throws DaoException
	 */
	private String getDataSources(CPathRecord record) throws DaoException {

		// to return
		StringBuffer dataSourceBuffer = new StringBuffer();

		// create record list to process
		ArrayList<CPathRecord> recordList = new ArrayList<CPathRecord>();
		if (record.getType().equals(CPathRecordType.PATHWAY)) {
			// we can use the record itself
			recordList.add(record);
		}
		else {
			// get list of source records
			DaoSourceTracker sourceTracker = new DaoSourceTracker();
			recordList = sourceTracker.getSourceRecords(record.getId());
		}

		// interate through record list
		DaoExternalDbSnapshot daoSnapShot = new DaoExternalDbSnapshot();
		ArrayList<String> processedMasterTerms = new ArrayList<String>();
		for (CPathRecord sourceRecord : recordList) {
		
			// get the snapshot id
			long snapShotId = sourceRecord.getSnapshotId();

			// get the snapshot record
			ExternalDatabaseSnapshotRecord snapShotRecord = daoSnapShot.getDatabaseSnapshot(snapShotId);
			if (snapShotRecord == null) continue;
			
			// get external db record from snapshot record
			ExternalDatabaseRecord externalDatabaseRecord = snapShotRecord.getExternalDatabase();
			if (externalDatabaseRecord == null) continue;

			// get name of external db from external db record and append to buffer
            if (!externalDatabaseRecord.getDbType().equals(ReferenceType.PROTEIN_UNIFICATION)) {
				String masterTerm = externalDatabaseRecord.getMasterTerm();
				if (processedMasterTerms.contains(masterTerm)) {
					continue;
				}
                dataSourceBuffer.append(masterTerm + " ");
				processedMasterTerms.add(masterTerm);
            }
        }

		// outta here
		return dataSourceBuffer.toString().trim();
	}

    /**
     * Indexes All Organism Data --> FIELD_ORGANISM
     */
    private void indexOrganismData(XmlAssembly xmlAssembly)
            throws JDOMException {
        //  Get Root Element
        Element rdfRoot = (Element) xmlAssembly.getXmlObject();

        //  Find All Organism Elements via XPath
        XPath xpath1 = XPath.newInstance("//bp:ORGANISM");
        xpath1.addNamespace("bp", BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        List organismList = xpath1.selectNodes(rdfRoot);
        StringBuffer organismTokens = new StringBuffer();

        //  Iterate through all Organism Elements
        for (int i = 0; i < organismList.size(); i++) {
            Element organismElement = (Element) organismList.get(i);

            //  Extract Organism Name
            XPath xpath2 = XPath.newInstance(".//bp:NAME");
            xpath2.addNamespace("bp",
                    BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
            Element nameElement = (Element) xpath2.selectSingleNode
                    (organismElement);

            //  Extract Organism Taxonomy ID
            XPath xpath3 = XPath.newInstance(".//bp:ID");
            xpath3.addNamespace("bp",
                    BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
            Element taxonomyElement = (Element) xpath3.selectSingleNode
                    (organismElement);

            if (nameElement != null) {
                organismTokens.append(nameElement.getTextNormalize()
                        + LuceneConfig.SPACE);
            }
            if (taxonomyElement != null) {
                organismTokens.append(taxonomyElement.getTextNormalize()
                        + LuceneConfig.SPACE);
            }
        }
        if (organismTokens.length() > 0) {
			fields.add(new Field(LuceneConfig.FIELD_ORGANISM, organismTokens.toString(),
                    Field.Store.YES, Field.Index.TOKENIZED));
        }
    }

	/**
	 * Gets the descendents of this cpath record
	 *
	 * @param daoCPath DaoCPath
	 * @param pathwayRecord CPathRecord
	 * @return String
	 * @throws DaoException
	 * @throws BioPaxRecordSummaryException
	 */
	private void indexDescendents(DaoCPath daoCPath, CPathRecord pathwayRecord)
		throws DaoException, BioPaxRecordSummaryException {

		// to return
		StringBuffer bufferToReturn = new StringBuffer();

		// get descendent ids
		ArrayList<Long> descendentIds = getAllDescendents(pathwayRecord.getId());

		// add self to list
		descendentIds.add(pathwayRecord.getId());

		for (Long descendentId : descendentIds) {

			// get cpath record
			CPathRecord descendentRecord = daoCPath.getRecordById(descendentId);

			// create the summary
			BioPaxRecordSummary summary =
				BioPaxRecordUtil.createBioPaxRecordSummary(descendentRecord);

            HashSet <String> nameMap = new HashSet <String> ();

            getNamesSet(summary, nameMap);
            getSynonymsSet(summary, nameMap);
            getExternalRefsSet(summary, nameMap);

            Iterator <String> nameIterator = nameMap.iterator();
            while (nameIterator.hasNext()) {
                String currentName = nameIterator.next();
                bufferToReturn.append(currentName + " ");
            }
		}

		// outta here
        String desc = bufferToReturn.toString().trim();
        fields.add(new Field(LuceneConfig.FIELD_DESCENDENTS, desc, Field.Store.YES,
                Field.Index.TOKENIZED));
        if (descendentIds.size() -1 > 0) {
            fields.add(new Field(LuceneConfig.FIELD_NUM_DESCENDENTS,
                    Integer.toString(descendentIds.size()-1),
                    Field.Store.YES, Field.Index.NO));
        }
    }

    /**
     * Gets all descendents of the specified cPath record.
     * <P>This is a potentially very slow query.
     *
     * @param cpathId CPath Record ID.
     * @return arraylist of descendent Ids.
     * @throws DaoException Database Access Error.
     */
    public ArrayList getAllDescendents (long cpathId) throws DaoException {
        ArrayList masterList = new ArrayList();
		DaoInternalFamily internalFamily = new DaoInternalFamily();

		long[] descendentIds = internalFamily.getDescendentIds(cpathId, CPathRecordType.PHYSICAL_ENTITY);
		for (long descendentId : descendentIds) {
            masterList.add(descendentId);
		}

		// outta here
        return masterList;
    }

	private String getNamesForField(BioPaxRecordSummary summary) {

		StringBuffer bufferToReturn = new StringBuffer();

        HashSet nameSet = new HashSet<String>();
        // name
		String name = summary.getName();
		if (name != null && name.length() > 0) {
			nameSet.add(name);
		}


        // label
		String label = summary.getLabel();
		if (label != null && label.length() > 0 && !label.equals(name)) {
			nameSet.add(label);
		}

		// short name
		String shortName = summary.getShortName();
		if (shortName != null && shortName.length() > 0) {
			nameSet.add(shortName);
		}

		// outta here
        Iterator <String> nameIterator = nameSet.iterator();
        while (nameIterator.hasNext()) {
            String currentName = nameIterator.next();
            bufferToReturn.append(currentName + " ");
        }
        return bufferToReturn.toString();
	}

	private String getSynonymsForField(BioPaxRecordSummary summary) {

		StringBuffer bufferToReturn = new StringBuffer();

		List<String> synonyms = (List<String>)summary.getSynonyms();
		if (synonyms != null) {
			for (String synonym : synonyms) {
				if (synonym != null && synonym.length() > 0) {
					bufferToReturn.append(synonym + XmlStripper.ELEMENT_DELIMITER);
				}
			}
		}

		// outta here
		String toReturn = bufferToReturn.toString();
		return toReturn.replaceAll(XmlStripper.ELEMENT_DELIMITER + "$", "");
	}

	private String getExternalRefsForField(BioPaxRecordSummary summary) {

		StringBuffer bufferToReturn = new StringBuffer();

		if (summary.getExternalLinks() != null) {
			for (ExternalLinkRecord link : (List<ExternalLinkRecord>)summary.getExternalLinks()) {
				String dbName = link.getExternalDatabase().getName();
				if (! dbName.equalsIgnoreCase("PUBMED")) {
					bufferToReturn.append(link.getLinkedToId() + XmlStripper.ELEMENT_DELIMITER);
				}
			}
		}

		// outta here
		String toReturn = bufferToReturn.toString();
		return toReturn.replaceAll(XmlStripper.ELEMENT_DELIMITER + "$", "");
	}

	private void getNamesSet(BioPaxRecordSummary summary, HashSet<String> nameSet) {
        // name
		String name = summary.getName();
		if (name != null && name.length() > 0) {
			nameSet.add(name);
		}

        // label
		String label = summary.getLabel();
		if (label != null && label.length() > 0 && !label.equals(name)) {
			nameSet.add(label);
		}

		// short name
		String shortName = summary.getShortName();
		if (shortName != null && shortName.length() > 0) {
			nameSet.add(shortName);
		}
	}

	private void getSynonymsSet(BioPaxRecordSummary summary, HashSet<String> nameSet) {
		List<String> synonyms = (List<String>)summary.getSynonyms();
		if (synonyms != null) {
			for (String synonym : synonyms) {
				if (synonym != null && synonym.length() > 0) {
					nameSet.add(synonym);
				}
			}
		}
	}

	private void getExternalRefsSet(BioPaxRecordSummary summary, HashSet<String> nameSet) {
		if (summary.getExternalLinks() != null) {
			for (ExternalLinkRecord link : (List<ExternalLinkRecord>)summary.getExternalLinks()) {
				String dbName = link.getExternalDatabase().getName();
				if (! dbName.equalsIgnoreCase("PUBMED")) {
					nameSet.add(link.getLinkedToId());
				}
			}
		}
	}

    private String getGeneSymbol (BioPaxRecordSummary summary) {
		StringBuffer bufferToReturn = new StringBuffer();
		if (summary.getExternalLinks() != null) {
			for (ExternalLinkRecord link : (List<ExternalLinkRecord>)summary.getExternalLinks()) {
				String dbName = link.getExternalDatabase().getMasterTerm();
                if (dbName.equals(ExternalDatabaseConstants.GENE_SYMBOL)) {
                    bufferToReturn.append(link.getLinkedToId());
                }
			}
		}
		return bufferToReturn.toString();
	}
}
