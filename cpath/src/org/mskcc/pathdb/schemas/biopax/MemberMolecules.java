/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
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

// package
package org.mskcc.pathdb.schemas.biopax;


// imports
import java.util.HashSet;
import java.util.Vector;
import java.util.ArrayList;
import java.io.StringReader;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;

/**
 * This class parses interaction data
 * given a cPath record id.
 *
 * @author Benjamin Gross.
 */
public class MemberMolecules {

	/**
	 * Finds all member molecules given CPathRecord.
	 *
	 * @return HashSet
	 */
	public static HashSet getMemberMolecules(CPathRecord record) throws Exception {

		// vector to return
		HashSet molecules = new HashSet();

		// get internal links
		DaoInternalLink daoInternalLinks = new DaoInternalLink();
		ArrayList internalLinks = daoInternalLinks.getTargetsWithLookUp(record.getId());

		if (internalLinks.size() > 0){
			for (int lc = 0; lc < internalLinks.size(); lc++){
				CPathRecord linkRecord = (CPathRecord)internalLinks.get(lc);
				molecules.addAll(getMemberMolecules(linkRecord));
			}
		}
		else{
			BioPaxConstants biopaxConstants = new BioPaxConstants();
			if (biopaxConstants.isPhysicalEntity(record.getSpecificType())){
				String molecule = getPhysicalEntity(record.getId(), record.getXmlContent());
				molecules.add(molecule);
			}
		}
		return molecules;
	}

    /**
     * Gets Physical Entity.
	 *
	 * @param recordID long.
	 * @param xmlContent String.
	 * @return String
     */
	private static String getPhysicalEntity(long recordID, String xmlContent) throws Exception {

		// string to return
		String link = null;

		// setup xml parsing
		Vector queries = new Vector();
		queries.add(new String("/*/bp:SHORT-NAME"));
		queries.add(new String("/*/bp:NAME"));
		queries.add(new String("/bp:NAME"));
		SAXBuilder builder = new SAXBuilder();
		StringReader reader = new StringReader(xmlContent);
		Document bioPaxDoc = builder.build(reader);
		Element root = bioPaxDoc.getRootElement();
		XPath xpath;
		for (int lc = 0; lc < queries.size(); lc++){
			xpath = XPath.newInstance((String)queries.elementAt(lc));
			xpath.addNamespace("bp", root.getNamespaceURI());
			Element e = (Element) xpath.selectSingleNode(root);
			if (e != null) {
				String physicalEntity = new String(e.getTextNormalize());
				link = new String("<a href=\"record.do?id=" +
								  String.valueOf(recordID) +
								  "\">" + physicalEntity +
								  "</a>");
				break;
			}
		}

		// outta here
		return link;
	}
}
