// $Id: CPathIdFilter.java,v 1.6 2006-02-22 22:47:51 grossb Exp $
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
package org.mskcc.pathdb.sql.assembly;

import org.mskcc.dataservices.schemas.psi.*;

/**
 * Transforms a PSI-MI Document with invalid IDs to valid IDs.
 * <p/>
 * Background:  By default, cPath uses integer values for all internal
 * identifers.  For example, a protein might have a cPath identifier set to
 * 12345.  However, in PSI-MI, protein IDs must be of type NCNAME, which
 * requires:
 * <P>NCName ::= (Letter | '_') (NCNameChar)*
 * <P>
 * If we output the protein Ids as is, cPath therefore generates invalid XML
 * (see Bug #0000290 for more details.)
 * <p/>
 * Rather than reimplement everything in cPath to use string identifiers,
 * this class simply transforms a PSI-MI document by converting all invalid
 * identifiers, e.g. 12345 to valid identifiers, e.g. "CPATH-12345".
 * <p/>
 * The main advantage of using a filter like this is that we can keep
 * all internal identifiers as integers, and not risk any changes to the
 * core import, index and assembly code.  The other advantage is that
 * the use of NCName will probably be dropped from the PSI Schema
 * in the near future.  When that happens, we simply drop this filter class.
 * <p/>
 * Note that a unit test for this filter exists in:
 * test/sql/assembly/TestAssembly.java.
 *
 * @author Ethan Cerami
 */
public class CPathIdFilter {
    /**
     * The CPATH ID Prefix.
     */
    public static final String CPATH_PREFIX = "CPATH-";

    /**
     * Adds cPathID Prefixes to all IDs and ID References.
     * See class description for complete details.
     *
     * @param entrySet EntrySet Object.
     * @return EntrySet Object.
     */
    public static EntrySet addCPathIdPrefix(EntrySet entrySet) {
        return filterIds(entrySet);
    }

    private static EntrySet filterIds(EntrySet entrySet) {
        for (int i = 0; i < entrySet.getEntryCount(); i++) {
            Entry entry = entrySet.getEntry(i);
            InteractorList interactorList = entry.getInteractorList();
            InteractionList interactionList = entry.getInteractionList();
            filterInteractorList(interactorList);
            filterInteractionList(interactionList);
        }
        return entrySet;
    }

    private static void filterInteractorList(InteractorList interactorList) {
        for (int i = 0; i < interactorList.getProteinInteractorCount(); i++) {
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            String id = CPATH_PREFIX + protein.getId();
            protein.setId(id);
        }
    }

    private static void filterInteractionList
            (InteractionList interactionList) {
        for (int i = 0; i < interactionList.getInteractionCount(); i++) {
            InteractionElementType interaction =
                    interactionList.getInteraction(i);
            ParticipantList pList = interaction.getParticipantList();
            for (int j = 0; j < pList.getProteinParticipantCount(); j++) {
                ProteinParticipantType pType = pList.getProteinParticipant(j);
                ProteinParticipantTypeChoice choice =
                        pType.getProteinParticipantTypeChoice();
                RefType refType = choice.getProteinInteractorRef();
                String id = CPATH_PREFIX + refType.getRef();
                refType.setRef(id);
            }
        }
    }
}
