// $Id: XmlRecordType.java,v 1.5 2006-03-06 17:29:04 cerami Exp $
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
 * Enumeration of XML Record Types.
 * The list is currently constrained to:  PSI_MI and BIO_PAX.
 *
 * @author Ethan Cerami
 */
public class XmlRecordType implements Serializable {
    private String name;

    /**
     * Private Constructor. Enumeration Pattern.
     *
     * @param name Type Name.
     */
    private XmlRecordType(String name) {
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
     * @param typeName Type Name, e.g. "PSI_MI", or "BIOPAX".
     * @return correct XmlRecordType.
     */
    public static XmlRecordType getType(String typeName) {
        if (typeName.equals(PSI_MI.toString())) {
            return PSI_MI;
        } else if (typeName.equals(BIO_PAX.toString())) {
            return BIO_PAX;
        } else {
            throw new IllegalArgumentException("No Matching cPath"
                    + "Record types for:  " + typeName);
        }
    }

    /**
     * XmlRecordType Record Type:  PSI_MI.
     */
    public static final XmlRecordType PSI_MI
            = new XmlRecordType("PSI_MI");

    /**
     * CPath Record Type:  BIO_PAX.
     */
    public static final XmlRecordType BIO_PAX
            = new XmlRecordType("BIO_PAX");

}
