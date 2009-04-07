package org.mskcc.pathdb.sql.util;

import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoOrganism;
import org.mskcc.pathdb.model.CPathRecordType;

/**
 * Database Stats.
 *
 * @author Ethan Cerami.
 */
public class DatabaseStats {
    private static DatabaseStats stats;
    private int numPathways;
    private int numInteractions;
    private int numPhysicalEntities;
    private int numOrganisms;

    /**
     * Gets Singelton Instance.
     * @return  Database Stats Object.
     * @throws DaoException Database Access Error.
     */
    public static DatabaseStats getInstance() throws DaoException {
        if (stats == null) {
            stats = new DatabaseStats();
        }
        return stats;
    }

    /**
     * Private Constructor.
     * @throws DaoException Database Acccess Error.
     */
    private DatabaseStats() throws DaoException {
        DaoCPath dao = DaoCPath.getInstance();
        numPathways = dao.getNumEntities(CPathRecordType.PATHWAY);
        numInteractions = dao.getNumEntities(CPathRecordType.INTERACTION);
        numPhysicalEntities = dao.getNumPhysicalEntities(true);
        DaoOrganism daoOrganism = new DaoOrganism();
		numOrganisms = daoOrganism.organismCount(true);
    }

    /**
     * Gets Total Number of Pathways.
     * @return number of pathways.
     */
    public int getNumPathways() {
        return numPathways;
    }

    /**
     * Gets Total Number of Interactions.
     * @return number of interactions.
     */
    public int getNumInteractions() {
        return numInteractions;
    }

    /**
     * Gets Total Number of Physical Entities.
     * @return number of physical entities.
     */
    public int getNumPhysicalEntities() {
        return numPhysicalEntities;
    }

    /**
     * Gets Total Number of Organisms.
     * @return number of Organisms.
     */
    public int getNumOrganisms() {
        return numOrganisms;
    }
}
