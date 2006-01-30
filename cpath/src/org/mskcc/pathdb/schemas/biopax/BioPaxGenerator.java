/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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

import org.jdom.Element;
import org.jdom.Namespace;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoIdGenerator;
import org.mskcc.pathdb.util.rdf.RdfConstants;

/**
 * Utility Class for generating BioPAX formatted data.
 *
 * @author Ethan Cerami
 */
public class BioPaxGenerator {

    /**
     * Generates a BioPAX Relationship XREF Element.
     *
     * @param ref             ExternalReference Object.
     * @param bioPaxNamespace BioPAX Namespace Object.
     * @return JDOM Element of a BioPAX XREF
     * @throws DaoException Error Accessing Database
     */
    public static Element generateRelationshipXref(ExternalReference ref,
                                                   Namespace bioPaxNamespace) throws DaoException {
        return generateXref(ref, bioPaxNamespace,
                BioPaxConstants.XREF_RELATIONSHIP);
    }

    /**
     * Generates a BioPAX Unification XREF Element.
     *
     * @param ref             ExternalReference Object.
     * @param bioPaxNamespace BioPAX Namespace Object.
     * @return JDOM Element of a BioPAX XREF
     * @throws DaoException Error Accessing Database
     */
    public static Element generateUnificationXref(ExternalReference ref,
                                                  Namespace bioPaxNamespace) throws DaoException {
        return generateXref(ref, bioPaxNamespace,
                BioPaxConstants.XREF_UNIFICATION);
    }

    /**
     * Appends a BioPAX Relationship XREF to the specified Element.
     *
     * @param ref ExternalReference Object.
     * @param e   JDOM Element.
     * @throws DaoException Error Accessing Database.
     */
    public static void appendRelationshipXref(ExternalReference ref, Element e)
            throws DaoException {
        Element xrefElement = generateRelationshipXref(ref, e.getNamespace());
        e.addContent(xrefElement);
    }

    /**
     * Appends a BioPAX Unification XREF to the specified Element.
     *
     * @param ref ExternalReference Object.
     * @param e   JDOM Element.
     * @throws DaoException Error Accessing Database.
     */
    public static void appendUnificationXref(ExternalReference ref, Element e)
            throws DaoException {
        Element xrefElement = generateUnificationXref(ref, e.getNamespace());
        e.addContent(xrefElement);
    }

    /**
     * Generates a BioPAX XREF Element.
     *
     * @param ref             ExternalReference Object.
     * @param bioPaxNamespace BioPAX Namespace Object.
     * @return JDOM Element of a BioPAX XREF
     * @throws DaoException Error Accessing Database
     */
    private static Element generateXref(ExternalReference ref,
                                        Namespace bioPaxNamespace, String xrefType) throws DaoException {

        //  Create Element
        Element xrefElement = new Element("XREF", bioPaxNamespace);
        Element relationshipElement = new Element(xrefType,
                bioPaxNamespace);
        Element dbElement = new Element("DB", bioPaxNamespace);
        Element idElement = new Element("ID", bioPaxNamespace);

        //  Set RDF Data Types
        dbElement.setAttribute(RdfConstants.getStringDataTypeAttribute());
        idElement.setAttribute(RdfConstants.getStringDataTypeAttribute());

        //  Set Text
        dbElement.setText(ref.getDatabase());
        idElement.setText(ref.getId());

        //  Build Hierarchy
        relationshipElement.addContent(dbElement);
        relationshipElement.addContent(idElement);
        xrefElement.addContent(relationshipElement);

        //  Set an RDF ID, based on locally generated ID.
        DaoIdGenerator idGenerator = new DaoIdGenerator();
        relationshipElement.setAttribute(RdfConstants.ID_ATTRIBUTE,
                idGenerator.getNextId());
        return xrefElement;
    }
}
