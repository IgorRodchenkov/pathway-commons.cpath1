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
package org.mskcc.pathdb.model;

/**
 * Enumeration of ExternalDatabase Types.
 * This list is currently constrained to:  PROTEIN_UNIFICATION.
 *
 * @author Ethan Cerami
 */
public class ReferenceType {
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
            return null;
        } else if (typeName.equals(PROTEIN_UNIFICATION.toString())) {
            return PROTEIN_UNIFICATION;
        } else if (typeName.equals(LINK_OUT.toString())) {
            return LINK_OUT;
        } else {
            return null;
        }
    }

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