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
import java.util.Vector;
import java.io.StringReader;

import org.jdom.Element;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.jdom.input.SAXBuilder;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.model.CPathRecord;

/**
 * This class contains utilities
 * functions.
 *
 * @author Benjamin Gross.
 */
public class BioPaxRecordUtil {

    /**
     * Gets Physical Entity.
	 *
	 * @param record String.
	 * @return String.
	 * @throws Exception.
     */
	public static String getEntity(String record) throws Exception {

		// get CPathRecord given record id argument
		int indexOfId = record.lastIndexOf("-");
		if (indexOfId == -1){
			return null;
		}
		indexOfId += 1;
		String cookedRecord = record.substring(indexOfId);
		Long id = new Long(cookedRecord);
		DaoCPath cPath = DaoCPath.getInstance();
		CPathRecord cPathRecord = cPath.getRecordById(id.longValue());

		return getEntityName(cPathRecord.getXmlContent());
	}

    /**
     * Gets Entity Name, return as link.
	 *
	 * @param recordID long.
	 * @param xmlContent String.
	 * @return String.
	 * @throws Exception.
     */
	public static String getEntity(long recordID, String xmlContent) throws Exception {

		// string to return
		String entity = getEntityName(xmlContent);
		return (entity != null) ?
			new String("<a href=\"record.do?id=" +
					   String.valueOf(recordID) +
					   "\">" + entity +
					   "</a>")
			: null;
	}


    /**
     * Gets Entity Name, return as link.
	 *
	 * @param xmlContent String.
	 * @return String.
	 * @throws Exception.
     */
	private static String getEntityName(String xmlContent) throws Exception {

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
				return new String(e.getTextNormalize());
			}
		}
		return null;
	}
	
}
