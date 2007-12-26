// $Id: EvidenceUtil.java,v 1.6 2007-12-26 13:46:42 grossben Exp $
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
import org.mskcc.pathdb.model.Reference;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoReference;

import java.util.Map;
import java.util.List;
import java.util.HashMap;

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
    public static String getEvidenceHtml (List<Evidence> evidenceList) throws DaoException {

		// buffer to return
        StringBuffer html = new StringBuffer("");
		Map<String,String> links = new HashMap<String,String>();
		Map<String,String> references = new HashMap<String, String>();

        // iterate over list of evidence list
		if (evidenceList.size() > 0) {
            DaoReference daoReference = new DaoReference();
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
				// used for pubmed abstract title
				Reference reference = daoReference.getRecord(externalLinkRecord.getLinkedToId(),
															 externalLinkRecord.getExternalDatabase().getId());
				// evidence code/terms
				String terms = "";
				List<Evidence.Code> evidenceCodes = evidence.getCodes(); 
				if (evidenceCodes != null && evidenceCodes.size() > 0) {
					for (Evidence.Code code : evidenceCodes) {
						for (String term : code.getTerms()) {
							if (term.length() > 0 && !term.equals("N/A")) {
								terms += term + "; ";
							}
						}
					}
				}
				uri = (uri == null) ? "" : uri;
				if (links.containsKey(uri)) {
					terms = links.get(uri) + terms;
				}
				links.put(uri, terms);
				references.put(uri, (reference != null) ? reference.getReferenceString() : "");
			}
			for (String uri : links.keySet()) {
				html.append("<li>");
				html.append(links.get(uri) +
							references.get(uri) + " " +
							"[<a href=\"" + uri + "\">PubMed</a>]");
				html.append("</li>\n\r");
            }
            html.append("</ul>\n\r");
        }

		//  outta here
        return html.toString();
    }
}
