// $Id: UpdatePsiInteractor.java,v 1.17 2006-02-22 22:47:51 grossb Exp $
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
package org.mskcc.pathdb.sql.transfer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.schemas.psi.PsiUtil;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDbCv;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Updates PSI Interactors.
 * For full details see base class:  UpdateInteractor.
 *
 * @author Ethan Cerami
 */
public class UpdatePsiInteractor extends UpdateInteractor {
    /**
     * Protein as is Currently Exists in the Database.
     */
    private ProteinInteractorType existingProtein;

    private ProgressMonitor pMonitor;

    /**
     * Constructor.
     *
     * @param newProtein new protein Record, scheduled for import.
     * @param pMonitor   ProgressMonitor Object.
     * @throws DaoException         Error Adding new data to database.
     * @throws ValidationException  Invalid XML
     * @throws MarshalException     Error Marshaling to XML.
     * @throws MissingDataException XML is missing data.
     */
    public UpdatePsiInteractor(ProteinInteractorType newProtein,
            ProgressMonitor pMonitor)
            throws DaoException, ValidationException, MarshalException,
            MissingDataException {
        this.pMonitor = pMonitor;
        //  Normalize XRefs
        PsiUtil psiUtil = new PsiUtil(pMonitor);
        psiUtil.normalizeXrefs(newProtein.getXref());

        //  Find a Match to Existing Interactor.
        ExternalReference newRefs[] = psiUtil.extractRefs(newProtein);

        DaoExternalLink linker = DaoExternalLink.getInstance();
        ArrayList records = linker.lookUpByExternalRefs(newRefs);
        if (records.size() > 0) {
            CPathRecord record = (CPathRecord) records.get(0);
            extractExistingRefs(record);
        } else {
            throw new IllegalArgumentException("No matching interactor "
                    + "found for protein:  " + newProtein.getId());
        }
        this.setNewExternalRefs(newRefs);
    }

    /**
     * Constructor.
     *
     * @param existingProtein Existing Protein, in database.
     * @param newProtein      new protein Record, scheduled for import.
     * @param pMonitor        ProgressMonitor Object.
     * @throws ValidationException  Invalid XML
     * @throws MarshalException     Error Marshaling to XML.
     * @throws MissingDataException XML is missing data.*
     */
    public UpdatePsiInteractor(CPathRecord existingProtein,
            ProteinInteractorType newProtein, ProgressMonitor pMonitor)
            throws ValidationException,
            MarshalException, MissingDataException {
        this.pMonitor = pMonitor;
        PsiUtil psiUtil = new PsiUtil(pMonitor);
        ExternalReference newRefs[] = psiUtil.extractRefs(newProtein);
        extractExistingRefs(existingProtein);
        this.setNewExternalRefs(newRefs);
    }

    /**
     * Constructor.
     *
     * @param newRef            New Reference to Add.
     * @param existingRef       Existing Reference.
     * @param refsAreNormalized References have already been normalized.
     * @param pMonitor          ProgressMonitor Object.
     * @throws DaoException         Error Adding new data to database.
     * @throws MissingDataException XML is missing data.
     */
    public UpdatePsiInteractor(ExternalReference existingRef,
            ExternalReference newRef, boolean refsAreNormalized,
            ProgressMonitor pMonitor)
            throws DaoException, MissingDataException {
        this.pMonitor = pMonitor;
        ExternalReference newRefs[] = new ExternalReference[2];
        if (!refsAreNormalized) {
            normalizeExternalRef(newRef);
            normalizeExternalRef(existingRef);
        }
        newRefs[0] = existingRef;
        newRefs[1] = newRef;

        //  Find  Match to Existing Interactor (if possible).
        DaoExternalLink linker = DaoExternalLink.getInstance();
        ArrayList records1 = linker.lookUpByExternalRef(existingRef);
        try {
            if (records1.size() > 0) {
                CPathRecord record = (CPathRecord) records1.get(0);
                extractExistingRefs(record);
            }
            setNewExternalRefs(newRefs);
        } catch (MarshalException e) {
            throw new DaoException(e);
        } catch (ValidationException e) {
            throw new DaoException(e);
        }
    }

    private void normalizeExternalRef(ExternalReference ref)
            throws DaoException {
        DaoExternalDbCv dao = new DaoExternalDbCv();
        String newDb = dao.getFixedCvTerm(ref.getDatabase());
        ref.setDatabase(newDb);
    }

    /**
     * Based on Matching CPath Record, define set of existing external refs.
     *
     * @param record CPath Record.
     * @throws ValidationException Invalid XML.
     * @throws MarshalException    Error Marshaling to XML.
     */
    private void extractExistingRefs(CPathRecord record)
            throws MarshalException, ValidationException, MissingDataException {
        PsiUtil psiUtil = new PsiUtil(pMonitor);
        String xml = record.getXmlContent();
        StringReader reader = new StringReader(xml);
        existingProtein = ProteinInteractorType.
                unmarshalProteinInteractorType(reader);
        ExternalReference existingRefs[] = psiUtil.extractRefs
                (existingProtein);
        this.setExistingExternalRefs(existingRefs);
        this.setcPathId(record.getId());
    }
}
