// $Id: BioPaxRecordSummaryTable.java,v 1.2 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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
package org.mskcc.pathdb.taglib;

// imports
import java.util.List;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;

/**
 * Custom jsp tag for displaying pathway child node (1 level deep)
 *
 * @author Benjamin Gross
 */
public class BioPaxRecordSummaryTable extends HtmlTable {

    /**
     * Reference to CPathRecord.
     */
    CPathRecord record;

	/**
	 * Reference to BioPaxRecordSummary
	 */
	BioPaxRecordSummary biopaxRecordSummary;

    /**
     * Receives Record ID Attribute.
     *
     * @param record CPathRecord
     */
    public void setRecord(CPathRecord record){
        this.record = record;
    }

    /**
     * Executes JSP Custom Tag
     *
     * @throws Exception Exception in writing to JspWriter.
     */
    protected void subDoStartTag() throws Exception {

		// get biopax record summary
		biopaxRecordSummary = BioPaxRecordUtil.createBioPaxRecordSummary(record);

		// output the info
		if (biopaxRecordSummary != null){
			outputRecords();
		}
    }

    /**
     * Output the Summary Information.
     */
    private void outputRecords() {

		outputHeader();
		append("<TABLE>");
		outputSynonyms();
		outputDataSource();
		outputAvailability();
		outputExternalLinks();
		outputComment();
		append("</TABLE>");
    }

    /**
     * Output the Header Information.
     */
    private void outputHeader() {

		// get header stryig
		String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(biopaxRecordSummary);

		// do we have something to process ?
		if (header != null){
			append("<DIV ID=\"apphead\">");
			append("<H2>" + header + "</H2>");
			append("</DIV>");
		}
	}

    /**
     * Output the Synonym Information.
     */
    private void outputSynonyms() {

		// get synonym string
		String synonymString = BioPaxRecordSummaryUtils.getBioPaxRecordSynonymString(biopaxRecordSummary);
		
		// do we have something to process ?
		if (synonymString != null) {
			append("<TR>");
			append("<TD>Synonyms:</TD>");
			append("<TD COLSPAN=3>" + synonymString + "</TD>");
			append("</TR>");
		}
	}

    /**
     * Output the Data Source information.
     */
    private void outputDataSource() {

		// get synonym string
		String dataSourceString = BioPaxRecordSummaryUtils.getBioPaxRecordDataSourceString(biopaxRecordSummary);
		
		// do we have something to process ?
		if (dataSourceString != null) {
			append("<TR>");
			append("<TD>Data Source:</TD>");
			append("<TD COLSPAN=3>" + dataSourceString + "</TD>");
			append("</TR>");
		}
	}

    /**
     * Output the Availability information.
     */
    private void outputAvailability() {

		// get synonym string
		String availabilityString = BioPaxRecordSummaryUtils.getBioPaxRecordAvailabilityString(biopaxRecordSummary);
		
		// do we have something to process ?
		if (availabilityString != null) {
			append("<TR>");
			append("<TD>Availability:</TD>");
			append("<TD COLSPAN=3>" + availabilityString + "</TD>");
			append("</TR>");
		}
	}

    /**
     * Output the External Links.
	 *
	 * (no help from biopaxRecordSummaryUtils)
     */
    private void outputExternalLinks() {

		// get the links
		List links = biopaxRecordSummary.getExternalLinks();

		// process them
		if (links != null && links.size() > 0){
			append("<TR>");
			append("<TD>External Links:</TD>");
			for (int lc = 1; lc <= links.size(); lc++) {
				append("<TD>");
				ExternalLinkRecord link = (ExternalLinkRecord) links.get(lc-1);
				ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
				String dbId = link.getLinkedToId();
				String linkStr = dbRecord.getName() + ":" + dbId;
				String uri = link.getWebLink();
				if (uri != null && uri.length() > 0) {
					append("<A HREF=\""+ uri + "\">" + linkStr + "</A>");
				} else {
					append(linkStr);
				}
				append("</TD>");
				if (lc % 3 == 0){
					append("</TR>");
					append("<TR>");
					// for nice spacing
					append("<TD></TD>");
				}
			}
			append("</TR>");
		}
	}

    /**
     * Output the Comment information.
     */
    private void outputComment() {

		// get synonym string
		String commentString = BioPaxRecordSummaryUtils.getBioPaxRecordCommentString(biopaxRecordSummary);
		
		// do we have something to process ?
		if (commentString != null) {
			commentString = commentString.replaceAll("<BR>", "<P>");
			append("<TR>");
			append("<TD>Comment:</TD>");
			append("<TD COLSPAN=3>" + commentString + "</TD>");
			append("</TR>");
		}
	}
}
