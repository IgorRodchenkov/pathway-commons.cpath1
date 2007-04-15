// $Id: XmlAssemblyFactory.java,v 1.17 2007-04-15 20:26:43 cerami Exp $
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

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.xdebug.XDebug;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;

/**
 * Factory for instantiating XmlAssembly objects.
 *
 * @author Ethan Cerami.
 */
public class XmlAssemblyFactory {
    private static Logger log = Logger.getLogger(XmlAssemblyFactory.class);

    /**
     * In XML_ABBREV mode, the root XML document is retrieved, but linked
     * resources are not retrieved.  For example, if you retrieve an
     * interaction in XML_ABBREV, you get back XML for the interaction,
     * but no XML for its protein participants.
     */
    public static final int XML_ABBREV = 0;

    /**
     * In XML_FULL mode, the root XML document is retrieved, as are all linked
     * resources.  For example, if you retrieve an ineraction in XML_FULL,
     * you get back XML for the interaction, plus XML for all its protein
     * participants.
     */
    public static final int XML_FULL = 1;

    /**
     * Same as XML_FULL mode, except only the XML document string is generated.
     * No JDOM elements or Castor elements are created.
     */
    public static final int XML_FULL_STRING_ONLY = 2;


    /**
     * Creates an XmlAssembly based on specified cPathId.
     *
     * @param cpathId cPathID must refer to an Interaction record.
     * @param numHits Total Number of Hits.
     * @param mode    XML_ABBREV or XML_FULL.
     * @param xdebug  XDebug Object
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(long cpathId, int numHits,
            int mode, XDebug xdebug) throws AssemblyException {
        validateModeParameter(mode);
        try {
            DaoCPath dao = DaoCPath.getInstance();
            CPathRecord record = dao.getRecordById(cpathId);
            return createAssemblyInstance(record, record.getXmlType(),
                    numHits, mode, xdebug);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Creates an XmlAssembly based on specified list of cPathIds.
     *
     * @param cpathIds Each cPathID must refer to an Interaction record.
     * @param xmlType  XmlRecordType Object.
     * @param numHits  Total Number of Hits.
     * @param mode     XML_ABBREV or XML_FULL.
     * @param xdebug   XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(long cpathIds[], XmlRecordType
            xmlType, int numHits, int mode, XDebug xdebug)
            throws AssemblyException {
        validateModeParameter(mode);
        Date start = new Date();
        try {
            log.info("Retrieving a total of " + cpathIds.length + " matching cPath records");
            ArrayList records = new ArrayList();
            DaoCPath dao = DaoCPath.getInstance();
            for (int i = 0; i < cpathIds.length; i++) {
                CPathRecord record = dao.getRecordById(cpathIds[i]);
                records.add(record);
            }
            Date stop = new Date();
            long timeInterval = stop.getTime() - start.getTime();
            log.info("Total time to retrieve all matching cPath records:  " + timeInterval
                + " ms");
            return createAssemblyInstance(records, xmlType, numHits, mode,
                    xdebug);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Creates an XmlAssembly based on specified cPathRecord.
     *
     * @param record  CPathRecord Object.
     * @param numHits Total Number of Hits.
     * @param mode    Mode must be one of XML_ABBREV, XML_FULL.
     * @param xdebug  XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(CPathRecord record,
            int numHits, int mode, XDebug xdebug)
            throws AssemblyException {
        validateModeParameter(mode);
        return createAssemblyInstance(record, record.getXmlType(),
                numHits, mode, xdebug);
    }

    /**
     * Creates an XmlAssembly based on specified list of cPathRecords.
     *
     * @param recordList An ArrayList of CPathRecord Objects.
     * @param xmlType    XmlRecordType Object.
     * @param numHits    Total Number of Hits.
     * @param mode       Mode must be one of XML_ABBREV, XML_FULL.
     * @param xdebug     XDebug Object.
     * @return XmlAssembly object.
     * @throws AssemblyException Error in Assembly.
     */
    public static XmlAssembly createXmlAssembly(ArrayList recordList,
            XmlRecordType xmlType, int numHits, int mode, XDebug xdebug)
            throws AssemblyException {
        for (int i = 0; i < recordList.size(); i++) {
            Object object = recordList.get(i);
            if (!(object instanceof CPathRecord)) {
                throw new IllegalArgumentException("ArrayList must "
                        + "contain objects of type:  "
                        + CPathRecord.class.getName());
            }
        }
        return createAssemblyInstance(recordList, xmlType, numHits, mode,
                xdebug);
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
            xmlAssembly = new BioPaxAssembly(xmlDocumentComplete, xdebug);
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
     */
    private static XmlAssembly createAssemblyInstance(CPathRecord record,
            XmlRecordType xmlType, int numHits, int mode, XDebug xdebug)
            throws AssemblyException {
        ArrayList records = new ArrayList();
        records.add(record);
        return createAssemblyInstance(records, xmlType, numHits, mode, xdebug);
    }

    /**
     * Creates an Instance of the XmlAssembly interface.
     */
    private static XmlAssembly createAssemblyInstance(ArrayList recordList,
            XmlRecordType xmlType, int numHits, int mode, XDebug xdebug)
            throws AssemblyException {
        xdebug.logMsg(XmlAssemblyFactory.class,
                "Creating XML Assembly of Type:  " + xmlType);

        //  Filter for all records of same type
        //  Just in case we have a grab-bag of BioPAX and PSI-MI Records.
        recordList = filterRecords(xmlType, recordList);

        //  Instantiate Correct XML Assembly based on XML Type
        XmlAssembly xmlAssembly = null;

        //  Branch here, based on XML type and mode
        if (xmlType.equals(XmlRecordType.PSI_MI)) {
            if (mode == XmlAssemblyFactory.XML_FULL) {
                xmlAssembly = new PsiAssembly(recordList, xdebug);
            } else {
                xmlAssembly = new PsiAssemblyStringOnly(recordList, xdebug);
            }
        } else {
            xmlAssembly = new BioPaxAssembly(recordList, mode, xdebug);
        }
        xmlAssembly.setNumHits(numHits);
        return xmlAssembly;
    }

    /**
     * Iterate through all cPath Records and retain only those that match
     * the specified XmlRecordType.
     */
    private static ArrayList filterRecords(XmlRecordType type,
            ArrayList recordList) {
        ArrayList filteredList = new ArrayList();
        for (int i = 0; i < recordList.size(); i++) {
            CPathRecord record = (CPathRecord) recordList.get(i);
            if (record.getXmlType().equals(type)) {
                filteredList.add(record);
            }
        }
        return filteredList;
    }

    private static void validateModeParameter(int mode) {
        if (mode != XML_ABBREV && mode != XML_FULL
                && mode != XML_FULL_STRING_ONLY) {
            throw new IllegalArgumentException("mode parameter must be set"
                    + "to XML_ABBREV or XML_FULL.");
        }
    }
}
