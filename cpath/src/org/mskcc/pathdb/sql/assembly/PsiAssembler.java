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
package org.mskcc.pathdb.sql.assembly;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.pathdb.model.CPathRecord;

import java.io.StringReader;
import java.util.Collection;
import java.util.Iterator;

/**
 * Given a series of XML Fragments, this class builds one complete
 * PSI-MI XML document.
 *
 * @author Ethan Cerami
 */
public class PsiAssembler {
    /**
     * Public CPath Identifier.
     */
    public static final String CPATH_DB = "CPATH";

    /**
     * Constructor.
     *
     * @param interactors  Collection of Castor ProteinIteractorType objects.
     * @param interactions Collection of Castor InteractionElementType objects.
     * @return Castor Entry Set Object.
     * @throws MarshalException    Error Marshalling Object.
     * @throws ValidationException XML is not valid.
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

            //  Append cPath ID as a Reference
            XrefType xref = cInteraction.getXref();
            if (xref == null) {
                xref = new XrefType();
                cInteraction.setXref(xref);
            }
            DbReferenceType ref = new DbReferenceType();
            ref.setDb(CPATH_DB);
            ref.setId(Long.toString(record.getId()));
            if (xref.getPrimaryRef() == null) {
                xref.setPrimaryRef(ref);
            } else {
                xref.addSecondaryRef(ref);
            }
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