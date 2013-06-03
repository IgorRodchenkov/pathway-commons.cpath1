package org.mskcc.pathdb.sql.assembly;

import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.XmlRecordType;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Date;
import java.util.Collection;

/**
 * Encapsulates a Complete PSI-MI XML Assembly Document.
 * This document is both well-formed and valid, and contains 1 or more
 * interaction records, along with complete interactor details.
 * This class is identical to PsiAssembly, except that the
 * class generates an XML document string only, and does not generate any Castor
 * objects.  This class was created because code profiling showed that Castor
 * marshalling/unmarshalling of large XML documents is a severe performance bottleneck.
 *
 * @author Ethan Cerami
 */
public class PsiAssemblyStringOnly implements XmlAssembly {
    private ArrayList interactions;
    private XDebug xdebug;
    private String xml;
    private int numHits = 0;
    private static Logger log = Logger.getLogger(PsiAssemblyStringOnly.class);
    private boolean useOptimizedCode;

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssemblyFactory.
     *
     * @param interactions ArrayList of CPathRecord objects.  Each CPathRecord
     *                     contains an Interaction.
     * @param useOptimizedCode use optimized code flag.
     * @param xdebug       XDebug Object.
     * @throws org.mskcc.pathdb.sql.assembly.AssemblyException Error In Assembly.
     */
    PsiAssemblyStringOnly(ArrayList interactions, boolean useOptimizedCode,
            XDebug xdebug)
            throws AssemblyException {
        this.interactions = interactions;
        this.xdebug = xdebug;
        this.useOptimizedCode = useOptimizedCode;
        PsiAssemblyStringOnly.log.info("Building PSI-MI XML Assembly, Mode:  XML String Only");
        try {
            if (interactions == null || interactions.size() == 0) {
                xml = null;
            } else {
                HashMap interactors = PsiAssemblyUtil.extractInteractors
                        (interactions, useOptimizedCode);
                Date start = new Date();
                buildPsi(interactors.values(), interactions);
                Date stop = new Date();
                long timeInterval = stop.getTime() - start.getTime();
                PsiAssemblyStringOnly.log.info
                    ("Total time to create PSI-MI XML assembly:  " + timeInterval
                    + " ms");
            }
        } catch (DaoException e) {
            throw new AssemblyException(e);
        }
    }

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssembly Factory.
     *
     * @param xdebug XDebug Object.
     */
    PsiAssemblyStringOnly(XDebug xdebug) {
        this.xdebug = xdebug;
        this.xml = null;
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
     * @throws org.mskcc.pathdb.sql.assembly.AssemblyException Error Assembling XML Document.
     */
    public String getXmlStringWithCPathIdPrefix() throws AssemblyException {
        throw new NullPointerException
                ("method:  getXmlStringWithCPathIdPrefix() is not supported.");
    }

    /**
     * Gets Complete XML Assembly (in object form).
     *
     * @return Java Object encapsulating XML Document.
     */
    public Object getXmlObject() {
        throw new NullPointerException ("method:  getXmlObject() is not supported.");
    }

    /**
     * Indicates if Assembly is Empty (contains no data).
     *
     * @return true or false.
     */
    public boolean isEmpty() {
        if (interactions == null || interactions.size() == 0) {
            return true;
        } else {
            return false;
        }
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
     */
    private void buildPsi(Collection interactors, Collection interactions) {
        xdebug.logMsg(this, "Concatenating all PSI-MI fragments");
        PsiAssemblerStringOnly psiBuilder = new PsiAssemblerStringOnly();
        xml = psiBuilder.generatePsi(interactors, interactions);
    }
}