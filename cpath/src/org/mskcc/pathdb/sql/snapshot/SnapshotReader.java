// $Id: SnapshotReader.java,v 1.1 2006-10-03 19:50:36 cerami Exp $
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
package org.mskcc.pathdb.sql.snapshot;

import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.ImportException;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Utility to Read in Snapshot Data from db.info.
 *
 * @author Ethan Cerami.
 */
public class SnapshotReader {
    private ExternalDatabaseSnapshotRecord snapshotRecord;

    /**
     * Constructor.
     * @param directory             Directory containing db.info file.
     * @throws ImportException      Error in Importing Data.
     */
    public SnapshotReader (File directory) throws ImportException {
        try {
            File file = new File (directory, "db.info");
            Properties properties = new Properties ();
            properties.load(new FileInputStream (file));
            String dbName = (String) properties.get("db_name");
            String snapshotVersion = (String) properties.get
                    ("db_snapshot_version");
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            Date snapshotDate = format.parse((String) properties.get
                    ("db_snapshot_date"));

            DaoExternalDb daoDb = new DaoExternalDb();
            ExternalDatabaseRecord dbRecord = daoDb.getRecordByName(dbName);
            if (dbRecord == null) {
                throw new ImportException ("In db.info, db_name is set to: "
                    + dbName + ".  However, no such database currently exists "
                    + "in cPath.  You must first load meta-data regarding "
                    + "this database.  Refer to Administration Guide for "
                    + "complete details.");
            }
            DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
            snapshotRecord = dao.getDatabaseSnapshot(dbRecord.getId(),
                    snapshotDate);
            if (snapshotRecord == null) {
                dao.addRecord(dbRecord.getId(), snapshotDate, snapshotVersion);
                snapshotRecord = dao.getDatabaseSnapshot(dbRecord.getId(),
                        snapshotDate);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new ImportException ("In order to load files in this "
                + "directory, you must create a valid db.info file.  "
                + "Refer to Administration Guide for complete details.");
        } catch (IOException e) {
            throw new ImportException (e);
        } catch (ParseException e) {
            throw new ImportException (e);
        } catch (DaoException e) {
            throw new ImportException (e);
        }
    }

    /**
     * Gets ExternalDatabaseSnapshot Record defined by db.info.
     *
     * @return ExternalDatabaseSnapshotRecord Object.
     */
    public ExternalDatabaseSnapshotRecord getSnapshotRecord() {
        return this.snapshotRecord;
    }
}
