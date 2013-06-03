// $Id: CvRecord.java,v 1.4 2006-02-22 22:47:50 grossb Exp $
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
package org.mskcc.pathdb.model;

import java.util.ArrayList;

/**
 * JavaBean to Encapsulate a set of Controlled Vocabulary Terms.
 * <p/>
 * Controlled Vocabulary terms are primarily used to identify external
 * databases.  For example, the terms:  SWISS-PROT, SWP and UNIPROT are all
 * controlled terms which map to the same SWISS-PROT external database.  Upon
 * import, we map individual terms to an external database, and then normalize
 * the term to the master term.  For example, if an import file contains
 * the terms:  SWISS-PROT or SWP, we map these to the SWISS-PROT database,
 * but normalize all the terms to UNIPROT.
 *
 * @author Ethan Cerami
 */
public class CvRecord {
    private String masterTerm;
    private ArrayList synonymTerms;

    /**
     * Gets the master controlled vocabulary term.
     *
     * @return term String.
     */
    public String getMasterTerm() {
        return masterTerm;
    }

    /**
     * Sets the master controlled vocabulary term.
     *
     * @param masterTerm term String.
     */
    public void setMasterTerm(String masterTerm) {
        this.masterTerm = masterTerm;
    }

    /**
     * Gets the List of Synonym Terms.
     *
     * @return ArrayList of String terms.
     */
    public ArrayList getSynonymTerms() {
        return synonymTerms;
    }

    /**
     * Sets the List of Synonmy Terms.
     *
     * @param synonymTerms ArrayList of String terms.
     */
    public void setSynonymTerms(ArrayList synonymTerms) {
        this.synonymTerms = synonymTerms;
    }
}
