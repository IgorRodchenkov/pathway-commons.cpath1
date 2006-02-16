// $Id: MemberMolecules.java,v 1.10 2006-02-16 15:18:50 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
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
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Calendar;

import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.sql.dao.DaoInternalLink;
import org.mskcc.pathdb.util.biopax.BioPaxRecordUtil;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;

/**
 * This class parses interaction data
 * given a cPath record id.
 *
 * @author Benjamin Gross.
 */
public class MemberMolecules {

	/**
	 *  Used to help us determine if moleculeSummary should be added to molecules hashset.
	*/
	private static HashSet moleculeNames;

	/**
	 * Should be called before each call to getMemberMolecules.
	 */
	public static void reset(){
		moleculeNames = new HashSet();
	}

	/**
	 * Finds all member molecules given CPathRecord.
	 *
	 * @param record CPathRecord
	 * @param longList ArrayList - if null, no timing performed
	 * @return HashSet
	 */
	public static HashSet getMemberMolecules(CPathRecord record, ArrayList longList) throws Exception {

		// for timing
        long startTime = 0;

		// hashset to return
		HashSet molecules = new HashSet();

		// get internal links
		DaoInternalLink daoInternalLinks = new DaoInternalLink();
		if (longList != null){
			startTime = Calendar.getInstance().getTimeInMillis();
		}
		ArrayList targets = daoInternalLinks.getTargetsWithLookUp(record.getId());
		if (longList != null){
			Long currentTime = new Long(Calendar.getInstance().getTimeInMillis() - startTime);
			longList.add(currentTime);
		}

		if (targets.size() > 0){
			for (int lc = 0; lc < targets.size(); lc++){
				CPathRecord targetRecord = (CPathRecord)targets.get(lc);
				molecules.addAll(getMemberMolecules(targetRecord, longList));
			}
		}
		else{
			BioPaxConstants biopaxConstants = new BioPaxConstants();
			if (biopaxConstants.isPhysicalEntity(record.getSpecificType())){
				BioPaxRecordSummary moleculeSummary = BioPaxRecordUtil.createBioPaxRecordSummary(record);
				if (moleculeSummary != null){
					String name = (moleculeSummary.getName() != null) ? moleculeSummary.getName() : moleculeSummary.getShortName();
					if (name != null && name.length() > 0){
						boolean addSummaryToMoleculesSet = moleculeNames.add(name);
						if (addSummaryToMoleculesSet) molecules.add(moleculeSummary);
					}
				}
			}
		}

		// outta here
		return molecules;
	}
}
