package org.mskcc.pathdb.util;

import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.model.CPathRecord;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.util.Iterator;
import java.util.Collection;
import java.io.StringReader;

/**
 * Given a series of XML Fragments, this class builds one complete
 * PSI document.
 *
 * @author Ethan Cerami
 */
public class PsiBuilder {

    /**
     * Constructor.
     * @param interactors Collection of Castor ProteinIteractorType objects.
     * @param interactions Collection of Castor InteractionElementType objects.
     * @return Castor Entry Set Object.
     * @throws org.exolab.castor.xml.MarshalException Error Marshalling Object.
     * @throws org.exolab.castor.xml.ValidationException XML is not valid.
     */
    public EntrySet generatePsi(Collection interactors,
            Collection interactions) throws MarshalException,
            ValidationException {
        InteractorList interactorList = generateInteractorList(interactors);
        InteractionList interactionList = generateInteractionList(interactions);
        EntrySet entrySet = generateEntrySet(interactorList, interactionList);
        return entrySet;
    }

    /**
     * Generates Interaction List from Collection.
     */
    private InteractionList generateInteractionList(Collection interactions)
            throws MarshalException, ValidationException {
        InteractionList interactionList = new InteractionList();
        Iterator iterator = interactions.iterator();
        while (iterator.hasNext()) {
            CPathRecord record = (CPathRecord) iterator.next();
            String xml = record.getXmlContent();
            StringReader reader = new StringReader(xml);
            InteractionElementType cInteraction =
                    InteractionElementType.unmarshalInteractionElementType
                    (reader);
            interactionList.addInteraction(cInteraction);
        }
        return interactionList;
    }

    /**
     * Generates Interactor List from Collection.
     */
    private InteractorList generateInteractorList(Collection interactors)
            throws MarshalException, ValidationException {
        Iterator iterator = interactors.iterator();
        InteractorList interactorList = new InteractorList();
        while (iterator.hasNext()) {
            CPathRecord record = (CPathRecord) iterator.next();
            String xml = record.getXmlContent();
            StringReader reader = new StringReader(xml);
            ProteinInteractorType cProtein =
                    ProteinInteractorType.unmarshalProteinInteractorType
                    (reader);
            interactorList.addProteinInteractor(cProtein);
        }
        return interactorList;
    }

    /**
     * Generates Root Entry Set Object.
     */
    private EntrySet generateEntrySet(InteractorList interactorList,
            InteractionList interactionList) {
        EntrySet entrySet = new EntrySet();
        entrySet.setLevel(1);
        entrySet.setVersion(1);
        Entry entry = new Entry();
        entrySet.addEntry(entry);
        entry.setInteractorList(interactorList);
        entry.setInteractionList(interactionList);
        return entrySet;
    }
}