// $Id: BioPaxRecordSummaryTable.java,v 1.20 2006-12-18 17:32:27 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.servlet.CPathUIConfig;

import java.util.List;
import java.util.StringTokenizer;
import java.util.HashSet;

/**
 * Custom jsp tag for displaying pathway child node (1 level deep)
 *
 * @author Benjamin Gross
 */
public class BioPaxRecordSummaryTable extends HtmlTable {

    /**
     * The number of synonyms per row.
     */
    private static final int SYNONYMS_PER_ROW = 3;

    /**
     * The spacing between synonyms.
     */
    private static final String SYNONYM_SPACING = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

    /**
     * The number of synonyms per row.
     */
    private static final int EXTERNAL_LINKS_PER_ROW = 3;

    /**
     * The spacing between external links.
     */
    private static final String EXTERNAL_LINKS_SPACING = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";

    /**
     * The a href link text to cytoscape tab
     */
    private static final String CYTOSCAPE_LINK_TEXT = "View Expression Data on this Pathway";

    /**
     * Reference to CPathRecord.
     */
    private CPathRecord record;

    /**
     * Reference to BioPaxRecordSummary
     */
    private BioPaxRecordSummary biopaxRecordSummary;

    /**
     * Receives Record ID Attribute.
     *
     * @param record CPathRecord
     */
    public void setRecord(CPathRecord record) {
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
        if (biopaxRecordSummary != null) {
            outputRecords();
        }
    }

    /**
     * Output the Summary Information.
     */
    private void outputRecords() {
        outputHeader();
        append("<TABLE CELLSPACING=5 CELLPADDING=0>");
        append("<TR VALIGN=TOP><TD>");
        append("<TABLE CELLSPACING=5 CELLPADDING=0>");
        outputDescription();
        outputSynonyms();
        outputExternalLinks();
        outputComment();
        outputDataSource(record.getSnapshotId());
        outputAvailability();
        append("</TABLE>");
        append("</TD>");
        append("<TD WIDTH=20%>");
        outputActionLinks();
        append("</TD></TR>");
        append("</TABLE>");
    }

    /**
     * Output the Header Information.
     */
    private void outputHeader() {

        // get header stryig
        String header = BioPaxRecordSummaryUtils.getBioPaxRecordHeaderString(biopaxRecordSummary);

        // do we have something to process ?
        if (header != null) {
            append("<div class='entity_header'>");
            append(header);
            append("</div>");
        }
    }

    /**
     * Reactome Hack to output description.
     */
    private void outputDescription() {
        if (biopaxRecordSummary.getName().startsWith("UniProt:")) {
            append("<TR>");
            append("<TD VALIGN=TOP><B>Description:</B></TD>");
            append("<TD VALIGN=TOP>");
            StringTokenizer tokenizer = new StringTokenizer(biopaxRecordSummary.getName(), " ");
            tokenizer.nextElement();
            while (tokenizer.hasMoreElements()) {
                append ((String) tokenizer.nextElement());
            }
            append("</TD></TR>");
        }
    }

    /**
     * Output the Synonym Information.
     */
    private void outputSynonyms() {

        // get synonym list
        List synonymList = biopaxRecordSummary.getSynonyms();

        // do we have something to process ?
        if (synonymList != null && synonymList.size() > 0) {
            append("<TR>");
            append("<TD VALIGN=TOP><B>Synonyms:</B></TD>");
            append("<TD VALIGN=TOP>");
            append("<TABLE VALIGN=TOP CELLSPACING=0 CELLPADDING=0>");
            append("<TR VALIGN=TOP>");
            boolean endedRow = false;
            int cnt = synonymList.size();
            for (int lc = 1; lc <= cnt; lc++) {
                append("<td VALIGN=TOP>");
                append ("<div class=synonym>");
                append((String) synonymList.get(lc - 1));
                append ("</dev>");
                append("</td>");
                // do we start a new row ?
                if ((lc % SYNONYMS_PER_ROW) == 0) {
                    append("</tr>");
                    endedRow = true;
                }
            }
            // do we have to cap a row ?
            if (!endedRow) {
                append("</tr>");
            }
            append("</TABLE>");
            append("</TD></TR>");
        }
    }

    /**
     * Output the Data Source information.
     */
    private void outputDataSource(long snapshotId) {
        try {
            String snapshotHtml = DbSnapshotInfo.getDbSnapshotHtml(snapshotId);

            // do we have something to process ?
            if (snapshotHtml != null && snapshotHtml.length() > 0) {
                append("<TR>");
                append("<TD WIDTH=10%><B>Data Source:</B></TD>");
                append("<TD COLSPAN=3>" + snapshotHtml + "</TD>");
                append("</TR>");
            }
        } catch (DaoException e) {
        }
    }

    /**
     * Output the Availability information.
     */
    private void outputAvailability() {

        // get synonym string
        String availabilityString =
                BioPaxRecordSummaryUtils.getBioPaxRecordAvailabilityString(biopaxRecordSummary);

        // do we have something to process ?
        if (availabilityString != null) {
            append("<TR>");
            append("<TD><B>Availability:</B></TD>");
            append("<TD COLSPAN=3>" + availabilityString + "</TD>");
            append("</TR>");
        }
    }

    /**
     * Output the External Links.
     * <p/>
     * (no help from biopaxRecordSummaryUtils)
     */
    private void outputExternalLinks() {

        // get the links
        List links = biopaxRecordSummary.getExternalLinks();

        // process them
        if (links != null && links.size() > 0) {
            append("<TR>");
            append("<TD><B>Links:</B></TD>");
            append("<TD VALIGN=TOP>");
            append("<TABLE VALIGN=TOP CELLSPACING=0 CELLPADDING=0>");
            append("<TR VALIGN=TOP>");
            boolean endedRow = false;
            int cnt = links.size();
            for (int lc = 1; lc <= cnt; lc++) {
                append("<TD");
                append ("<div class=synonym>");
                ExternalLinkRecord link = (ExternalLinkRecord) links.get(lc - 1);
                ExternalDatabaseRecord dbRecord = link.getExternalDatabase();
                String dbId = link.getLinkedToId();
                String linkStr = dbRecord.getName() + ":" + dbId;
                String uri = link.getWebLink();
                if (uri != null && uri.length() > 0) {
                    append("<A HREF=\"" + uri + "\">" + linkStr + "</A>");
                } else {
                    append(linkStr);
                }
                append ("<div>");
                append("</TD>");
                if ((lc % EXTERNAL_LINKS_PER_ROW) == 0) {
                    append("</tr>");
                    endedRow = true;
                    if (lc < cnt) {
                        append("<tr>");
                        endedRow = false;
                    }
                }
            }
            // do we have to cap a row ?
            if (!endedRow) {
                append("</tr>");
            }
            append("</TABLE>");
            append("</TD></TR>");
        }
    }

    /**
     * Output the Comment information.
     */
    private void outputComment() {

        // get comment string
        String commentString =
                BioPaxRecordSummaryUtils.getBioPaxRecordCommentString(biopaxRecordSummary);

        // do we have something to process ?
        if (commentString != null) {
            append("<TR VALIGN=TOP>");
            append("<TD><B>Comment:</B></TD>");
            append("<TD COLSPAN=3>");
            append (ReactomeCommentUtil.massageComment(commentString));
            append("</TD>");
            append("</TR>");
        }
    }

    /**
     * Output Actino Links.
     */
    private void outputActionLinks() {
        if (record.getType().equals(CPathRecordType.PATHWAY)) {
            if (CPathUIConfig.getWebUIBean().getDisplayCytoscapeTab()) {
            append("<div class='action_button'>"
                    + "<a href=\"cytoscape.do\">" + CYTOSCAPE_LINK_TEXT + "</a></div>");
            }
        }

        if (record.getType().equals(CPathRecordType.PATHWAY)
                || record.getType().equals(CPathRecordType.INTERACTION)) {
            ProtocolRequest request = new ProtocolRequest();
            request.setCommand(ProtocolConstants.COMMAND_GET_RECORD_BY_CPATH_ID);
            request.setQuery(Long.toString(record.getId()));
            request.setFormat(ProtocolConstants.FORMAT_BIO_PAX);
            append("<div class='action_button'>"
                + "<a href=\"" + request.getUri() + "\">"
                + "Download in BioPAX Format" + "</a></div>");
        }
    }
}
