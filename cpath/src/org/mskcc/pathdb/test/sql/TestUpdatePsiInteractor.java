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
package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.DbReferenceType;
import org.mskcc.dataservices.schemas.psi.NamesType;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.dataservices.schemas.psi.XrefType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.schemas.psi.PsiUtil;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.transfer.UpdatePsiInteractor;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.StringWriter;
import java.util.ArrayList;

/**
 * Tests the Update Interactor class.
 *
 * @author Ethan Cerami
 */
public class TestUpdatePsiInteractor extends TestCase {
    private static final String NAME = "Protein_A";
    private static final String DESCRIPTION = "DNA Repair Protein A";

    /**
     * Tests the doUpdate() method.
     *
     * @throws Exception All Exceptions.
     */
    public void testUpdate() throws Exception {
        ProteinInteractorType proteinA = createProtein(NAME, DESCRIPTION,
                "SWP", "ABC123", "PIR", "XYZ123");
        ProteinInteractorType proteinB = createProtein(NAME, DESCRIPTION,
                "SWP", "ABC123", "LocusLink", "LOCUS123");
        DaoCPath cpath = new DaoCPath();
        PsiUtil util = new PsiUtil(new ProgressMonitor());
        util.normalizeXrefs(proteinA.getXref());
        ExternalReference refsA[] = util.extractRefs(proteinA);
        StringWriter writer = new StringWriter();
        proteinA.marshal(writer);
        long cpathId = cpath.addRecord(NAME, DESCRIPTION, 25,
                CPathRecordType.PHYSICAL_ENTITY, BioPaxConstants.PROTEIN,
                XmlRecordType.PSI_MI, writer.toString(), refsA);
        UpdatePsiInteractor updater = new UpdatePsiInteractor(proteinB,
                new ProgressMonitor());
        boolean needsUpdating = updater.needsUpdating();
        assertEquals(true, needsUpdating);
        updater.doUpdate();
        validateUpdate(cpathId);
    }

    /**
     * Validates that the Interactor record now contains all external
     * references defined by the union of proteinA and proteinB.
     */
    private void validateUpdate(long id) throws DaoException {
        DaoCPath cpath = new DaoCPath();
        CPathRecord record = cpath.getRecordById(id);
        String xml = record.getXmlContent();
        //  Note that SWP has been normalized to UNIPROT
        int index0 = xml.indexOf
                ("<primaryRef db=\"UNIPROT\" id=\"ABC123\"/>");
        //  Note that original link is still there
        int index1 = xml.indexOf("<secondaryRef db=\"PIR\" id=\"XYZ123\"/>");
        //  Note that new link is now there
        int index2 = xml.indexOf("<secondaryRef db=\"ENTREZ_GENE\" "
                + "id=\"LOCUS123\"/>");
        assertTrue(index0 > 0);
        assertTrue(index1 > 0);
        assertTrue(index2 > 0);
        DaoExternalLink linker = new DaoExternalLink();
        ArrayList list = linker.getRecordsByCPathId(id);
        assertEquals(3, list.size());
        ExternalLinkRecord record0 = (ExternalLinkRecord) list.get(0);
        ExternalLinkRecord record1 = (ExternalLinkRecord) list.get(1);
        ExternalLinkRecord record2 = (ExternalLinkRecord) list.get(2);
        String id0 = record0.getLinkedToId();
        String id1 = record1.getLinkedToId();
        String id2 = record2.getLinkedToId();
        assertEquals("ABC123", id0);
        assertEquals("XYZ123", id1);
        assertEquals("LOCUS123", id2);
    }

    /**
     * Programmatically Create a new PSI Protein Interactor.
     */
    private ProteinInteractorType createProtein(String shortName,
            String fullName, String primaryDb, String primaryId,
            String secondaryDb, String secondaryId) {
        ProteinInteractorType protein = new ProteinInteractorType();
        NamesType names = new NamesType();
        names.setShortLabel(shortName);
        names.setFullName(fullName);
        protein.setNames(names);
        protein.setId(NAME);

        XrefType xref = new XrefType();
        DbReferenceType primaryRef = new DbReferenceType();
        primaryRef.setDb(primaryDb);
        primaryRef.setId(primaryId);
        xref.setPrimaryRef(primaryRef);
        DbReferenceType secondaryRef = new DbReferenceType();
        secondaryRef.setDb(secondaryDb);
        secondaryRef.setId(secondaryId);
        xref.addSecondaryRef(secondaryRef);
        protein.setXref(xref);
        return protein;
    }
}