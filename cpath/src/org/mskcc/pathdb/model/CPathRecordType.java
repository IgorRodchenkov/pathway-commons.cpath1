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
 * Enumeration of CPathRecord Types.
 * This list is currently constrained to:  PHYSICAL_ENTITY, INTERACTION,
 * and PATHWAY.
 *
 * @author Ethan Cerami
 */
public class CPathRecordType {
    private String name;

    /**
     * Private Constructor. Enumeration Pattern.
     *
     * @param name Type Name.
     */
    private CPathRecordType(String name) {
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
     * @param typeName Type Name, e.g. "PHYSICAL_ENTITY", "INTERACTION"
     *                 or "PATHWAY".
     * @return correct CPathRecordType.
     */
    public static CPathRecordType getType(String typeName) {
        if (typeName.equals(PHYSICAL_ENTITY.toString())) {
            return PHYSICAL_ENTITY;
        } else if (typeName.equals(INTERACTION.toString())) {
            return INTERACTION;
        } else if (typeName.equals(PATHWAY.toString())) {
            return PATHWAY;
        } else {
            throw new IllegalArgumentException("No Matching cPath"
                    + "Record types for:  " + typeName);
        }
    }

    /**
     * CPath Record Type:  PHYSICAL_ENTITY.
     */
    public static final CPathRecordType PHYSICAL_ENTITY
            = new CPathRecordType("PHYSICAL_ENTITY");

    /**
     * CPath Record Type:  INTERACTION.
     */
    public static final CPathRecordType INTERACTION
            = new CPathRecordType("INTERACTION");

    /**
     * CPath Record Type:  PATHWAY.
     */
    public static final CPathRecordType PATHWAY
            = new CPathRecordType("PATHWAY");

}
