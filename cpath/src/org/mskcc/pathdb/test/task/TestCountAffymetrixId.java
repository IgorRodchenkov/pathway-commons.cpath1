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
package org.mskcc.pathdb.test.task;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.sql.transfer.UpdatePsiInteractor;
import org.mskcc.pathdb.task.CountAffymetrixIdsTask;

public class TestCountAffymetrixId extends TestCase {

    public void test() throws Exception {
        CountAffymetrixIdsTask task = new CountAffymetrixIdsTask(562, false);
        int numRecords = task.getTotalNumRecords();
        int withAffyIds = task.getNumRecordsWithAffymetrixIds();

        //  Should be 1 Record, with 0 Affy Ids.
        assertEquals(1, numRecords);
        assertEquals(0, withAffyIds);

        //  Now add an Fake AffyId to existing interactor
        ExternalReference existingRef = new ExternalReference("SwissProt",
                "P06139");
        ExternalReference newRef = new ExternalReference
                ("Affymetrix", "a100_at");
        UpdatePsiInteractor updater = new UpdatePsiInteractor
                (existingRef, newRef, false);
        updater.doUpdate();

        //  Shoulw now be 1 Record, with 1 Affy Id.
        task = new CountAffymetrixIdsTask(562, false);
        numRecords = task.getTotalNumRecords();
        withAffyIds = task.getNumRecordsWithAffymetrixIds();
        assertEquals(1, numRecords);
        assertEquals(1, withAffyIds);
    }
}