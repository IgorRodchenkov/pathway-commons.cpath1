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
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.references.ParseBackgroundReferencesTask;
import org.mskcc.pathdb.sql.transfer.ImportPsiToCPath;
import org.mskcc.pathdb.task.ProgressMonitor;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the ImportPsiToCPath class.
 *
 * @author Ethan Cerami
 */
public class TestImportPsiToCPath extends TestCase {

    /**
     * Tests Import.
     *
     * @throws Exception All Exceptions.
     */
    public void testAccess() throws Exception {
        ProgressMonitor pMonitor = new ProgressMonitor();
        ContentReader reader = new ContentReader();
        String file = new String("testData/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml, true,
                false, pMonitor);
        assertEquals(7, summary.getNumInteractorsProcessed());
        assertEquals(0, summary.getNumInteractorsFound());
        assertEquals(7, summary.getNumInteractorsSaved());
        assertEquals(6, summary.getNumInteractionsSaved());
        assertEquals(0, summary.getNumInteractionsClobbered());

        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = new ExternalReference("PIR", "BWBYD5");
        ArrayList records = linker.lookUpByExternalRefs(refs);
        CPathRecord record = (CPathRecord) records.get(0);
        assertEquals(4932, record.getNcbiTaxonomyId());
        assertEquals("GTP/GDP exchange factor for Rsr1 protein",
                record.getDescription());

        //  Try Saving Again.
        //  Validate that no new interactors are saved.
        //  Validate that new interactions clobbered old interactions.
        //  Only one interaction in psi_sample_mixed.xml has an external ref.
        //  Hence, only one interaction gets clobbered.
        summary = importer.addRecord(xml, true, false, pMonitor);
        assertEquals(0, summary.getNumInteractorsSaved());
        assertEquals(6, summary.getNumInteractionsSaved());
        assertEquals(1, summary.getNumInteractionsClobbered());

        //  Retrieve Interaction, DIP:  58E, and verify that all three
        //  interactors were saved.
        ExternalReference ref = new ExternalReference("DIP", "58E");
        records = linker.lookUpByExternalRef(ref);
        record = (CPathRecord) records.get(0);
        long interactionId = record.getId();
        DaoInternalLink internalLinker = new DaoInternalLink();
        records = internalLinker.getInternalLinksWithLookup(interactionId);
        assertEquals(3, records.size());
    }

    /**
     * Tests Data Import with ID Mapping Service
     *
     * @throws Exception All Exceptions.
     */
    public void testImportWithIdMappingService() throws Exception {
        //  Try to locate Q727A4 by its Affymetrix ID.
        //  This should fail.
        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference ref = new ExternalReference("Affymetrix",
                "1552275_3p_s_at");
        ArrayList records = linker.lookUpByExternalRef(ref);
        assertEquals(0, records.size());

        //  First, load a small set of background references into
        //  the background reference server.
        File file = new File("testData/references/unification_refs.txt");
        ParseBackgroundReferencesTask task =
                new ParseBackgroundReferencesTask(file, false);
        task.parseAndStoreToDb();

        //  Then, load a small PSI-MI File.
        //  This file contains UNIPROT_1234, and unification refs
        //  for UNIPROT_1234 exist in unification_refs.txt.
        ProgressMonitor pMonitor = new ProgressMonitor();
        ContentReader reader = new ContentReader();
        String psiFile = new String("testData/psi_sample_id_map.xml");
        String xml = reader.retrieveContent(psiFile);
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml, true,
                false, pMonitor);

        //  Now, try to locate UNIPROT_1234 by its PIR ID.
        linker = new DaoExternalLink();
        ref = new ExternalReference("PIR", "PIR_1234");
        records = linker.lookUpByExternalRef(ref);

        //  Verify that we have correctly located the record.
        assertEquals(1, records.size());
        CPathRecord record = (CPathRecord) records.get(0);

        //  Verify that XML has been modified to include external
        //  references derived from the background reference subsystem.
        assertTrue(record.getXmlContent().indexOf
                ("<primaryRef db=\"UNIPROT\" id=\"UNIPROT_1234\"/>") > 0);
        assertTrue(record.getXmlContent().indexOf
                ("<secondaryRef db=\"HUGE\" id=\"HUGE_4321\"/>") > 0);
        assertTrue(record.getXmlContent().indexOf
                ("<secondaryRef db=\"PIR\" id=\"PIR_4321\"/>") > 0);
        assertTrue(record.getXmlContent().indexOf
                ("<secondaryRef db=\"PIR\" id=\"PIR_1234\"/>") > 0);
        assertTrue(record.getXmlContent().indexOf
                ("<secondaryRef db=\"HUGE\" id=\"HUGE_1234\"/>") > 0);
    }
}