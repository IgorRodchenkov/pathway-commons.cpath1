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
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.ArrayList;

/**
 * Factory for instantiating XmlAssembly objects.
 *
 * @author Ethan Cerami.
 */
public class XmlAssemblyFactory {

    /**
     * Creates an XmlAssembly based on specified cPathId.
     *
     * @param cpathId cPathID must refer to an Interaction record.
     * @param xmlType XmlRecordType Object.
     * @param numHits Total Number of Hits.
     * @param xdebug  XDebug Object
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(long cpathId, XmlRecordType
            xmlType, int numHits,
            XDebug xdebug) throws AssemblyException {
        try {
            DaoCPath dao = new DaoCPath();
            CPathRecord record = dao.getRecordById(cpathId);
            return createAssemblyInstance(record, xmlType, numHits, xdebug);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        }
    }

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
            return createAssemblyInstance(record, record.getXmlType(), 
                    numHits, xdebug);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Creates an XmlAssembly based on specified list of cPathIds.
     *
     * @param cpathIds Each cPathID must refer to an Interaction record.
     * @param xmlType XmlRecordType Object.
     * @param numHits  Total Number of Hits.
     * @param xdebug   XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(long cpathIds[], XmlRecordType
            xmlType, int numHits, XDebug xdebug) throws AssemblyException {
        try {
            ArrayList records = new ArrayList();
            DaoCPath dao = new DaoCPath();
            for (int i = 0; i < cpathIds.length; i++) {
                CPathRecord record = dao.getRecordById(cpathIds[i]);
                records.add(record);
            }
            return createAssemblyInstance(records, xmlType, numHits, xdebug);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Creates an XmlAssembly based on specified cPathRecord.
     *
     * @param record      CPathRecord Object.
     * @param xmlType     XmlRecordType Object.
     * @param numHits     Total Number of Hits.
     * @param xdebug      XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(CPathRecord record,
            XmlRecordType xmlType, int numHits, XDebug xdebug)
            throws AssemblyException {
        return createAssemblyInstance(record, xmlType, numHits, xdebug);
    }

    /**
     * Creates an XmlAssembly based on specified list of cPathRecords.
     *
     * @param recordList   An ArrayList of CPathRecord Objects.
     * @param xmlType      XmlRecordType Object.
     * @param numHits      Total Number of Hits.
     * @param xdebug       XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(ArrayList recordList,
            XmlRecordType xmlType, int numHits, XDebug xdebug)
            throws AssemblyException {
        for (int i = 0; i < recordList.size(); i++) {
            Object object = recordList.get(i);
            if (!(object instanceof CPathRecord)) {
                throw new IllegalArgumentException("ArrayList must "
                        + "contain objects of type:  "
                        + CPathRecord.class.getName());
            } else {
                CPathRecord record = (CPathRecord) object;
            }
        }
        return createAssemblyInstance(recordList, xmlType, numHits, xdebug);
    }

    /**
     * Creates an XMLAssembly Object from the specified XML Document.
     *
     * @param xmlDocumentComplete Complete XML Document.
     * @param xmlType             XML Record Type.
     * @param numHits             Total Number of Hits.
     * @param xdebug              XDebug Object.
     * @return XmlAssemblyObject.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(String xmlDocumentComplete,
            XmlRecordType xmlType, int numHits, XDebug xdebug)
            throws AssemblyException {
        XmlAssembly xmlAssembly = null;
        if (xmlType.equals(XmlRecordType.PSI_MI)) {
            xmlAssembly = new PsiAssembly(xmlDocumentComplete, xdebug);
        } else {
            xmlAssembly = new BioPaxAssembly (xmlDocumentComplete, xdebug);
        }
        xmlAssembly.setNumHits(numHits);
        return xmlAssembly;
    }

    /**
     * Creates an XMLAssembly Object from the specified XML Document.
     *
     * @param xdebug XDebug Object.
     * @return XmlAssemblyObject.
     */
    public static XmlAssembly createEmptyXmlAssembly(XDebug xdebug) {
        XmlAssembly xmlAssembly = new PsiAssembly(xdebug);
        return xmlAssembly;
    }

    /**
     * Creates an Instance of the XmlAssembly interface.
     *
     * @param record CPathRecord Object.
     * @param xmlType XmlRecordType Object.
     * @return XmlAssembly Object.
     * @throws AssemblyException Error in Assembly.
     */
    private static XmlAssembly createAssemblyInstance(CPathRecord record,
            XmlRecordType xmlType, int numHits, XDebug xdebug)
            throws AssemblyException {
        ArrayList records = new ArrayList();
        records.add(record);
        return createAssemblyInstance(records, xmlType, numHits, xdebug);
    }

    /**
     * Creates an Instance of the XmlAssembly interface.
     *
     * @param recordList ArrayList of CPathRecord Objects.
     * @param xmlType XmlRecordType Object.
     * @return XmlAssembly Object.
     * @throws AssemblyException Error in Assembly.
     */
    private static XmlAssembly createAssemblyInstance(ArrayList recordList,
            XmlRecordType xmlType, int numHits, XDebug xdebug)
            throws AssemblyException {
        xdebug.logMsg(XmlAssemblyFactory.class,
                "Creating XML Assembly of Type:  " + xmlType);

        //  Filter for all records of same type
        //  Just in case we have a grab-bag of BioPAX and PSI-MI Records.
        recordList = filterRecords (xmlType, recordList);

        //  Instantiate Correct XML Assembly based on XML Type
        XmlAssembly xmlAssembly = null;
        if (xmlType.equals(XmlRecordType.PSI_MI)) {
            xmlAssembly = new PsiAssembly(recordList, xdebug);
        } else {
            xmlAssembly = new BioPaxAssembly (recordList, xdebug);
        }
        xmlAssembly.setNumHits(numHits);
        return xmlAssembly;
    }

    /**
     * Iterate through all cPath Records and retain only those that match
     * the specified XmlRecordType.
     */
    private static ArrayList filterRecords (XmlRecordType type,
            ArrayList recordList) {
        ArrayList filteredList = new ArrayList();
        for (int i = 0; i < recordList.size(); i++) {
            CPathRecord record =  (CPathRecord) recordList.get(i);
            if (record.getXmlType().equals(type)) {
                filteredList.add(record);
            }
        }
        return filteredList;
    }
}