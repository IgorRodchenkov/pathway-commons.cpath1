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
package org.mskcc.pathdb.test.schemas.psi;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.sql.dao.DaoBackgroundReferences;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.references.ParseBackgroundReferencesTask;
import org.mskcc.pathdb.schemas.psi.ImportPsiToCPath;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.schemas.psi.ImportPsiToCPath;

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
        String file = new String("testData/psi_mi/psi_sample_mixed.xml");
        String xml = reader.retrieveContent(file);
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml, true,
                false, pMonitor);
        assertEquals(7, summary.getNumPhysicalEntitiesProcessed());
        assertEquals(0, summary.getNumPhysicalEntitiesFound());
        assertEquals(7, summary.getNumPhysicalEntitiesSaved());
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
        assertEquals(0, summary.getNumPhysicalEntitiesSaved());
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
     * Tests Data Import with Background Reference Service
     *
     * @throws Exception All Exceptions.
     */
    public void testImportWithBackroundReferenceService() throws Exception {
        //  Start with zero background references
        DaoBackgroundReferences dao = new DaoBackgroundReferences();
        dao.deleteAllRecords();

        //  Load a small set of unification references into
        //  the background reference server.
        File file = new File("testData/references/unification_refs2.txt");
        ParseBackgroundReferencesTask task =
                new ParseBackgroundReferencesTask(file, false);
        int recordsSaved = task.parseAndStoreToDb();

        //  Load a small set of affymetrix ids into the background ref server
        file = new File("testData/references/link_out_refs2.txt");
        task = new ParseBackgroundReferencesTask(file, false);
        recordsSaved = task.parseAndStoreToDb();

        //  Then, load a small PSI-MI File.
        //  This file contains P53, and unification refs
        //  for P53 exist in unification_refs2.txt.
        ProgressMonitor pMonitor = new ProgressMonitor();
        ContentReader reader = new ContentReader();
        String psiFile = new String("testData/psi_mi/psi_sample_id_map.xml");
        String xml = reader.retrieveContent(psiFile);
        ImportPsiToCPath importer = new ImportPsiToCPath();
        ImportSummary summary = importer.addRecord(xml, true,
                false, pMonitor);

        //  Now, try to locate P53 by an equivalent UNIPROT ID.
        DaoExternalLink linker = new DaoExternalLink();
        ExternalReference ref = new ExternalReference("UNIPROT", "P04637");
        ArrayList records = linker.lookUpByExternalRef(ref);

        //  Verify that we have correctly located the record.
        assertEquals(1, records.size());

        //  Now, try to locate P53 by its PIR ID.
        linker = new DaoExternalLink();
        ref = new ExternalReference("PIR", "DNHU53");
        records = linker.lookUpByExternalRef(ref);

        //  Verify that we have correctly located the record.
        assertEquals(1, records.size());
        CPathRecord record = (CPathRecord) records.get(0);

        //  Verify that XML has been modified to include unification
        //  references derived from the background reference subsystem.
        //  note that this tests only a handful of the new refs,
        //  not all of them
        xml = record.getXmlContent();
        assertTrue(xml.indexOf("<primaryRef db=\"UNIPROT\" "
                + "id=\"Q16848\"/>") > 0);
        assertTrue(xml.indexOf("<secondaryRef db=\"UNIPROT\" "
                + "id=\"Q9NP68\"/>") > 0);
        assertTrue(xml.indexOf("<secondaryRef db=\"UNIPROT\" "
                + "id=\"Q9NPJ2\"/>") > 0);
        assertTrue(xml.indexOf("<secondaryRef db=\"PIR\" "
                + "id=\"DNHU53\"/>") > 0);
        assertTrue(xml.indexOf("<secondaryRef db=\"UNIPROT\" "
                + "id=\"Q99659\"/>") > 0);

        //  Verify that XML has been modified to include linkouts
        //  derived from the background reference subsystem.
        assertTrue(xml.indexOf("<secondaryRef db=\"AFFYMETRIX\" "
                + "id=\"1939_at\"/>") > 0);

    }
}