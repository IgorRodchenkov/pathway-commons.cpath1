// $Id: PsiAssembly.java,v 1.20 2007-04-16 19:20:20 cerami Exp $
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

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.Entry;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.InteractionList;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.pathdb.xdebug.XDebug;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Date;

/**
 * Encapsulates a Complete PSI-MI XML Assembly Document.
 * This document is both well-formed and valid, and contains 1 or more
 * interaction records, along with complete interactor details.
 *
 * @author Ethan Cerami
 */
public class PsiAssembly implements XmlAssembly {
    private XDebug xdebug;
    private String xml;
    private EntrySet entrySet;
    private int numHits = 0;
    private ArrayList interactions = null;
    private static Logger log = Logger.getLogger(PsiAssembly.class);
    private boolean useOptimizedCode;

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssemblyFactory.
     *
     * @param interactions ArrayList of CPathRecord objects.  Each CPathRecord
     *                     contains an Interaction.
     * @param useOptimizedCode use optimized code flag.
     * @param xdebug       XDebug Object.
     * @throws AssemblyException Error In Assembly.
     */
    PsiAssembly(ArrayList interactions, boolean useOptimizedCode,
            XDebug xdebug)
            throws AssemblyException {
        this.interactions = interactions;
        this.xdebug = xdebug;
        this.useOptimizedCode = useOptimizedCode;
        log.info("Building PSI-MI XML Assembly, Mode:  Use Castor");
        try {
            if (interactions == null || interactions.size() == 0) {
                entrySet = null;
            } else {
                HashMap interactors = PsiAssemblyUtil.extractInteractors
                        (interactions, useOptimizedCode);
                Date start = new Date();
                buildPsi(interactors.values(), interactions);
                Date stop = new Date();
                long timeInterval = stop.getTime() - start.getTime();
                log.info("Total time to create PSI-MI XML assembly:  " + timeInterval
                    + " ms");
            }
        } catch (IOException e) {
            throw new AssemblyException(e);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        } catch (MarshalException e) {
            throw new AssemblyException(e);
        } catch (ValidationException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssembly Factory.
     *
     * @param xmlDocumentComplete Complete XML Document.
     * @param xdebug              XDebug Object.
     * @throws AssemblyException Error In Assembly.
     */
    PsiAssembly(String xmlDocumentComplete, XDebug xdebug)
            throws AssemblyException {
        this.xml = xmlDocumentComplete;
        this.xdebug = xdebug;
        if (xml != null) {
            StringReader xmlReader = new StringReader(xml);
            try {
                this.entrySet = EntrySet.unmarshalEntrySet(xmlReader);
            } catch (MarshalException e) {
                throw new AssemblyException(e);
            } catch (ValidationException e) {
                throw new AssemblyException(e);
            }
        }
    }

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssembly Factory.
     *
     * @param xdebug XDebug Object.
     */
    PsiAssembly(XDebug xdebug) {
        this.xdebug = xdebug;
        this.xml = null;
        this.entrySet = null;
    }

    /**
     * Gets Comlete Xml Assembly (in String form).
     *
     * @return XML Document String.
     */
    public String getXmlString() {
        return xml;
    }

    /**
     * Gets XML Record Type.
     *
     * @return XmlRecordType.PSI_MI
     */
    public XmlRecordType getXmlType() {
        return XmlRecordType.PSI_MI;
    }

    /**
     * Gets Comlete Xml Assembly (in String form).
     * All Internal IDs are converted from the form: 1234 to CPATH-1234.
     * See org.mskcc.pathdb.sql.assembly.CPathIdFilter for more details.
     * <p/>
     * Note that calling this method has an intentional side effect of
     * modifying the embedded EntrySet object.  For example, if you call
     * getXmlStringWithCPathIdPrefix(), and then you call getXmlObject(),
     * the returned EntrySet object will now have IDs of the form, "CPATH-123".
     *
     * @return XML Document String.
     * @throws AssemblyException Error Assembling XML Document.
     */
    public String getXmlStringWithCPathIdPrefix() throws AssemblyException {
        if (entrySet != null) {
            entrySet = CPathIdFilter.addCPathIdPrefix(entrySet);
            try {
                this.xml = generateXml(entrySet);
            } catch (MarshalException e) {
                throw new AssemblyException(e);
            } catch (ValidationException e) {
                throw new AssemblyException(e);
            } catch (IOException e) {
                throw new AssemblyException(e);
            }
        }
        return this.xml;
    }

    /**
     * Gets Complete XML Assembly (in object form).
     *
     * @return Java Object encapsulating XML Document.
     */
    public Object getXmlObject() {
        return entrySet;
    }

    /**
     * Indicates if Assembly is Empty (contains no data).
     *
     * @return true or false.
     */
    public boolean isEmpty() {
        boolean emptyFlag = true;
        if (entrySet != null) {
            if (entrySet.getEntryCount() > 0) {
                Entry entry = entrySet.getEntry(0);
                InteractionList list = entry.getInteractionList();
                if (list.getInteractionCount() > 0) {
                    emptyFlag = false;
                }
            }
        }
        return emptyFlag;
    }

    /**
     * Gets Total Number of Records.
     * This Assembly may be a subset of a larger set.
     * This method returns the total number of records in the larger,
     * complete set.
     *
     * @return int number of records.
     */
    public int getNumHits() {
        return this.numHits;
    }

    /**
     * Sets Total Number of Records.
     * This Assembly may be a subset of a larger set.
     * This method returns the total number of records in the larger,
     * complete set.
     *
     * @param numRecords Total Number of Records.
     */
    public void setNumHits(int numRecords) {
        this.numHits = numRecords;
    }



    /**
     * Builds PSI XML Document.
     *
     * @param interactors  ArrayList of Interactors.
     * @param interactions ArrayList of Interactions.
     * @throws ValidationException Document is not valid.
     * @throws MarshalException    Could not Marshal Document to XML.
     */
    private void buildPsi(Collection interactors, Collection interactions)
            throws ValidationException, MarshalException, IOException {
        xdebug.logMsg(this, "Creating Final PSI-MI XML Document");
        PsiAssembler psiBuilder = new PsiAssembler();
        entrySet = psiBuilder.generatePsi(interactors, interactions);
        xml = generateXml(entrySet);
    }

    /**
     * Generates XML from Entry Set Object.
     */
    private String generateXml(EntrySet set) throws ValidationException,
            MarshalException, IOException {
        PropertyManager pManager = PropertyManager.getInstance();
        String psiSchemaUrl = (String) pManager.get
                (CPathConstants.PROPERTY_PSI_SCHEMA_LOCATION);
        StringWriter writer = new StringWriter();
        Marshaller marshaller = new Marshaller(writer);
        marshaller.setSchemaLocation("net:sf:psidev:mi " + psiSchemaUrl);
        marshaller.marshal(set);
        return writer.toString();
    }
}
