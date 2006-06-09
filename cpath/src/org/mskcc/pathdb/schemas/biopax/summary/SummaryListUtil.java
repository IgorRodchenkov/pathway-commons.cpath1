// $Id: SummaryListUtil.java,v 1.9 2006-06-09 19:22:03 cerami Exp $
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
package org.mskcc.pathdb.schemas.biopax.summary;

import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * SummaryList Utility Class.
 * <p/>
 * Given a cPath ID, this class will retrieve summary objects of all children.
 * For example, if given the cPath ID of a pathway, this class will retrieve
 * summaries for all children interactions.
 *
 * @author Ethan Cerami.
 */
public class SummaryListUtil {
    /**
     * Mode:  Get Immediate Children.
     */
    public static final int MODE_GET_CHILDREN = 1;

    /**
     * Mode:  Get Immediate Parents.
     */
    public static final int MODE_GET_PARENTS = 2;

    private long cPathId;
    private ArrayList summaryList = new ArrayList();
    private int mode;

    /**
     * Constructor.
     *
     * @param cPathId cPath ID.
     * @param mode    int.
     */
    public SummaryListUtil(long cPathId, int mode) {
        this.cPathId = cPathId;
        this.mode = mode;
    }

    /**
     * Gets Summaries for all Children of the specified cPathId.
     *
     * @return ArrayList of EntitySummary Objects.
     * @throws DaoException           Database Access Error.
     * @throws EntitySummaryException Error Creating Summary.
     */
    public ArrayList getSummaryList() throws EntitySummaryException,
            DaoException {
        DaoInternalLink daoInternalLinks = new DaoInternalLink();
        ArrayList internalLinks;
        if (mode == MODE_GET_CHILDREN) {
            internalLinks = daoInternalLinks.getTargets(cPathId);
        } else {
            internalLinks = daoInternalLinks.getSources(cPathId);
        }
        for (int i = 0; i < internalLinks.size(); i++) {
            InternalLinkRecord internalLink =
                    (InternalLinkRecord) internalLinks.get(i);
            long targetId;
            if (mode == MODE_GET_CHILDREN) {
                targetId = internalLink.getTargetId();
            } else {
                targetId = internalLink.getSourceId();
            }
            EntitySummaryParser summaryParser =
                    new EntitySummaryParser(targetId);
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
 *
 * @author Ethan Cerami
 */
class SummaryComparator implements Comparator {

    /**
     * Compares two EntitySummary Objects.
     *
     * @param object0 EntitySummary Object 0.
     * @param object1 EntitySummary Object 1/
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(Object object0, Object object1) {
        if (object0 != null && object1 != null) {
            EntitySummary summary0 = (EntitySummary) object0;
            EntitySummary summary1 = (EntitySummary) object1;
            String type0 = summary0.getSpecificType();
            String type1 = summary1.getSpecificType();
            if (type0 != null && type1 != null) {
                return type0.compareTo(type1);
            }
        }
        return -1;
    }
}
