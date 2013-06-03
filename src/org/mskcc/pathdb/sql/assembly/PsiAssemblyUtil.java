package org.mskcc.pathdb.sql.assembly;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.apache.log4j.Logger;

import java.util.*;

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
     * @param useOptimizedCode use optimized code flag.
     * @return HashMap of All Interactors, indexed by cpath ID.
     * @throws org.mskcc.pathdb.sql.dao.DaoException Data Access Error.
     */
    static HashMap extractInteractors(ArrayList interactions, boolean useOptimizedCode)
            throws DaoException {
        log.info("Retrieving links");
        Date start = new Date();
        HashMap interactorMap = new HashMap();
        DaoInternalLink linker = new DaoInternalLink();
        DaoCPath cpath = DaoCPath.getInstance();
        Set interactorIdSet = new HashSet();
        for (int i = 0; i < interactions.size(); i++) {
            CPathRecord record = (CPathRecord) interactions.get(i);
            ArrayList list = linker.getTargets(record.getId());
            for (int j = 0; j < list.size(); j++) {
                InternalLinkRecord link = (InternalLinkRecord) list.get(j);
                Long cathId = new Long(link.getTargetId());
                interactorIdSet.add(cathId);
            }
        }
        Date stop = new Date();
        long timeInterval = stop.getTime() - start.getTime();
        log.info("Total time to retrieve internal links:  "
                + timeInterval + " ms");

        start = new Date();
        Iterator iterator = interactorIdSet.iterator();
        if (useOptimizedCode) {
            log.info("Retrieving all interactor records in one large query");
            Long ids[] = (Long[]) interactorIdSet.toArray(new Long[interactorIdSet.size()]);
            long cpathIds[] = new long[ids.length];
            for (int i=0; i<cpathIds.length; i++) {
                cpathIds[i] = ids[i];
            }
            ArrayList recordList = cpath.getRecordsById(cpathIds);
            for (int i=0; i<recordList.size(); i++) {
                CPathRecord interactor = (CPathRecord) recordList.get(i);
                interactorMap.put(interactor.getId(), interactor);
            }
        } else {
            log.info("Retrieving all interactor records individually");
            while (iterator.hasNext()) {
                long cpathId = (Long) iterator.next();
                CPathRecord interactor = cpath.getRecordById (cpathId);
                interactorMap.put(cpathId, interactor);
            }
        }
        stop = new Date();
        timeInterval = stop.getTime() - start.getTime();
        log.info("Total time to retrieve interactors:  "
                + timeInterval + " ms");
        log.info("Total number of linked interactors found:  "
                + interactorMap.size());
        return interactorMap;
    }
}
