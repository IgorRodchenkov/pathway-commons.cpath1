package org.mskcc.pathdb.service;

import org.jdom.Document;
import org.jdom.Element;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.GoVocab;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.protocol.GridProtocol;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Live GRID_LOCAL Interactor Service.
 * Connects to the GRID_LOCAL Database.
 * Information about GRID_LOCAL is available online at:
 * <A HREF="http://biodata.mshri.on.ca/grid/servlet/Index">GRID_LOCAL</A>.
 *
 * @author Ethan Cerami
 */
public class GridInteractorUtil {

    /**
     * Gets Basic information regarding interactor.
     *
     * @param doc        XML Document containting Database Result Set.
     * @param interactor Interactor object.
     */
    private void getBasicInformation(Document doc, Interactor interactor) {
        String localId = getString(doc, "id");
        String orfName = getString(doc, "orf_name");
        String geneNameList = getString(doc, "gene_names");
        String geneNames[] = GridProtocol.splitString(geneNameList);
        String description = getString(doc, "description");
        interactor.setName(orfName);
        interactor.setDescription(description);
        interactor.addAttribute(InteractorVocab.LOCAL_ID, localId);
        ArrayList list = new ArrayList();
        for (int i = 0; i < geneNames.length; i++) {
            list.add(geneNames[i]);
        }
        interactor.addAttribute(InteractorVocab.GENE_NAME, list);
    }

    /**
     * Gets the GO (Gene Ontology) Terms.
     *
     * @param doc        XML Document containting Database Result Set.
     * @param interactor Interactor object.
     */
    private void getGoTerms(Document doc, Interactor interactor) {
        ArrayList functionList = getGoTerms(doc, "function");
        ArrayList processList = getGoTerms(doc, "process");
        ArrayList compList = getGoTerms(doc, "component");
        ArrayList specialList = getGoTerms(doc, "special");
        interactor.addAttribute(GoVocab.GO_CATEGORY_FUNCTION, functionList);
        interactor.addAttribute(GoVocab.GO_CATEGORY_PROCESS, processList);
        interactor.addAttribute(GoVocab.GO_CATEGORY_COMPONENT, compList);
        interactor.addAttribute(GoVocab.GO_CATEGORY_SPECIAL, specialList);
    }

    /**
     * Gets the list of External References.
     *
     * @param doc        XML Document containting Database Result Set.
     * @param interactor Interactor object.
     */
    private void getExternalRefs(Document doc, Interactor interactor) {
        ExternalReference refs[];
        String dbIds = getString(doc, "external_ids");
        String dbNames = getString(doc, "external_names");
        String idArray[] = GridProtocol.splitString(dbIds);
        String nameArray[] = GridProtocol.splitString(dbNames);
        refs = new ExternalReference[idArray.length];
        for (int i = 0; i < idArray.length; i++) {
            refs[i] = new ExternalReference(nameArray[i], idArray[i]);
        }
        interactor.setExternalRefs(refs);
    }

    /**
     * Parse Result Set.
     *
     * @param doc JDOM Document object.
     * @return Interactor object.
     */
    protected Interactor parseResults(Document doc) {
        Interactor interactor = new Interactor();
        getBasicInformation(doc, interactor);
        getGoTerms(doc, interactor);
        getExternalRefs(doc, interactor);
        return interactor;
    }

    /**
     * Gets GO Terms.
     *
     * @param doc          XML Document containting Database Result Set.
     * @param columnPrefix Column Prefix, for example "function".
     * @return ArrayList of HashMaps objects.
     */
    private ArrayList getGoTerms(Document doc, String columnPrefix) {
        String goIds = getString(doc, columnPrefix + "_ids");
        String goNames = getString(doc, columnPrefix + "_names");
        String[] idArray = splitGoIds(goIds);
        String[] nameArray = GridProtocol.splitString(goNames);
        ArrayList terms = new ArrayList();
        for (int i = 0; i < idArray.length; i++) {
            HashMap goTerm = new HashMap();
            goTerm.put(GoVocab.GO_ID, idArray[i]);
            goTerm.put(GoVocab.GO_NAME, nameArray[i]);
            terms.add(goTerm);
        }
        return terms;
    }

    /**
     * Splits and Massages GO Ids.
     *
     * @param idStr String with multiple GoIDs.
     * @return Array of String Objects.
     */
    private String[] splitGoIds(String idStr) {
        String ids[] = GridProtocol.splitString(idStr);
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].startsWith(GridProtocol.GO_PREFIX)) {
                ids[i] = ids[i].substring(GridProtocol.GO_PREFIX.length());
            }
        }
        return ids;
    }

    /**
     * Extract String from XML Document.
     *
     * @param document    JDOM Document.
     * @param elementName Element Name Target.
     * @return String value.
     */
    private String getString(Document document, String elementName) {
        Element root = document.getRootElement();
        Element entry = root.getChild("entry");
        if (entry != null) {
            Element element = entry.getChild(elementName);
            if (element != null) {
                return element.getTextTrim();
            }
        }
        return null;
    }
}