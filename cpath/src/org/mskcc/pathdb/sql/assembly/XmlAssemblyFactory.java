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

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.ArrayList;

/**
 * Factory for instantiaing XmlAssembly objects.
 * This class always returns PsiAssembly objects.  However, in the future,
 * it may support other types of assemblies, such as a BioPax Assembly.
 *
 * @author Ethan Cerami.
 */
public class XmlAssemblyFactory {

    /**
     * Creates an XmlAssembly based on specified cPathId.
     *
     * @param cpathId cPathID must refer to an Interaction record.
     * @param numHits Total Number of Hits.
     * @param xdebug  XDebug Object
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(long cpathId, int numHits,
            XDebug xdebug) throws AssemblyException {
        try {
            DaoCPath dao = new DaoCPath();
            CPathRecord record = dao.getRecordById(cpathId);
            checkRecordType(record);
            return createAssemblyInstance(record, numHits, xdebug);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Creates an XmlAssembly based on specified list of cPathIds.
     *
     * @param cpathIds Each cPathID must refer to an Interaction record.
     * @param numHits  Total Number of Hits.
     * @param xdebug   XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(long cpathIds[], int numHits,
            XDebug xdebug) throws AssemblyException {
        try {
            ArrayList records = new ArrayList();
            DaoCPath dao = new DaoCPath();
            for (int i = 0; i < cpathIds.length; i++) {
                CPathRecord record = dao.getRecordById(cpathIds[i]);
                checkRecordType(record);
                records.add(record);
            }
            return createAssemblyInstance(records, numHits, xdebug);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Creates an XmlAssembly based on specified cPathRecord.
     *
     * @param interaction CPathRecord must contain an Interaction.
     * @param numHits     Total Number of Hits.
     * @param xdebug      XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(CPathRecord interaction,
            int numHits, XDebug xdebug) throws AssemblyException {
        checkRecordType(interaction);
        return createAssemblyInstance(interaction, numHits, xdebug);
    }

    /**
     * Creates an XmlAssembly based on specified list of cPathRecords.
     *
     * @param interactions An ArrayList of CPathRecord Objects.
     *                     Each CPathRecord object must contain an Interaction.
     * @param numHits      Total Number of Hits.
     * @param xdebug       XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(ArrayList interactions,
            int numHits, XDebug xdebug) throws AssemblyException {
        for (int i = 0; i < interactions.size(); i++) {
            Object object = interactions.get(i);
            if (!(object instanceof CPathRecord)) {
                throw new IllegalArgumentException("ArrayList must "
                        + "contain objects of type:  "
                        + CPathRecord.class.getName());
            } else {
                CPathRecord record = (CPathRecord) object;
                checkRecordType(record);
            }
        }
        return createAssemblyInstance(interactions, numHits, xdebug);
    }

    /**
     * Creates an XMLAssembly Object from the specified XML Document.
     *
     * @param xmlDocumentComplete Complete XML Document.
     * @param numHits             Total Number of Hits.
     * @param xdebug              XDebug Object.
     * @return XmlAssemblyObject.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(String xmlDocumentComplete,
            int numHits, XDebug xdebug) throws AssemblyException {
        XmlAssembly xmlAssembly = new PsiAssembly(xmlDocumentComplete, xdebug);
        xmlAssembly.setNumHits(numHits);
        return xmlAssembly;
    }

    /**
     * Creates an XMLAssembly Object from the specified XML Document.
     *
     * @param xdebug XDebug Object.
     * @return XmlAssemblyObject.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createEmptyXmlAssembly(XDebug xdebug)
            throws AssemblyException {
        XmlAssembly xmlAssembly = new PsiAssembly(xdebug);
        return xmlAssembly;
    }

    /**
     * Creates an Instance of the XmlAssembly interface.
     *
     * @param interaction CPathRecord Interaction Object.
     * @return XmlAssembly Object.
     * @throws AssemblyException Error in Assembly.
     */
    private static XmlAssembly createAssemblyInstance(CPathRecord interaction,
            int numHits, XDebug xdebug) throws AssemblyException {
        ArrayList records = new ArrayList();
        records.add(interaction);
        return createAssemblyInstance(records, numHits, xdebug);
    }

    /**
     * Creates an Instance of the XmlAssembly interface.
     *
     * @param interactions ArrayList of CPathRecord Objects.
     * @return XmlAssembly Object.
     * @throws AssemblyException Error in Assembly.
     */
    private static XmlAssembly createAssemblyInstance(ArrayList interactions,
            int numHits, XDebug xdebug) throws AssemblyException {
        XmlAssembly xmlAssembly = new PsiAssembly(interactions, xdebug);
        xmlAssembly.setNumHits(numHits);
        return xmlAssembly;
    }

    /**
     * Confirms that this is an Interaction Record.
     * If this is not an interaction record, an IllegalArgumentException
     * is thrown.
     *
     * @param record CPathRecord.
     */
    private static void checkRecordType(CPathRecord record) {
        if (!record.getType().equals(CPathRecordType.INTERACTION)) {
            throwIllegalArgument(record);
        }
    }

    /**
     * Throws an IllegalArgumentException with detailed error message.
     *
     * @param record CPathRecord.
     */
    private static void throwIllegalArgument(CPathRecord record) {
        throw new IllegalArgumentException("The specified cpathId:  "
                + record.getId() + " points to a record of type:  "
                + record.getType()
                + ".  It must point to a record of type:  "
                + CPathRecordType.INTERACTION.toString()
                + ".");
    }
}