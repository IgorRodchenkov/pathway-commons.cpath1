// $Id: TestImportBioPaxToCPath.java,v 1.18 2009-04-07 17:24:16 grossben Exp $
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
package org.mskcc.pathdb.test.schemas.biopax;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.ImportSummary;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.schemas.biopax.ImportBioPaxToCPath;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoSourceTracker;
import org.mskcc.pathdb.sql.references.ParseBackgroundReferencesTask;
import org.mskcc.pathdb.sql.snapshot.SnapshotReader;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.cache.EhCache;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the ImportBioPaxToCPath Class.
 *
 * @author Ethan Cerami
 */
public class TestImportBioPaxToCPath extends TestCase {

    /**
     * Tests BioPAX Import.
     *
     * @throws Exception All Exceptions.
     */
    public void testImport() throws Exception {
        //  Start Cache with Clean Slate
        EhCache.initCache();
        EhCache.resetAllCaches();

        //  Store some dummy Affymetrix IDs to the Database
        File file = new File("testData/references/link_out_refs3.txt");
        ParseBackgroundReferencesTask task = new ParseBackgroundReferencesTask
                (file, false);
        task.parseAndStoreToDb();

        //  Store some dummy UniProt to RefSeq Ids to the Database
        file = new File("testData/references/uniprot2refseq.txt");
        task = new ParseBackgroundReferencesTask (file, false);
        task.parseAndStoreToDb();

        DaoOrganism daoOrganism = new DaoOrganism();
        ArrayList organismList = daoOrganism.getAllOrganisms();

        //  Before import, we have 0 organisms
        int numOrganisms = organismList.size();

        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent
                ("testData/biopax/biopax1_sample1.owl");
        ProgressMonitor pMonitor = new ProgressMonitor();
        ImportBioPaxToCPath importer = new ImportBioPaxToCPath(false);

        SnapshotReader snapshotReader = new SnapshotReader(
                new File ("testData/biopax"), "db.info");
        long snapshotId = snapshotReader.getSnapshotRecord().getId();

        ImportSummary summary = importer.addRecord(xml, snapshotId, false, pMonitor);
        assertEquals(1, summary.getNumPathwaysSaved());
        assertEquals(0, summary.getNumPathwaysFound());
        assertEquals(4, summary.getNumInteractionsSaved());
        assertEquals(0, summary.getNumInteractionsFound());
        assertEquals(7, summary.getNumPhysicalEntitiesSaved());
        assertEquals(0, summary.getNumPhysicalEntitiesFound());

        //  After import, we have 1 organism
        organismList = daoOrganism.getAllOrganisms();
        assertEquals(numOrganisms + 1, organismList.size());

        //  After import, protein GLK (SWP:  P46880) should have an external
        //  link to Affymetrix ID:  1919_at.
        //  This verifies that the connection with the background link-out
        //  service is working.
        DaoExternalLink externalLinker = DaoExternalLink.getInstance();
        ArrayList recordList = externalLinker.lookUpByExternalRef
                (new ExternalReference("Affymetrix", "1919_at"));
        assertEquals(1, recordList.size());
        CPathRecord record = (CPathRecord) recordList.get(0);
        assertEquals("GLK", record.getName());

        //  Verify that the BioPAX XML now contains an XREF for Affymetrix
        int index = record.getXmlContent().indexOf("AFFYMETRIX");
        assertTrue(index > 0);

        //  Verify that the BioPAX XML now contains an XREF for cPath
        index = record.getXmlContent().indexOf
                ("XMLSchema#string\">CPATH</bp:DB>");
        assertTrue(index > 0);

        //  After import, protein GLK (SWP:  P46880) should have an external
        //  link to Unification XREF:  Ref Seq ID:  NP_051067.
        //  This verifies that the connection with the background unification
        //  service is working.
        externalLinker = DaoExternalLink.getInstance();
        recordList = externalLinker.lookUpByExternalRef
                (new ExternalReference("RefSeq", "NP_051067"));
        assertEquals(1, recordList.size());
        record = (CPathRecord) recordList.get(0);
        assertEquals("GLK", record.getName());

        //  Verify that the BioPAX XML now contains an XREF for RefSeq
        index = record.getXmlContent().indexOf("REF_SEQ");
        assertTrue(index > 0);

        //  Verify that record is not linked to any snapshot
        assertEquals (-1, record.getSnapshotId());

        //  Verify that we can get from the merged physical entity record to the untouched
        //  physical entity record
        DaoSourceTracker daoSourceTracker = new DaoSourceTracker();
        ArrayList list = daoSourceTracker.getSourceRecords(record.getId());
        assertEquals (1, list.size());
        CPathRecord sourceRecord = (CPathRecord) list.get(0);
        DaoExternalDbSnapshot daoSnapshot = new DaoExternalDbSnapshot();
        ExternalDatabaseSnapshotRecord snapshotRecord =
                daoSnapshot.getDatabaseSnapshot(sourceRecord.getSnapshotId());
        assertEquals ("Reactome", snapshotRecord.getExternalDatabase().getName());
        assertEquals ("1.0", snapshotRecord.getSnapshotVersion());

        //  Get Pathway by Unification Ref, and verify snapshot
        recordList = externalLinker.lookUpByExternalRef
                (new ExternalReference("Reactome", "69091"));
        assertEquals (1, recordList.size());
        record = (CPathRecord) recordList.get(0);
        assertEquals("glycolysis", record.getName());
        snapshotRecord = daoSnapshot.getDatabaseSnapshot(record.getSnapshotId());
        assertEquals ("Reactome", snapshotRecord.getExternalDatabase().getName());
        assertEquals ("1.0", snapshotRecord.getSnapshotVersion());

        //  Try Saving Again
        importer = new ImportBioPaxToCPath(false);
        summary = importer.addRecord(xml, snapshotId, false, pMonitor);

        //  Because the pathway has a unification xref, it should not
        //  be saved again.
        assertEquals(0, summary.getNumPathwaysSaved());
        assertEquals(1, summary.getNumPathwaysFound());

        //  Two of the interactions have unification XRefs.
        //  Therefore, two are found, and two are resaved.
        assertEquals(2, summary.getNumInteractionsSaved());
        assertEquals(2, summary.getNumInteractionsFound());

        //  Because all physical entities have Unification XRefs, they
        //  should not be saved again.
        assertEquals(0, summary.getNumPhysicalEntitiesSaved());
        assertEquals(7, summary.getNumPhysicalEntitiesFound());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test that we can import BioPAX records into cPath";
    }
}
