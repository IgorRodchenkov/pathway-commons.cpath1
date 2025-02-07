// $Id: BioPaxShowFlag.java,v 1.4 2006-11-29 16:55:45 grossb Exp $
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

/**
 * The BioPaxShowFlag contains information about what to show on BioPaxRecord.jsp.  We do
 * not currently allow pagination of results.  Rather, we provide a preview mode, where
 * we show the first X records in a particular set, and a show all mode, where we show
 * all the records in a particular set.
 * <p/>
 * <P>There are currently five flags that can be set:
 * <UL>
 * <LI>SHOW_ALL_MOLECULES, Index 0:  When set to 0, show only the first X molecules;  when set to
 * 1, show all molecules.
 * <LI>SHOW_ALL_CHILDREN, Index 1:  When set to 0, show only the first X children;  when set to
 * 1, show all children.
 * <LI>SHOW_ALL_PARENT_INTERACTIONS, Index 2:  When set to 0, show only the first X interaction
 * parents;  when set to 1, show all interaction parents.
 * <LI>SHOW_ALL_PARENT_COMPLEXES, Index 3:  When set to 0, show only the first X complex parents;
 * when set to 1, show all complex parents.
 * <LI>SHOW_ALL_PATHWAYS, Index 4:  When set to 0, show only the first X pathways;  when set to
 * 1, show all pathways.
 * </UL>
 *
 * @author Ethan Cerami
 */
public class BioPaxShowFlag implements Cloneable {

    /**
     * Default Number of Records to Show
     */
    public static final int DEFAULT_NUM_RECORDS = 20;

    /**
     * Name of URL Parameter that stores the Show/Hide Flags.
     */
    public static final String SHOW_FLAG = "show_flags";

    /**
     * Index 0.  When set to 0, show only the first X molecules;  when set to 1, show all molecules.
     */
    public static final int SHOW_ALL_MOLECULES = 0;

    /**
     * Index 1.  When set to 0, show only the first X children;  when set to 1, show all children.
     */
    public static final int SHOW_ALL_CHILDREN = 1;

    /**
     * Index 2.  When set to 0, show only the first X interaction parents;
     * when set to 1, show all interaction parents.
     */
    public static final int SHOW_ALL_PARENT_INTERACTIONS = 2;

    /**
     * Index 3.  When set to 0, show only the first X complex parents;
     * when set to 1, show all complex parents.
     */
    public static final int SHOW_ALL_PARENT_COMPLEXES = 3;

    /**
     * Index 4.  When set to 0, show only the first X pathways;
     * when set to 1, show all pathways.
     */
    public static final int SHOW_ALL_PATHWAYS = 4;

	///////////////////////////////////////////////////////////////////
	//
	// IF A NEW FLAG IS ADDED, MAKE SURE TO INC NUMBER_OF_FLAGS variable
	//
	///////////////////////////////////////////////////////////////////
	/**
	 * Number of flags
	 */
	private static final int NUMBER_OF_FLAGS = 5;
	

    /**
     * Stores current set of flags.
     */
    private int[] flags;

    /**
     * Empty Argument Constructor.
     */
    public BioPaxShowFlag() {
        flags = new int[NUMBER_OF_FLAGS];
    }

    /**
     * Constructor with String value.
     * Currently, the value is a three character string, where each character is 0 or 1, and the
     * character position refer to a SHOW flag.
     * For example, the string:  "10000" means:
     * <UL>
     * <LI>SHOW_ALL_MOLECULES = 1
     * <LI>SHOW_ALL_CHILDREN = 0
     * <LI>SHOW_ALL_PARENT_INTERACTIONS = 0
     * <LI>SHOW_ALL_PARENT_COMPLEXES = 0
     * <LI>SHOW_ALL_PATHWAYS = 0
     * </UL>
     *
     * @param value String value, e.g. "1000".
     */
    public BioPaxShowFlag(String value) {
        flags = new int[NUMBER_OF_FLAGS];
        if (value != null) {
            for (int i = 0; i < NUMBER_OF_FLAGS; i++) {
                if (value.charAt(i) == '1') {
                    flags[i] = 1;
                }
				else {
					flags[i] = 0;
				}
            }
        }
    }

    /**
     * Gets Flag at specified index position.
     *
     * @param flagIndex Flag Index Position.
     * @return 0 or 1
     */
    public int getFlag(int flagIndex) {
        return flags[flagIndex];
    }

    /**
     * Sets Flag at specified index position.
     *
     * @param flagIndex Flag Index Position.
     * @param value     0 or 1
     */
    public void setFlag(int flagIndex, int value) {
        flags[flagIndex] = value;
    }

    /**
     * Gets the URL Parameter.
     *
     * @return URL Name / Value Parameter.
     */
    public String getUrlParameter() {
        StringBuffer param = new StringBuffer(SHOW_FLAG + "=");
        for (int i = 0; i < NUMBER_OF_FLAGS; i++) {
            if (flags[i] == 1) {
                param.append("1");
            } else {
                param.append("0");
            }
        }
        return param.toString();
    }

    /**
     * Outputs an HTML Header.
     *
     * @param defaultNumRecords Default Number of Records to Show.
     * @param totalNumRecords   Total Number of Records in Set.
     * @param cPathId           cPath Identifier.
     * @param title             Section title.
     * @param showFlag          ShowFlag Object.
     * @param flagIndex         current flag index.
     * @return HTML String.
     */
    public static String createHtmlHeader(int defaultNumRecords, int totalNumRecords,
            long cPathId, String title, BioPaxShowFlag showFlag, int flagIndex,
            String anchorName) {
        StringBuffer html = new StringBuffer();
        html.append("<div class='h3'>");
        html.append("<h3>");
        html.append ("<A NAME='" + anchorName + "'>");
        html.append(title);
        html.append ("</A>");
        int end = determineEndIndex(defaultNumRecords, totalNumRecords, showFlag, flagIndex);

        if (totalNumRecords > 0) {
            html.append(" (Showing 1 - " + end + " of " + totalNumRecords + ")");
        }
        try {
            BioPaxShowFlag clone = (BioPaxShowFlag) showFlag.clone();
            String linkTitle = null;

            if (totalNumRecords > defaultNumRecords) {
                // generate link to change number of records to display
                if (showFlag.getFlag(flagIndex) == 1) {
                    clone.setFlag(flagIndex, 0);
                    linkTitle = "[display 1-" + defaultNumRecords + "]";
                } else {
                    clone.setFlag(flagIndex, 1);
                    linkTitle = "[display all]";
                }
            }
            if (linkTitle != null) {
                String url = generateUrl(cPathId, clone);
                String link = generateLink(url, linkTitle);
                html.append(link);
            }
        } catch (CloneNotSupportedException e) {
        }
        html.append("</h3>");
        html.append("</div>");
        return html.toString();
    }

    /**
     * Determines End Index for Pagination.
     *
     * @param defaultNumRecords Default Number of Records.
     * @param totalNumRecords   Total Number  of Records.
     * @param showFlag          BioPaxShowFlag Object.
     * @param flagIndex         Flag Index.
     * @return end index.
     */
    public static int determineEndIndex(int defaultNumRecords, int totalNumRecords,
            BioPaxShowFlag showFlag, int flagIndex) {
        if (showFlag.getFlag(flagIndex) == 1) {
            return totalNumRecords;
        } else {
            return Math.min(totalNumRecords, defaultNumRecords);
        }
    }

    private static String generateUrl(long cPathId, BioPaxShowFlag flag) {
        return "record.do?id=" + cPathId + "&" + flag.getUrlParameter();
    }

    private static String generateLink(String url, String title) {
        return "&nbsp;&nbsp;<A HREF=\"" + url + "\">" + title + "</A>";
    }

    /**
     * Creates a perfect clone of the BioPaxShowFlag Object.
     *
     * @return Perfect clone of the BioPaxShowFlag Object.
     * @throws CloneNotSupportedException Clone Error.
     */
    public Object clone() throws CloneNotSupportedException {
        BioPaxShowFlag clone = new BioPaxShowFlag();
        clone.setFlag(SHOW_ALL_MOLECULES, getFlag(SHOW_ALL_MOLECULES));
        clone.setFlag(SHOW_ALL_CHILDREN, getFlag(SHOW_ALL_CHILDREN));
        clone.setFlag(SHOW_ALL_PARENT_INTERACTIONS, getFlag(SHOW_ALL_PARENT_INTERACTIONS));
        clone.setFlag(SHOW_ALL_PARENT_COMPLEXES, getFlag(SHOW_ALL_PARENT_COMPLEXES));
        clone.setFlag(SHOW_ALL_PATHWAYS, getFlag(SHOW_ALL_PATHWAYS));
        return clone;
    }
}