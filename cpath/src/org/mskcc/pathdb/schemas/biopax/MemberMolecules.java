// $Id: MemberMolecules.java,v 1.19 2006-12-04 19:14:33 grossb Exp $
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

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.taglib.BioPaxShowFlag;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryException;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoInternalFamily;
import org.mskcc.pathdb.sql.dao.DaoCPath;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;

import java.util.HashSet;
import java.util.ArrayList;

/**
 * This class determines all molecules within a specific pathway.
 *
 * @author Benjamin Gross.
 */
public class MemberMolecules {


    /**
     * Finds all member molecules for specified Pathway record.
     *
     * @param record CPathRecord
	 * @param set HashSet<BioPaxRecordSummary> - set gets populated with BioPaxRecordSummary for molecules
	 * @param flag BioPaxShowFlag (flag indicates show all or top X molecules - controls result set size)
     * @return Integer - total number of molecules in db - required to render "show 1 - 20 of XXX" header
     * @throws DaoException Database Access Error.
     */
    public static Integer getMoleculesInPathway(CPathRecord record,
												HashSet<BioPaxRecordSummary> moleculeSet,
												BioPaxShowFlag flag) throws DaoException {

        DaoCPath daoCPath = DaoCPath.getInstance();
        DaoInternalFamily dao = new DaoInternalFamily();
        return dao.getDescendentSummaries(record.getId(),
										  CPathRecordType.PHYSICAL_ENTITY,
										  moleculeSet,
										  (flag.getFlag(BioPaxShowFlag.SHOW_ALL_MOLECULES) == 1) ? true : false);
    }

    /**
     * Finds all member molecules for specified Complex record.
     *
     * @param record   CPathRecord
     * @return HashSet Set of BioPaxRecordSummary Objects.
     * @throws BioPaxRecordSummaryException Error Creating Summary.
     * @throws DaoException                 Database Access Error.
     */
    public static HashSet getMoleculesInComplex(CPathRecord record) throws DaoException,
        BioPaxRecordSummaryException {
        DaoInternalLink dao = new DaoInternalLink();
        HashSet molecules = new HashSet();
        ArrayList list = dao.getTargetsWithLookUp(record.getId());
        for (int i=0; i<list.size(); i++) {
            CPathRecord peRecord = (CPathRecord) list.get(i);
            BioPaxRecordSummary moleculeSummary =
                BioPaxRecordUtil.createBioPaxRecordSummary(peRecord);
            molecules.add(moleculeSummary);
        }
        return molecules;
    }
}
