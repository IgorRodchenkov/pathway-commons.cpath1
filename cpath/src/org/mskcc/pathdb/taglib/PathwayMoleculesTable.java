// $Id: PathwayMoleculesTable.java,v 1.21 2007-01-03 16:37:15 cerami Exp $
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

import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummary;
import org.mskcc.pathdb.schemas.biopax.summary.BioPaxRecordSummaryUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

/**
 * Custom jsp tag for displaying the set of molecules contained in a pathway.
 *
 * @author Benjamin Gross
 */
public class PathwayMoleculesTable extends HtmlTable {

    /**
     * Our assumed number cols/window.
     */
    private static final int NUM_COLS = 100;

    /**
     * The number of molecules per row.
     */
    private int moleculesPerRow = 4;

    /**
     * Set of BioPaxSummary Molecules.
     */
    private Set moleculeSet;

	/**
	 * Total number of molecules in db.
	 */
	private Integer totalNumMolecules;

    /**
     * ArrayList of Molecule links.
     */
    private ArrayList moleculesLinkList;

    /**
     * HttpServlet Request Object.
     */
    private HttpServletRequest request;

    /**
     * Current cPath Id.
     */
    private long cPathId;

    /**
     * Receives Set Attribute.
     *
     * @param moleculeSet Set.
     */
    public void setMoleculeSet(Set moleculeSet) {
        this.moleculeSet = moleculeSet;
    }

    /**
     * Receives totalNumMolecules Attribute.
     *
     * @param totalNumMolecules
     */
    public void setTotalNumMolecules(Integer totalNumMolecules) {
        this.totalNumMolecules = totalNumMolecules;
    }

    /**
     * Receives the Current Request Object.
     *
     * @param request HttpServletRequest Object.
     */
    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    /**
     * Receives the Current cPath ID.
     *
     * @param cPathId cPath ID.
     */
    public void setcpathId(long cPathId) {
        this.cPathId = cPathId;
    }

    /**
     * Executes JSP Custom Tag
     */
    protected void subDoStartTag() {
        String param = request.getParameter(BioPaxShowFlag.SHOW_FLAG);
        BioPaxShowFlag showFlag = new BioPaxShowFlag(param);
        if (moleculeSet != null && moleculeSet.size() > 0) {
            processMoleculeSet(showFlag);
            String title = "Contains the Following Molecules";
            String htmlHeader = BioPaxShowFlag.createHtmlHeader(BioPaxShowFlag.DEFAULT_NUM_RECORDS,
                    totalNumMolecules, cPathId, title, showFlag,
                    BioPaxShowFlag.SHOW_ALL_MOLECULES, "pe_list");
            append(htmlHeader);
            append("<TABLE>");
            outputRecords();
            endTable();
        }
    }

    /**
     * Populates the molecule link set given the molecule set.
     */
    private void processMoleculeSet(BioPaxShowFlag showFlag) {
        // create an arraylist from our molecule set
        ArrayList molecules = new ArrayList(moleculeSet);

        // sort the array list
        Collections.sort(molecules, new MoleculeComparator());

        // setup our link list
        moleculesLinkList = new ArrayList();

        int cnt = BioPaxShowFlag.determineEndIndex(BioPaxShowFlag.DEFAULT_NUM_RECORDS, molecules.size(),
                showFlag, BioPaxShowFlag.SHOW_ALL_MOLECULES);

        int maxMoleculeNameLength = 0;
        for (int lc = 0; lc < cnt; lc++) {
            BioPaxRecordSummary molecule = (BioPaxRecordSummary) molecules.get(lc);
            String moleculeLink = BioPaxRecordSummaryUtils.createEntityLink(molecule);
            moleculesLinkList.add(moleculeLink);
            // compute max molecule name length
            // consult BioPaxRecordSummaryUtils.getRecordName() to see how name is
            // created in call to createEntityLink above
            String moleculeName = (molecule.getShortName() != null
                    && molecule.getShortName().length() > 0) ? molecule.getShortName()
                    : (molecule.getName() != null && molecule.getName().length() > 0)
                    ? molecule.getName() : null;
            if (moleculeName != null) {
                maxMoleculeNameLength = computeMaxMoleculeNameLength
                        (moleculeName, maxMoleculeNameLength);
            }
        }
        moleculesPerRow = (maxMoleculeNameLength > 0)
                ? (NUM_COLS / maxMoleculeNameLength) : moleculesPerRow;
    }

    /**
     * Computes max molecule name length.
     *
     * @param moleculeName         String
     * @param currentMaxNameLength int
     * @return int
     */
    private int computeMaxMoleculeNameLength(String moleculeName, int currentMaxNameLength) {

        return (moleculeName.length() > BioPaxRecordSummaryUtils.NAME_LENGTH)
                ? BioPaxRecordSummaryUtils.NAME_LENGTH
                : (moleculeName.length() > currentMaxNameLength)
                ? moleculeName.length() : currentMaxNameLength;
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
        for (int lc = 1; lc <= cnt; lc++) {
            append("<td>");
            append((String) moleculesLinkList.get(lc - 1));
            append("</td>");
            if ((lc % moleculesPerRow) == 0) {
                // end the row
                endRow();
                endedRow = true;
                // start a new one if items are left
                if (lc < cnt) {
                    startRow();
                    endedRow = false;
                }
            }
        }
        // do we have to cap a row ?
        if (!endedRow) {
            endRow();
        }
    }
}

/**
 * Compares ParticipantSummaryComponent Objects, and sorts by name.
 *
 * @author Benjamin Gross
 */
class MoleculeComparator implements Comparator {

    /**
     * Compares BioPaxRecordSummary Objects, and sorts by name.
     *
     * @param object0 Object
     * @param object1 Object
     * @return a negative integer, zero, or a positive integer as the first argument is
     *         less than, equal to, or greater than the second.
     */
    public int compare(Object object0, Object object1) {
        if (object0 != null && object1 != null) {
            BioPaxRecordSummary summary0 = (BioPaxRecordSummary) object0;
            BioPaxRecordSummary summary1 = (BioPaxRecordSummary) object1;
            String label0 = summary0.getLabel();
            String label1 = summary1.getLabel();
            if (label0 != null && label1 != null) {
                return label0.compareTo(label1);
            }
        }
        return -1;
    }
}
