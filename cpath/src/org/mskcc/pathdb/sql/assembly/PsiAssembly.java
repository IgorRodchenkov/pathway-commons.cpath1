package org.mskcc.pathdb.sql.assembly;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.StringWriter;
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

    /**
     * Package Only Constructor.  Class must be instantiated via the
     * XmlAssemblyFactory.
     *
     * @param interactions ArrayList of CPathRecord objects.  Each CPathRecord
     *                     contains an Interaction.
     * @throws AssemblyException Error In Assembly.
     */
    PsiAssembly(ArrayList interactions, XDebug xdebug)
            throws AssemblyException {
        this.xdebug = xdebug;
        try {
            HashMap interactors = extractInteractors(interactions);
            buildPsi(interactors.values(), interactions);
        } catch (DaoException e) {
            throw new AssemblyException(e);
        } catch (MarshalException e) {
            throw new AssemblyException(e);
        } catch (ValidationException e) {
            throw new AssemblyException(e);
        }
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
     * @return Java Object encapsulating XML Document.
     */
    public Object getXmlObject () {
        return entrySet;
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
            throws ValidationException, MarshalException {
        xdebug.logMsg(this, "Creating Final PSI-MI XML Document");
        PsiAssembler psiBuilder = new PsiAssembler();
        entrySet = psiBuilder.generatePsi(interactors, interactions);
        xml = generateXml(entrySet);
    }

    /**
     * Generates XML from Entry Set Object.
     */
    private String generateXml(EntrySet set) throws ValidationException,
            MarshalException {
        StringWriter writer = new StringWriter();
        set.marshal(writer);
        String xml = writer.toString();
        return xml;
    }
}