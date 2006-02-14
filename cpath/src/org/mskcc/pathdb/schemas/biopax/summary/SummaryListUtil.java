package org.mskcc.pathdb.schemas.biopax.summary;

import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.jdom.JDOMException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * SummaryList Utility Class.
 *
 * Given a cPath ID, this class will retrieve summary objects of all children.  For example, if given the cPath
 * ID of a pathway, this class will retrieve summaries for all children interactions.
 *
 * @author Ethan Cerami.
 */
public class SummaryListUtil {
    private long cPathId;
    private ArrayList summaryList = new ArrayList();

    /**
     * Constructor.
     * @param cPathId   cPath ID.
     */
    public SummaryListUtil (long cPathId) {
        this.cPathId = cPathId;
    }

    /**
     * Gets Summaries for all Children of the specified cPathId.
     * @return ArrayList of EntitySummary Objects.
     */
    public ArrayList getSummaryList() throws DaoException, NoSuchMethodException, IllegalAccessException,
            IOException, InvocationTargetException, EntitySummaryException, JDOMException {
        DaoInternalLink daoInternalLinks = new DaoInternalLink();
        ArrayList internalLinks = daoInternalLinks.getTargets(cPathId);
        for (int i=0; i<internalLinks.size(); i++) {
            InternalLinkRecord internalLink = (InternalLinkRecord) internalLinks.get(i);
            long targetId = internalLink.getTargetId();
            EntitySummaryParser summaryParser = new EntitySummaryParser(targetId);
            EntitySummary summary = summaryParser.getEntitySummary();
            summaryList.add(summary);
        }

        //  Sort based on Specific Type
        Collections.sort(summaryList, new SummaryComparator());
        return summaryList;
    }
}

/**
 * Compares two EntitySummary Objects, and sorts by specific type.
 */
class SummaryComparator implements Comparator {

    /**
     * Compares two EntitySummary Objects.
     * @param object0 EntitySummary Object 0.
     * @param object1 EntitySummary Object 1/
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to,
     * or greater than the second.
     */
    public int compare(Object object0, Object object1) {
        EntitySummary summary0 = (EntitySummary) object0;
        EntitySummary summary1 = (EntitySummary) object1;
        String type0 = summary0.getSpecificType();
        String type1 = summary1.getSpecificType();
        return type0.compareTo(type1);
    }
}