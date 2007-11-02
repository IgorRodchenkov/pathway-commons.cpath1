// $Id: EvidenceUtil.java,v 1.1 2007-11-02 13:17:26 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
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

import org.mskcc.pathdb.model.Evidence;
import org.mskcc.pathdb.model.Evidence.Code;
import org.mskcc.pathdb.model.ExternalLinkRecord;

import java.util.List;

/**
 * Evidence Utility Class.
 *
 * @author Benjamin Gross
 */
public class EvidenceUtil {

    /**
     * Gets experiment html.
     * @param evidenceList List<Evidence>
     * @return HTML String
     */
    public static String getEvidenceHtml (List<Evidence> evidenceList) {

		// buffer to return
        StringBuffer html = new StringBuffer("");

        // iterate over list of evidence list
		if (evidenceList.size() > 0) {
		    html.append("<p><b>Experiment Type:</b></p>\n\r");
            html.append("<ul>\n\r");
            for (Evidence evidence : evidenceList) {
				// external link - only process pubmed
				ExternalLinkRecord externalLinkRecord = null;
				if (evidence.getExternalLinks() == null) continue;
				for (ExternalLinkRecord link : evidence.getExternalLinks()) {
					String dbName = link.getExternalDatabase().getName();
					if (dbName != null && dbName.equalsIgnoreCase("PUBMED")) {
						externalLinkRecord = link;
						break;
					}
				}
				if (externalLinkRecord == null) continue;
				String uri = (externalLinkRecord.getWebLink() == null) ? "" :
					externalLinkRecord.getWebLink();
				// evidence code
				for (Evidence.Code code : evidence.getCodes()) {
					html.append("<li>");
					String terms = "";
					for (String term : code.getTerms()) {
						terms += term + " ;";
					}
					terms = terms.replaceAll(" ;$", "");
					uri = (uri == null) ? "" : uri;
					html.append("[<a href=\"" + uri + "\">" + terms + "</a>]");
					html.append("</li>\n\r");
				}
            }
            html.append("</ul>\n\r");
        }

		//  outta here
        return html.toString();
    }
}
