package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;

import java.text.Format;
import java.text.SimpleDateFormat;

/**
 * Utility Class for Outputting Database Snapshot Information.
 */
public class DbSnapshotInfo {

    /**
     * Gets Database Snapshot HTML Summary.
     * @param snapShotId Snapshot ID.
     * @return HTML Blurb.
     * @throws DaoException Database Error.
     */
    public static String getDbSnapshotHtml(long snapShotId) throws DaoException {
        StringBuffer html = new StringBuffer();
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        if (snapShotId > 0) {
            ExternalDatabaseSnapshotRecord snapshot = dao.getDatabaseSnapshot (snapShotId);
            html.append(snapshot.getExternalDatabase().getName() + ", ");
            if (snapshot.getSnapshotVersion() != null) {
                html.append("Release:  " + snapshot.getSnapshotVersion());
            }
            Format formatter = new SimpleDateFormat("dd-MMM-yy");
            String s = formatter.format(snapshot.getSnapshotDate());
            html.append(" [" + s + "]");
        }
        return html.toString();
    }
}
