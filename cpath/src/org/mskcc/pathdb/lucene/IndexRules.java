// $Id: IndexRules.java,v 1.4 2006-02-22 22:47:50 grossb Exp $
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
package org.mskcc.pathdb.lucene;

import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.assembly.XmlAssemblyFactory;

/**
 * Encapsulates rules for what/how cPath records should be indexed by Lucene.
 *
 * @author Ethan Cerami
 */
public class IndexRules {
    /**
     * Flag Used to indicate record should not be indexed by Lucene.
     */
    public static final int NO_INDEX = -1;

    /**
     * Determines if/how the specified cPath record should be indexed by Lucene.
     * There are three possible return values:
     * <UL>
     * <LI>{@link IndexRules#NO_INDEX}:  Record should not be indexed at all.
     * <LI>{@link XmlAssemblyFactory#XML_FULL}:  Full XML should be indexed.
     * <LI>{@link XmlAssemblyFactory#XML_ABBREV}:  Abbreviated XML should be
     * indexed.
     * @param record CPath Record Object.
     * @return integer value;  see description above for full details.
     */
    public static int indexRecord (CPathRecord record) {
        if (record.getXmlType().equals(XmlRecordType.PSI_MI)) {
            // Rules for PSI-MI
            if (record.getType().equals(CPathRecordType.INTERACTION)) {
                return XmlAssemblyFactory.XML_FULL;
            }
        } else if (record.getXmlType().equals(XmlRecordType.BIO_PAX)) {
            //  Rules for BioPAX
            if (record.getType().equals(CPathRecordType.PATHWAY)
                || record.getType().equals(CPathRecordType.PHYSICAL_ENTITY)) {
                return XmlAssemblyFactory.XML_ABBREV;
            }
        }
        return NO_INDEX;
    }
}
