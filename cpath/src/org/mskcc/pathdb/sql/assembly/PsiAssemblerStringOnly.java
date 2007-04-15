package org.mskcc.pathdb.sql.assembly;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.dataservices.util.PropertyManager;

import java.util.Collection;
import java.util.Iterator;

/**
 * Given a series of XML Fragments, this class builds one complete
 * PSI-MI XML document.  This class is identical to PsiAssembler, except that the
 * class generates an XML document string only, and does not generate any Castor
 * objects.  This class was created because code profiling showed that Castor
 * marshalling/unmarshalling of large XML documents is a severe performance bottleneck.
 *
 * @author Ethan Cerami
 */
public class PsiAssemblerStringOnly {
    StringBuffer xmlDoc = new StringBuffer();

    /**
     * Generates PSI-MI Assembly.
     *
     * @param interactors  Collection of cPath Record objects for all interators.
     * @param interactions Collection of cPath Record objects for all interactions.
     * @return PSI-MI XML Document.
     */
    public String generatePsi(Collection interactors,
                              Collection interactions) {
        appendHeader(xmlDoc);
        appendInteractorList(interactors, xmlDoc);
        appendInteractionList(interactions, xmlDoc);
        appendFooter(xmlDoc);
        return xmlDoc.toString();
    }

    /**
     * Appends Interactor List from Collection.
     */
    private void appendInteractorList(Collection interactors, StringBuffer xmlDoc) {
        xmlDoc.append("<interactorList>\n");
        Iterator iterator = interactors.iterator();
        while (iterator.hasNext()) {
            CPathRecord record = (CPathRecord) iterator.next();
            String xml = record.getXmlContent();

            //  Change proteinInteractorType to proteinInteractor
            xml = xml.replaceAll("proteinInteractorType", "proteinInteractor");
            appendToDocStripHeader(xml, xmlDoc);
        }
        xmlDoc.append("</interactorList>\n");
    }

    /**
     * Appends Interaction List from Collection.
     */
    private void appendInteractionList(Collection interactions, StringBuffer xmlDoc) {
        xmlDoc.append("<interactionList>\n");
        Iterator iterator = interactions.iterator();
        while (iterator.hasNext()) {
            CPathRecord record = (CPathRecord) iterator.next();
            String xml = record.getXmlContent();

            //  Change interactionElementType to interaction
            xml = xml.replaceAll("interactionElementType", "interaction");
            appendToDocStripHeader(xml, xmlDoc);
        }
        xmlDoc.append("</interactionList>\n");
    }

    /**
     * Append to global document.
     * Strips out the <xml... header in each fragment.
     */
    private void appendToDocStripHeader(String xml, StringBuffer xmlDoc) {
        //  Each XML fragment may have an XML header;  strip it out.
        String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        if (xml.indexOf(xmlHeader) == 0) {
            xml = xml.substring(xmlHeader.length() + 1);
        }

        //  Replace redundant default XML namespace declarations
        String namespaceDecl = "xmlns=\"net:sf:psidev:mi\"";
        xml = xml.replaceAll(namespaceDecl, "");
        xmlDoc.append(xml);
    }

    /**
     * Appends XML Header.
     */
    private void appendHeader (StringBuffer xmlDoc) {
        PropertyManager pManager = PropertyManager.getInstance();
        String psiSchemaUrl = (String) pManager.get
                (CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION);
        xmlDoc.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xmlDoc.append("<entrySet xsi:schemaLocation=\"net:sf:psidev:mi "
            + psiSchemaUrl +"\""
            + " level=\"1\" version=\"1\" xmlns=\"net:sf:psidev:mi\""
            + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
        xmlDoc.append("<entry>\n");
    }

    /**
     * Appends XML Footer.
     */
    private void appendFooter (StringBuffer xmlDoc) {
        xmlDoc.append("</entry>\n");
        xmlDoc.append("</entrySet>\n");
    }
}