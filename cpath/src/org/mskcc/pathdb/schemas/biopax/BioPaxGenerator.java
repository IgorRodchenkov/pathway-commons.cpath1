package org.mskcc.pathdb.schemas.biopax;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.dao.DaoIdGenerator;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Utility Class for generating BioPAX formatted data.
 *
 * @author Ethan Cerami
 */
public class BioPaxGenerator {

    /**
     * Generates a BioPAX Relationship XREF Element.
     * @param ref               ExternalReference Object.
     * @param bioPaxNamespace   BioPAX Namespace Object.
     * @return                  JDOM Element of a BioPAX XREF
     * @throws DaoException     Error Accessing Database
     */
    public static Element generateRelationshipXref (ExternalReference ref,
            Namespace bioPaxNamespace) throws DaoException {

        //  Create Element
        Element xrefElement = new Element ("XREF", bioPaxNamespace);
        Element relationshipElement = new Element ("relationshipXref",
                bioPaxNamespace);
        Element commentElement = new Element ("COMMENT", bioPaxNamespace);
        Element dbElement = new Element ("DB", bioPaxNamespace);
        Element idElement = new Element ("ID", bioPaxNamespace);

        //  Set RDF Data Types
        commentElement.setAttribute(RdfConstants.getStringDataTypeAttribute());
        dbElement.setAttribute(RdfConstants.getStringDataTypeAttribute());
        idElement.setAttribute(RdfConstants.getStringDataTypeAttribute());

        //  Set Text
        commentElement.setText("Relationship XRef added by cPath");
        dbElement.setText(ref.getDatabase());
        idElement.setText(ref.getId());

        //  Build Hierarchy
        relationshipElement.addContent(commentElement);
        relationshipElement.addContent(dbElement);
        relationshipElement.addContent(idElement);
        xrefElement.addContent  (relationshipElement);

        //  Set an RDF ID, based on locally generated ID.
        DaoIdGenerator idGenerator = new DaoIdGenerator();
        relationshipElement.setAttribute(RdfConstants.ID_ATTRIBUTE,
                idGenerator.getNextId());
        return xrefElement;
    }

    /**
     * Appends a BioPAX Relationship XREF to the specified Element.
     * @param ref               ExternalReference Object.
     * @param e                 JDOM Element.
     * @throws DaoException     Error Accessing Database.
     */
    public static void appendRelationshipXref (ExternalReference ref, Element e)
        throws DaoException {
        Element xrefElement = generateRelationshipXref(ref, e.getNamespace());
        e.addContent(xrefElement);
    }
}
