/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
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
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.taglib;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.search.Hits;
import org.mskcc.pathdb.controller.ProtocolConstants;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.lucene.LuceneIndexer;
import org.mskcc.pathdb.lucene.PsiInteractionToIndex;

import java.io.IOException;

/**
 * Custom JSP Tag for Displaying Search Results Table.
 *
 * @author Ethan Cerami
 */
public class SearchResultsTable extends HtmlTable {
    private ProtocolRequest pRequest;
    private String uid;
    private Pager pager;

    /**
     * Sets UID Parameter.
     * @param request ProtocolRequest.
     */
    public void setProtocolRequest(ProtocolRequest request) {
        this.pRequest = request;
        this.uid = request.getQuery();
    }

    /**
     * Start Tag Processing.
     * @throws Exception Database Access Error.
     */
    protected void subDoStartTag() throws Exception {
        LuceneIndexer lucene = new LuceneIndexer();
        try {
            Hits hits = lucene.executeQuery(uid);
            this.pager = new Pager(pRequest, hits.length());
            createHeader(hits);
            String headers[] = {"", "Name", "Description", "Notes",
                "Details"};
            createTableHeaders(headers);
            outputResults(hits);
            endTable();
        } finally {
            lucene.closeIndexSearcher();
        }
    }

    private void createHeader(Hits hits) {
        String title = "Matching Results found for:  " + uid;
        this.createHeader(title);

        if (hits.length() > 0) {
            this.startTable();
            this.startRow(1);
            this.append("<td colspan='3'>Total Number of Matches:  "
                    + hits.length());
            this.append("</td>");
            this.append("<td colspan='2'>");
            this.append("<div class='right'>");
            append(pager.getHeaderHtml());
            append("</div></td>");
            this.endRow();
            this.endTable();
        }
        this.startTable();
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputResults(Hits hits) throws IOException {
        if (hits.length() == 0) {
            append("<TR>");
            append("<TD COLSPAN=4>No Matching Results found for:  " + uid);
            append("</TR>");
        } else {
            for (int i = pager.getStartIndex(); i < pager.getEndIndex(); i++) {
                this.startRow(i);
                Document doc = hits.doc(i);
//                Field name = doc.getField(LuceneIndexer.FIELD_NAME);
//                Field desc = doc.getField
//                        (LuceneIndexer.FIELD_DESCRIPTION);
//                Field tax_id = doc.getField
//                        (PsiInteractorToIndex.FIELD_TAXONOMY_ID);
//                Field species = doc.getField
//                        (PsiInteractorToIndex.FIELD_SPECIES);
//                Field database = doc.getField
//                        (PsiInteractionToIndex.FIELD_DATABASE);
//                Field interactionType = doc.getField
//                        (PsiInteractionToIndex.FIELD_INTERACTION_TYPE_NAME);
                Field pmid = doc.getField(PsiInteractionToIndex.FIELD_PMID);

                Field cpathId = doc.getField(LuceneIndexer.FIELD_CPATH_ID);
                append("<TD VALIGN=TOP WIDTH=20>");
                append(Integer.toString(i + 1) + ".");
                append("</TD>");
                append("<TD VALIGN=TOP WIDTH=60>");
                String url = "interactor.do?id=" + cpathId.stringValue();

//                if (name == null) {
//                    append("[No Name]");
//                } else {
//                    append(name.stringValue());
//                }
                Field all = doc.getField(LuceneIndexer.FIELD_ALL);
                outputDataField(all.stringValue());
                append("</TD>");

//                if (desc == null) {
//                    outputDataField ("[No Description]");
//                } else {
//                    outputDataField(desc.stringValue());
//                }

                StringBuffer notes = new StringBuffer();
//                if (species != null) {
//                    notes.append(species.stringValue()
//                            + "<BR>[NCBI Taxonomy ID:  "
//                            + tax_id.stringValue() + "]<BR>");
//                }
//                if (database != null) {
//                    notes.append("Database Source:  " + database.stringValue());
//                    notes.append("<BR>");
//                }
//                if (interactionType != null) {
//                    notes.append("Interaction Type:  " +
//                            interactionType.stringValue());
//                    notes.append("<BR>");
//                }
                if (pmid != null) {
                    notes.append("PMID Reference:  " + pmid.stringValue());
                    notes.append("<BR>");
                }
                outputDataField (notes.toString());

                this.outputDataField("<A HREF='"+url+"'>View Details</A>");

                //  outputInteractionLink(cpathId.stringValue());
                this.endRow();
            }
        }
    }

    private void outputInteractionLink(String uid) {
        String url = this.getInteractionLink
                (uid, ProtocolConstants.FORMAT_HTML);
        append("<TD>");
        this.outputLink("View Interactions", url);
        append("</TD>");
    }
}