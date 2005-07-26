package org.mskcc.pathdb.schemas.externalDb;

import org.jdom.input.SAXBuilder;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.xpath.XPath;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ReferenceType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Imports an External DB XML File into a List of ExternalDatabaseRecord
 * Objects.
 *
 * @author Ethan Cerami
 */
public class ExternalDbXmlUtil {
    private static final String TYPE_ATTRIBUTE = "type";
    private static final String NAME_ELEMENT = "name";
    private static final String DESCRIPTION_ELEMENT = "description";
    private static final String URL_ELEMENT = "url";
    private static final String URL_PATTERN_ELEMENT = "url_pattern";
    private static final String SAMPLE_ID_ELEMENT = "sample_id";
    private static final String CONTROLLED_TERMS_ELEMENT = "controlled_terms";
    private static final String MASTER_TERM_ELEMENT = "master_term";
    private static final String SYNONYM_ELEMENT = "synonym";
    private static final String SLASH = "/";
    private ArrayList dbList;

    /**
     * Constructor.
     * @param file Input File containing External Database XML.
     * @throws IOException I/O Error.
     * @throws JDOMException XML Error.
     */
    public ExternalDbXmlUtil (File file) throws IOException,
            JDOMException {

        dbList = new ArrayList();
        SAXBuilder builder = new SAXBuilder (true);
        Document doc = builder.build(file);
        Element root = doc.getRootElement();
        List children = root.getChildren();
        for (int i=0; i<children.size(); i++) {
            Element externalDb = (Element) children.get(i);
            String type = externalDb.getAttributeValue(TYPE_ATTRIBUTE);
            String name = extractElementText (externalDb, NAME_ELEMENT);
            String description = extractElementText (externalDb,
                    DESCRIPTION_ELEMENT);
            String urlPattern = extractElementText (externalDb,
                    URL_ELEMENT + SLASH + URL_PATTERN_ELEMENT);
            String sampleId = extractElementText (externalDb,
                    URL_ELEMENT + SLASH + SAMPLE_ID_ELEMENT);
            String masterTerm = extractElementText (externalDb,
                    CONTROLLED_TERMS_ELEMENT + SLASH + MASTER_TERM_ELEMENT);
            ArrayList synonymList = extractSynonymList(externalDb);

            ExternalDatabaseRecord dbRecord = new ExternalDatabaseRecord();
            dbRecord.setName(name);
            dbRecord.setDbType(ReferenceType.getType(type));
            dbRecord.setDescription(description);
            dbRecord.setUrl(urlPattern);
            dbRecord.setSampleId(sampleId);
            dbRecord.setMasterTerm(masterTerm);
            dbRecord.setSynonymTerms(synonymList);
            dbList.add(dbRecord);
        }
    }

    /**
     * Gets List of External Database Objects.
     * @return ArrayList of ExternalDatabaseRecord Objects.
     */
    public ArrayList getExternalDbList() {
        return dbList;
    }

    private ArrayList extractSynonymList(Element externalDb)
            throws JDOMException {
        ArrayList synonymList = new ArrayList();
        XPath xpath = XPath.newInstance(CONTROLLED_TERMS_ELEMENT
            + SLASH + SYNONYM_ELEMENT);
        List synList = xpath.selectNodes(externalDb);
        if (synList != null) {
            for (int j=0; j<synList.size(); j++) {
                Element synElement = (Element) synList.get(j);
                synonymList.add(synElement.getTextNormalize());
            }
        }
        return synonymList;
    }

    private String extractElementText (Element e, String xpathQuery)
            throws JDOMException {
        XPath xpath = XPath.newInstance(xpathQuery);
        Element target = (Element) xpath.selectSingleNode(e);
        if (target != null) {
            return target.getTextNormalize();
        } else {
            return null;
        }
    }
}