package org.mskcc.pathdb.sql.assembly;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.Marshaller;
import org.mskcc.dataservices.schemas.psi.Entry;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.dataservices.schemas.psi.InteractionList;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.util.CPathConstants;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

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

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssemblyFactory.
     *
     * @param interactions ArrayList of CPathRecord objects.  Each CPathRecord
     *                     contains an Interaction.
     * @param xdebug       XDebug Object.
     * @throws AssemblyException Error In Assembly.
     */
    PsiAssembly(ArrayList interactions, XDebug xdebug)
            throws AssemblyException {
        this.interactions = interactions;
        this.xdebug = xdebug;
        try {
            HashMap interactors = extractInteractors(interactions);
            buildPsi(interactors.values(), interactions);
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
     * @throws AssemblyException Error In Assembly.
     */
    PsiAssembly(XDebug xdebug) throws AssemblyException {
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
     * Gets Complete XML Assembly (in object form).
     *
     * @return Java Object encapsulating XML Document.
     */
    public Object getXmlObject() {
        return entrySet;
    }

    /**
     * Indicates is Assembly is Empty (contains no data).
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
     * Given a List of Interaction Records, retrieve all associated
     * interactors.
     *
     * @param interactions ArrayList of CPathRecord Objects containing
     *                     Interactions.
     * @return HashMap of All Interactors, indexed by cpath ID.
     * @throws DaoException Data Access Error.
     */
    protected HashMap extractInteractors(ArrayList interactions)
            throws DaoException {
        HashMap interactorMap = new HashMap();
        DaoInternalLink linker = new DaoInternalLink();
        DaoCPath cpath = new DaoCPath();
        for (int i = 0; i < interactions.size(); i++) {
            CPathRecord record = (CPathRecord) interactions.get(i);
            ArrayList list = linker.getInternalLinks(record.getId());
            for (int j = 0; j < list.size(); j++) {
                InternalLinkRecord link = (InternalLinkRecord) list.get(j);
                Long key = new Long(link.getCpathIdB());
                if (!interactorMap.containsKey(key)) {
                    CPathRecord interactor = cpath.getRecordById
                            (link.getCpathIdB());
                    interactorMap.put(key, interactor);
                }
            }
        }
        xdebug.logMsg(this, "Total number of Linked Interactors found:  "
                + interactorMap.size());
        return interactorMap;
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
        marshaller.setSchemaLocation("net:sf:psidev:mi "+ psiSchemaUrl);
        marshaller.marshal(set);
        return writer.toString();
    }
}