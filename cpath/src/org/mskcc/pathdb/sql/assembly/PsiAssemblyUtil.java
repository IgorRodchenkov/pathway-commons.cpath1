package org.mskcc.pathdb.sql.assembly;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;

/**
 * Contains core classes, used by both PsiAssembly and PsiAssemblyStringOnly.
 *
 * @author Ethan Cerami
 */
public class PsiAssemblyUtil {
    private static Logger log = Logger.getLogger(PsiAssemblyUtil.class);    

    /**
     * Given a List of Interaction Records, retrieve all associated
     * interactors.
     *
     * @param interactions ArrayList of CPathRecord Objects containing
     *                     Interactions.
     * @return HashMap of All Interactors, indexed by cpath ID.
     * @throws org.mskcc.pathdb.sql.dao.DaoException Data Access Error.
     */
    static HashMap extractInteractors(ArrayList interactions)
            throws DaoException {
        log.info("Retreiving links and interactors");
        Date start = new Date();
        HashMap interactorMap = new HashMap();
        DaoInternalLink linker = new DaoInternalLink();
        DaoCPath cpath = DaoCPath.getInstance();
        for (int i = 0; i < interactions.size(); i++) {
            CPathRecord record = (CPathRecord) interactions.get(i);
            ArrayList list = linker.getTargets(record.getId());
            for (int j = 0; j < list.size(); j++) {
                InternalLinkRecord link = (InternalLinkRecord) list.get(j);
                Long key = new Long(link.getTargetId());
                if (!interactorMap.containsKey(key)) {
                    CPathRecord interactor = cpath.getRecordById
                            (link.getTargetId());
                    interactorMap.put(key, interactor);
                }
            }
        }
        Date stop = new Date();
        long timeInterval = stop.getTime() - start.getTime();
        log.info("Total time to retrieve internal links and interactors:  "
                + timeInterval + " ms");
        log.info("Total number of linked interactors found:  "
                + interactorMap.size());
        return interactorMap;
    }
}
