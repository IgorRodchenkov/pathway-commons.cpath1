// $Id: BioPaxToIndex.java,v 1.13 2007-01-25 21:17:39 grossb Exp $
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
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoSourceTracker;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;

import junit.framework.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates a BioPAX Record scheduled for indexing in Lucene.
 * <p/>
 * Currently indexes the following content:
 * <TABLE WIDTH=100%>
 * <TR>
 * <TH ALIGN=LEFT><B>Content</B></TH>
 * <TH ALIGN=LEFT><B>Field</B></TH>
 * <TH ALIGN=LEFT><B>Notes</B></TH>
 * </TR>
 * <TR>
 * <TD>All terms after XML Stripping</TD>
 * <TD>FIELD_ALL</TD>
 * </TR>
 * <TR>
 * <TD>NAME/SHORT-NAME</TD>
 * <TD>FIELD_NAME</TD>
 * </TR>
 * <TR>
 * <TD>cPath ID</TD>
 * <TD>FIELD_CPATH_ID</TD>
 * </TR>
 * </TR>
 * <TR>
 * <TD>entity type</TD>
 * <TD>FIELD_ENTITY_TYPE</TD>
 * </TR>
 * <TR>
 * <TD>data source(s)</TD>
 * <TD>FIELD_DATA_SOURCE</TD>
 * </TR>
 * <TR>
 * <TD VALIGN=TOP>Organism Data</TD>
 * <TD VALIGN=TOP>FIELD_ORGANISM</TD>
 * <TD VALIGN=TOP>The "Browse by Organism" web page and the "Quick Browse"
 * web component work by automatically running queries on the FIELD_ORGANISM.
 * </TD>
 * </TR>
 * </TABLE>
 *
 * @author Ethan Cerami
 */
public class BioPaxToIndex implements ItemToIndex {

    /**
     * Page Attribute:  Data Sources.
     */
    public static final String DATA_SOURCE_DELIMITER = ":::::";

    /**
     * Internal List of all Fields scheduled for Indexing.
     */
    private ArrayList fields = new ArrayList();

    /**
     * Constructor.
     * Only available within the Lucene package.
     * The only way to construct the object is via the Factory class.
     *
     * @param xmlAssembly XmlAssembly.
     * @throws IOException Input Output Error.
     * @throws JDOMException.
	 * @throws DaoException.
     */
    BioPaxToIndex(long cpathId, XmlAssembly xmlAssembly)
		throws IOException, JDOMException, DaoException {

		// get cpath record
        DaoCPath cpath = DaoCPath.getInstance();
		CPathRecord record = cpath.getRecordById(cpathId);

        //  Index All Terms -->  FIELD_ALL
        String xml = xmlAssembly.getXmlString();
        String terms = XmlStripper.stripTags(xml, true);

        //  Remove cPath IDs, part of bug:  #798
        terms = removecPathIds(terms);

        fields.add(Field.Text(LuceneConfig.FIELD_ALL, terms));

        //  Index cPath ID --> FIELD_CPATH_ID
        fields.add(Field.Text(LuceneConfig.FIELD_CPATH_ID,
                Long.toString(cpathId)));

		// index entity type --> FIELD_ENTITY_TYPE
		fields.add(Field.Text(LuceneConfig.FIELD_ENTITY_TYPE,
							  record.getSpecificType()));

		// data source --> FIELD_DATA_SOURCE
		String dataSource = getDataSources(cpath, record);
		fields.add(Field.Text(LuceneConfig.FIELD_DATA_SOURCE,
							  dataSource));
		
        //  Index Name/Short Name --> FIELD_NAME
        Element rdfRoot = (Element) xmlAssembly.getXmlObject();
        XPath xpath = XPath.newInstance("*/bp:NAME");
        xpath.addNamespace("bp", BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        Element nameElement = (Element) xpath.selectSingleNode(rdfRoot);

        xpath = XPath.newInstance("*/bp:SHORT-NAME");
        xpath.addNamespace("bp", BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE_URI);
        Element shortNameElement = (Element) xpath.selectSingleNode(rdfRoot);

        StringBuffer nameBuf = new StringBuffer();
        if (nameElement != null) {
            nameBuf.append(nameElement.getTextNormalize() + " ");
        }
        if (shortNameElement != null) {
            nameBuf.append(shortNameElement.getTextNormalize());
        }
        fields.add(Field.Text(LuceneConfig.FIELD_NAME, nameBuf.toString()));

        //  Index Organism Data --> FIELD_ORGANISM
        indexOrganismData(xmlAssembly);
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
	 * Gets the data sources used to create this cpath record
	 *
	 * @param cpath DaoCPath
	 * @param record CPathRecord
	 * @return String
	 * @throws DaoException
	 */
	private String getDataSources(DaoCPath cpath, CPathRecord record) throws DaoException {

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
			dataSourceBuffer.append(externalDatabaseRecord.getName() + DATA_SOURCE_DELIMITER);
		}

		// outta here
		return dataSourceBuffer.toString();
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
            fields.add(Field.Text(LuceneConfig.FIELD_ORGANISM,
                    organismTokens.toString()));
        }
    }
}
