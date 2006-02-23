// $Id: PathwayMoleculesTable.java,v 1.5 2006-02-23 15:44:08 grossb Exp $
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
package org.mskcc.pathdb.taglib;

// imports
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Collections;
import java.util.Comparator;

import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils;

/**
 * Custom jsp tag for displaying the set of molecules contained in a pathway.
 *
 * @author Benjamin Gross
 */
public class PathwayMoleculesTable extends HtmlTable {

    /**
     * The number of molecules per row.
     */
    private static int MOLECULES_PER_ROW = 4;

    /**
     * HashSet of BioPaxSummary Molecules.
     */
    private HashSet moleculeSet;

    /**
     * HashSet of Molecule links.
     */
    private ArrayList moleculesLinkList;

    /**
     * Receives HashSet Attribute.
     *
     * @param moleculeSet HashSet.
     */
    public void setMoleculeSet(HashSet moleculeSet){
        this.moleculeSet = moleculeSet;
    }

    /**
     * Executes JSP Custom Tag
     */
    protected void subDoStartTag() {

        // here we go
        if (moleculeSet != null && moleculeSet.size() > 0){
			processMoleculeSet();
            outputRecords();
        }
    }

	/**
	 * Creates populates the molecule link set
	 * given the molecule set.
	 */
	private void processMoleculeSet(){

		// create an arraylist from our molecule set
		ArrayList molecules = new ArrayList(moleculeSet);

		// sort the array list
		Collections.sort(molecules, new MoleculeComparator());

		// setup our link list
		moleculesLinkList = new ArrayList();

		// interate through the molecules list
		int cnt = molecules.size();
		for (int lc = 0; lc < cnt; lc++){
			String moleculeLink = BioPaxRecordSummaryUtils.createEntityLink((BioPaxRecordSummary)molecules.get(lc));
			moleculesLinkList.add(moleculeLink);
		}

	}

    /**
     * Output the Interaction Information.
     */
    private void outputRecords() {

		// render the table
		startRow();

		// interate through list
		int cnt = moleculesLinkList.size();
		boolean endedRow = false;
		for (int lc = 1; lc <= cnt; lc++){
            append("<td>");
			append((String)moleculesLinkList.get(lc-1));
            append("</td>");
            if ((lc % MOLECULES_PER_ROW) == 0){
				// end the row
				endRow();
				endedRow = true;
				// start a new one if items are left
				if (lc < cnt){
					startRow();
					endedRow = false;
				}
			}
		}
		// do we have to cap a row ?
		if (!endedRow) endRow();
    }
}

/**
 * Compares ParticipantSummaryComponent Objects, and sorts by name.
 */
class MoleculeComparator implements Comparator {

    /**
	 * Compares BioPaxRecordSummary Objects, and sorts by name.
	 *
     * @param object0 Object
     * @param object1 Object
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to,
     * or greater than the second.
     */
    public int compare(Object object0, Object object1) {
        if (object0 != null && object1 != null) {
            BioPaxRecordSummary summary0 = (BioPaxRecordSummary) object0;
            BioPaxRecordSummary summary1 = (BioPaxRecordSummary) object1;
			String name0 = (summary0.getName() != null) ? summary0.getName() : summary0.getShortName();
			String name1 = (summary1.getName() != null) ? summary1.getName() : summary1.getShortName();
            if (name0 != null && name1!= null) {
                return name0.compareTo(name1);
            }
        }
        return -1;
    }
}
