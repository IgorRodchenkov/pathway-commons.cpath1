// $Id: UpdateRdfLinks.java,v 1.7 2006-11-16 15:40:30 cerami Exp $
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
package org.mskcc.pathdb.schemas.biopax;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.xpath.XPath;
import org.mskcc.pathdb.util.rdf.RdfConstants;
import org.mskcc.pathdb.util.rdf.RdfUtil;

import java.util.*;

/**
 * Updates all RDF "id" and RDF "resource" attributes to use newly updated
 * RDF IDs.
 * <p/>
 * This class requires a HashMap which maps old IDs to new IDs.
 * <p/>
 * For example, if we have an ID map like this:
 * <p/>
 * (String) catalysis43 -->  (Long) 1
 * <p/>
 * and, we encounter an element like this:
 * <p/>
 * &lt;bp:STEP-INTERACTIONS rdf:resource="#catalysis43" /&gt;
 * <p/>
 * and, our ID prefix is"CPATH-", the element is modified like so:
 * <p/>
 * &lt;bp:STEP-INTERACTIONS rdf:resource="#CPATH-1" /&gt;
 * <p/>
 * This class performs in-place memory changes on the original JDOM elements.
 * <p/>
 *
 * @author Ethan Cerami
 */
public class UpdateRdfLinks {
    private HashMap globalLinks = new HashMap ();

    /**
     * Updates all RDF id and resource attributes to use newly updated RDF IDs.
     * See Class Comments for full details.
     *
     * @param e            JDOM Element      
     * @param idMap        HashMap of old IDs (String Objects) to new
     *                     IDs (Long Objects);
     * @param idPrefix     An ID Prefix, e.g.  CPathIdFilter.CPATH_PREFIX.
     * @throws JDOMException Error Processing XML.
     */
    public void updateInternalLinks (Element e, HashMap idMap, String idPrefix)
            throws JDOMException {
        //  Update the RDF ID
        Attribute idAttribute = e.getAttribute (RdfConstants.ID_ATTRIBUTE,
                RdfConstants.RDF_NAMESPACE);
        Long newId = updateIdAttribute (idAttribute, idMap, idPrefix);

        //  Get all RDF resource attributes
        XPath xpath = XPath.newInstance ("//@rdf:resource");
        xpath.addNamespace ("rdf", RdfConstants.RDF_NAMESPACE_URI);
        List links = xpath.selectNodes (e);

        Set internalLinks = new HashSet ();
        //  Iterate through all RDF Links
        for (int j = 0; j < links.size (); j++) {
            Attribute link = (Attribute) links.get (j);
            String key = RdfUtil.removeHashMark (link.getValue ());

            //  If we are pointing to a resource that now has a new
            //  ID, update the pointer.
            Object newLinkId = idMap.get (key);
            if (newLinkId != null) {
                link.setValue (createNewLink (idPrefix, newLinkId));
                //  Store the Internal Links for future reference
                internalLinks.add (newLinkId);
            }
        }
        globalLinks.put (newId, convertToLongArray (internalLinks));
    }

    private long[] convertToLongArray (Set internalLinks) {
        long ids[] = new long[internalLinks.size ()];
        Iterator iter = internalLinks.iterator ();
        int i = 0;
        while (iter.hasNext ()) {
            Long id = (Long) iter.next ();
            ids[i++] = id.longValue ();
        }
        return ids;
    }

    /**
     * Gets an Array of Internal Links for Specified cPath ID.
     *
     * @param cpathId cPath ID
     * @return Array of longs, representing cPath Ids.
     */
    public long[] getInternalLinks (long cpathId) {
        return (long[]) globalLinks.get (new Long (cpathId));
    }

    /**
     * Updates an RDF ID Attribute.
     */
    private Long updateIdAttribute (Attribute idAttribute,
            HashMap idMap, String idPrefix) {
        if (idAttribute != null) {
            String key = idAttribute.getValue ();
            Long newId = (Long) idMap.get (key);
            if (newId != null) {
                idAttribute.setValue (new String (idPrefix + newId.toString ()));
                return newId;
            }
        }
        return null;
    }

    private String createNewLink (String idPrefix, Object newId) {
        return new String ("#" + idPrefix + newId);
    }
}
