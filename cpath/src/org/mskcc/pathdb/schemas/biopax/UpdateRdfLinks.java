package org.mskcc.pathdb.schemas.biopax;

import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.Attribute;
import org.jdom.xpath.XPath;
import org.mskcc.pathdb.util.rdf.RdfUtil;

import java.util.*;

/**
 * Updates all RDF "id" and RDF "resource" attributes to use newly updated
 * RDF IDs.
 * <P>
 * This class requires a HashMap which maps old IDs to new IDs.
 * <P>
 * For example, if we have an ID map like this:
 * <P>
 * (String) catalysis43 -->  (Long) 1
 * <P>
 * and, we encounter an element like this:
 * <P>
 * &lt;bp:STEP-INTERACTIONS rdf:resource="#catalysis43" /&gt;
 * <P>
 * and, our ID prefix is"CPATH-", the element is modified like so:
 * <P>
 * &lt;bp:STEP-INTERACTIONS rdf:resource="#CPATH-1" /&gt;
 * <P>
 * This class performs in-place memory changes on the original JDOM elements.
 * <P>
 * @author Ethan Cerami
 */
public class UpdateRdfLinks {
    private HashMap globalLinks = new HashMap();

    /**
     * Updates all RDF id and resource attributes to use newly updated RDF IDs.
     * See Class Comments for full details.
     *
     * @param resourceList  ArrayList of JDOM Element Objects.
     * @param idMap         HashMap of old IDs (String Objects) to new
     *                      IDs (Long Objects);
     * @param idPrefix        An ID Prefix, e.g.  CPathIdFilter.CPATH_PREFIX.
     */
    public void updateInternalLinks (ArrayList resourceList,
            HashMap idMap, String idPrefix) throws JDOMException {
        //  Iterate through all RDF resources
        for (int i = 0; i < resourceList.size(); i++) {

            //  Get next RDF resource
            Element e = (Element) resourceList.get(i);

            //  Update the RDF ID
            Attribute idAttribute = e.getAttribute(RdfConstants.ID_ATTRIBUTE,
                    RdfConstants.RDF_NAMESPACE);
            Long newId = updateIdAttribute (idAttribute, idMap, idPrefix);

            //  Get all RDF resource attributes
            XPath xpath = XPath.newInstance("//@rdf:resource");
            xpath.addNamespace("rdf", RdfConstants.RDF_NAMESPACE_URI);
            List links = xpath.selectNodes(e);

            Set internalLinks = new HashSet();
            //  Iterate through all RDF Links
            for (int j=0; j<links.size(); j++) {
                Attribute link = (Attribute) links.get(j);
                String key = RdfUtil.removeHashMark(link.getValue());

                //  If we are pointing to a resource that now has a new
                //  ID, update the pointer.
                Object newLinkId = idMap.get(key);
                if (newLinkId != null) {
                    link.setValue(createNewLink(idPrefix, newLinkId));
                    //  Store the Internal Links for future reference
                    internalLinks.add(newLinkId);
                }
            }
            globalLinks.put (newId, convertToLongArray(internalLinks));
        }
    }

    private long[] convertToLongArray(Set internalLinks) {
        long ids[] = new long[internalLinks.size()];
        Iterator iter = internalLinks.iterator();
        int i = 0;
        while (iter.hasNext()) {
            Long id = (Long) iter.next();
            ids[i++] = id.longValue();
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
        return (long[]) globalLinks.get(new Long (cpathId));
    }

    /**
     * Updates an RDF ID Attribute.
     */
    private Long updateIdAttribute (Attribute idAttribute,
            HashMap idMap, String idPrefix) {
        if (idAttribute != null) {
            String key = idAttribute.getValue();
            Long newId = (Long) idMap.get(key);
            if (newId != null) {
                idAttribute.setValue(new String (idPrefix + newId.toString()));
                return newId;
            }
        }
        return null;
    }

    private String createNewLink (String idPrefix, Object newId) {
        return new String ("#" + idPrefix + newId);
    }
}