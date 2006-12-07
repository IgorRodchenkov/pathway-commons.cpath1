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
			return getDbSnapshotHtml(dao.getDatabaseSnapshot(snapShotId));
        }
        return "";
    }

    /**
     * Gets Database Snapshot HTML Summary.
     * @param snapShot ExternalDatabaseSnapshotRecord.
     * @return HTML Blurb.
     */
    public static String getDbSnapshotHtml(ExternalDatabaseSnapshotRecord snapshot) {

        StringBuffer html = new StringBuffer();
		html.append ("<A HREF='dbSnapshot.do?snapshot_id=" + snapshot.getId() + "'>");
		html.append(snapshot.getExternalDatabase().getName() + ", ");
		if (snapshot.getSnapshotVersion() != null) {
			html.append("Release:  " + snapshot.getSnapshotVersion());
		}
		html.append ("</A>");
		Format formatter = new SimpleDateFormat("dd-MMM-yy");
		String s = formatter.format(snapshot.getSnapshotDate());
		html.append(" [" + s + "]");
		return html.toString();
	}
}
