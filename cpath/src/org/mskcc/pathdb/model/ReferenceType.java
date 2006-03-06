// $Id: ReferenceType.java,v 1.10 2006-03-06 17:29:04 cerami Exp $
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

import java.io.Serializable;

/**
 * Enumeration of ExternalDatabase Types.
 *
 * @author Ethan Cerami
 */
public class ReferenceType implements Serializable {
    private String name;

    /**
     * Private Constructor. Enumeration Pattern.
     *
     * @param name Type Name.
     */
    private ReferenceType(String name) {
        this.name = name;
    }

    /**
     * Gets Type Name.
     *
     * @return Type Name.
     */
    public String toString() {
        return name;
    }

    /**
     * Get Type by Type Name.
     *
     * @param typeName Type Name, e.g. "PROTEIN_UNIFICATION".
     * @return correct ExternalDatabaseType.
     */
    public static ReferenceType getType(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("typeName is null");
        } else if (typeName.equals(PROTEIN_UNIFICATION.toString())) {
            return PROTEIN_UNIFICATION;
        } else if (typeName.equals(LINK_OUT.toString())) {
            return LINK_OUT;
        } else if (typeName.equals
                (INTERACTION_PATHWAY_UNIFICATION.toString())) {
            return INTERACTION_PATHWAY_UNIFICATION;
        } else {
            throw new NullPointerException("Cannot find:  " + typeName);
        }
    }

    /**
     * External Record Type:  INTERACTION_PATHWAY_UNIFICATION.
     */
    public static final ReferenceType INTERACTION_PATHWAY_UNIFICATION
            = new ReferenceType("INTERACTION_PATHWAY_UNIFICATION");

    /**
     * External Record Type:  PROTEIN_UNIFICATION.
     */
    public static final ReferenceType PROTEIN_UNIFICATION
            = new ReferenceType("PROTEIN_UNIFICATION");

    /**
     * External Record Type:  LINK_OUT.
     */
    public static final ReferenceType LINK_OUT
            = new ReferenceType("LINK_OUT");

}
