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

import org.mskcc.pathdb.lucene.CPathResult;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoException;

import java.util.ArrayList;

/**
 * Custom JSP Tag for Displaying Search Results Table.
 *
 * @author Ethan Cerami
 */
public class SearchResultsTable extends HtmlTable {
    private String uid;
    private ArrayList searchResults;

    /**
     * Sets Interaction Parameter.
     * @param searchResults ArrayList of CPathResult objects.
     */
    public void setSearchResults(ArrayList searchResults) {
        this.searchResults = searchResults;
    }

    /**
     * Sets UID Parameter.
     * @param uid UID String.
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * Start Tag Processing.
     * @throws DaoException Database Access Error.
     */
    protected void subDoStartTag() throws DaoException {
        startTable("Matching Results found for:  " + uid.toUpperCase());
        String headers[] = {"Rank", "Score", "Name", "Description",
                            "Interactions"};
        createTableHeaders(headers);
        outputResults();
        endTable();
    }

    /**
     * Outputs Interaction Data.
     */
    private void outputResults() throws DaoException {
        if (searchResults.size() == 0) {
            append("<TR>");
            append("<TD COLSPAN=4>No Matching Results found!");
            append("</TR>");
        }
        for (int i = 0; i < searchResults.size(); i++) {
            append("<TR>");
            CPathResult result = (CPathResult) searchResults.get(i);
            CPathRecord record = result.getRecord();
            outputDataField(Integer.toString(i));
            outputDataField(Float.toString(result.getScore()));
            outputDataField(record.getName());
            outputDataField(record.getDescription());
            outputInteractionLink(record.getName());
            append("</TR>");
        }
    }

    private void outputInteractionLink(String uid) {
        String url = new String("dataservice?version=1.0&cmd="
                + "retrieve_interactions&db=grid&&format=html&uid=" + uid);
        append("<TD>");
        this.outputLink("View Interactions", url);
        append("</TD>");
    }
}