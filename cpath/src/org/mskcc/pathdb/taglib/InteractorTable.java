// $Id: InteractorTable.java,v 1.18 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.taglib;

import org.mskcc.dataservices.schemas.psi.NamesType;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.pathdb.lucene.LuceneConfig;
import org.mskcc.pathdb.model.ProteinWithWeight;
import org.mskcc.pathdb.protocol.ProtocolConstants;
import org.mskcc.pathdb.protocol.ProtocolRequest;

import java.util.Iterator;
import java.util.List;

/**
 * Custom JSP Tag for Displaying Matching Interactors.
 *
 * @author Ethan Cerami
 */
public class InteractorTable extends HtmlTable {
    private ProtocolRequest protocolRequest;
    private List interactorList;

    /**
     * Sets Interactor Set
     *
     * @param interactorList Interactor List
     */
    public void setInteractorList(List interactorList) {
        this.interactorList = interactorList;
    }

    /**
     * Sets Protocol Request Parameter.
     *
     * @param request Protocol Request
     */
    public void setProtocolRequest(ProtocolRequest request) {
        this.protocolRequest = request;
    }

    /**
     * Start Tag Processing.
     *
     * @throws org.mskcc.pathdb.sql.dao.DaoException
     *          Database Access Error.
     */
    protected void subDoStartTag() throws Exception {
        protocolRequest.setFormat(ProtocolConstants.FORMAT_XML);
        String query = protocolRequest.getQuery();
        if (query != null
                && (query.indexOf(LuceneConfig.FIELD_INTERACTOR_ID) == -1)
                && interactorList != null
                && interactorList.size() > 0) {
            append("<div id=\"highlight\" class=\"toolgroup\">\n"
                    + "<div class=\"label\">\n"
                    + "<strong>Matching Proteins</strong>\n"
                    + "</div>"
                    + "<div class=\"body\">");
            outputInteractors();
            append("</div>\n</div>");
        }
    }

    /**
     * Outputs All Interactors.
     */
    private void outputInteractors() {
        Iterator iterator = interactorList.iterator();
        while (iterator.hasNext()) {
            ProteinWithWeight proteinWithWeight = (ProteinWithWeight)
                    iterator.next();
            ProteinInteractorType protein = proteinWithWeight.getProtein();
            NamesType names = protein.getNames();
            String proteinId = protein.getId();
            append("<div>");
            String href = getInteractionLink(LuceneConfig.FIELD_INTERACTOR_ID
                    + ":" + proteinId, ProtocolConstants.FORMAT_HTML);
            String toolTip = TagUtil.getLabel(names);
            String label = TagUtil.truncateLabel(toolTip);
            String link = TagUtil.createLink(toolTip, href, label);
            append(link);
            append("</div>");
        }
    }
}
