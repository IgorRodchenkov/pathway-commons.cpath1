package org.mskcc.pathdb.sql.transfer;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.DbReferenceType;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.dataservices.schemas.psi.XrefType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.util.PsiUtil;

import java.io.StringReader;
import java.io.StringWriter;
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

    /**
     * Constructor.
     * @param newProtein new protein Record, scheduled for import.
     * @throws DaoException Error Adding new data to database.
     * @throws ValidationException Invalid XML
     * @throws MarshalException Error Marshaling to XML.
     */
    public UpdatePsiInteractor(ProteinInteractorType newProtein)
            throws DaoException, ValidationException, MarshalException {
        PsiUtil psiUtil = new PsiUtil();

        //  Find a Match to Existing Interactor.
        ExternalReference newRefs[] = psiUtil.extractRefs(newProtein);
        DaoExternalLink linker = new DaoExternalLink();
        CPathRecord record = linker.lookUpByExternalRefs(newRefs);
        if (record != null) {
            String xml = record.getXmlContent();
            StringReader reader = new StringReader(xml);
            existingProtein = ProteinInteractorType.
                    unmarshalProteinInteractorType(reader);
            ExternalReference existingRefs[] = psiUtil.extractRefs
                    (existingProtein);
            this.setExistingExternalRefs(existingRefs);
            this.setcPathId(record.getId());
        } else {
            throw new IllegalArgumentException("No matching interactor "
                    + "found for protein:  " + newProtein.getId());
        }
        this.setNewExternalRefs(newRefs);
    }

    /**
     * Updates the XML Stored in the existing Interactor to include all new
     * external references.
     * This method is specific to PSI.
     * @param newList ArrayList of External Reference Objects.
     */
    protected void updateInteractorXml(ArrayList newList)
            throws ValidationException, MarshalException, DaoException {
        XrefType xref = existingProtein.getXref();
        for (int i = 0; i < newList.size(); i++) {
            DbReferenceType secondaryRef = new DbReferenceType();
            ExternalReference ref = (ExternalReference) newList.get(i);
            secondaryRef.setDb(ref.getDatabase());
            secondaryRef.setId(ref.getId());
            xref.addSecondaryRef(secondaryRef);
        }
        StringWriter writer = new StringWriter();
        existingProtein.marshal(writer);
        DaoCPath cpath = new DaoCPath();
        cpath.updateXml(this.getcPathId(), writer.toString());
    }
}