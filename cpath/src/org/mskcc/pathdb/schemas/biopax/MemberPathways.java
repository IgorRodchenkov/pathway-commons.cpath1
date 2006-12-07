// $Id: MemberPathways.java,v 1.16 2006-12-07 15:46:38 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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

// package
package org.mskcc.pathdb.schemas.biopax;

// imports

import org.jdom.JDOMException;
import org.mskcc.pathdb.taglib.BioPaxShowFlag;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.InternalLinkRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.GlobalFilterSettings;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;

/**
 * This class parses interaction data
 * given a cPath record id.
 *
 * @author Benjamin Gross.
 */
public class MemberPathways {

    /**
     * Finds all member pathways given CPathRecord.
     *
     * @param record CPathRecord
	 * @param set Set<BioPaxRecordSummary> - set gets populated with BioPaxRecordSummary for pathawys
	 * @param filterSettings GlobalFilterSettings (filters the records by dataset)
	 * @param flag BioPaxShowFlag (flag indicates show all or top X pathways - controls result set size)
     * @return Integer - total number of pathways record is a member of -
	 *         (required to render "show 1 - 20 of XXX" header)
     * @throws DaoException  Throwable
     */
    public static Integer getMemberPathways(CPathRecord record,
											Set<BioPaxRecordSummary> pathwaySet,
											GlobalFilterSettings filterSettings,
											BioPaxShowFlag flag) throws DaoException {
        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoInternalFamily dao = new DaoInternalFamily();
		return dao.getAncestorSummaries(record.getId(),
										CPathRecordType.PATHWAY,
										pathwaySet,
										filterSettings.getSnapshotIdSet(),
										(flag.getFlag(BioPaxShowFlag.SHOW_ALL_PATHWAYS) == 1) ? true : false);
    }

}
