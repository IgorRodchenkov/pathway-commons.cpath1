package org.mskcc.pathdb.taglib;

import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.model.CPathRecord;
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
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        if (snapShotId > 0) {
			return getDbSnapshotHtml(dao.getDatabaseSnapshot(snapShotId), true, true);
        }
        return "";
    }

    /**
     * Gets Database Snapshot HTML Summary.
     * @param snapShotId Snapshot ID.
	 * @param createLink boolean
     * @return HTML Blurb.
     * @throws DaoException Database Error.
     */
    public static String getDbSnapshotHtml(long snapShotId, boolean createLink) throws DaoException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        if (snapShotId > 0) {
			return getDbSnapshotHtml(dao.getDatabaseSnapshot(snapShotId), true, createLink);
        }
        return "";
    }

    /**
     * Gets Database Snapshot HTML Summary.
     * @param snapShotId Snapshot ID.
     * @return HTML Blurb.
     * @throws DaoException Database Error.
     */
    public static String getDbSnapshotHtmlAbbrev(long snapShotId) throws DaoException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        if (snapShotId > 0) {
			return getDbSnapshotHtml(dao.getDatabaseSnapshot(snapShotId), false, true);
        }
        return "";
    }

    /**
     * Gets Database Snapshot HTML Summary.
     * @param snapshot ExternalDatabaseSnapshotRecord.
     * @return HTML Blurb.
     */
    public static String getDbSnapshotHtml(ExternalDatabaseSnapshotRecord snapshot,
										   boolean showAllDetails, boolean createLink) {

        StringBuffer html = new StringBuffer();
		if (createLink) {
			html.append ("<a href='dbSnapshot.do?snapshot_id=" + snapshot.getId() + "'>");
		}
		html.append(snapshot.getExternalDatabase().getName());

        if (showAllDetails) {
            if (snapshot.getSnapshotVersion() != null
                    && ! snapshot.getSnapshotVersion().equals(CPathRecord.NA_STRING)) {
                html.append (", ");
                html.append("Release:  " + snapshot.getSnapshotVersion());
            }
        }
		if (createLink) {
			html.append ("</a>");
		}
        if (showAllDetails) {
            Format formatter = new SimpleDateFormat("dd-MMM-yy");
            String s = formatter.format(snapshot.getSnapshotDate());
            html.append(" [" + s + "]");
        }
        return html.toString();
	}
}
