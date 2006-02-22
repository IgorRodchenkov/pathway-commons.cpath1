// $Id: TabSpaceTokenizer.java,v 1.4 2006-02-22 22:47:51 grossb Exp $
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
package org.mskcc.pathdb.sql.references;

import java.util.Enumeration;

/**
 * Tokenizer for parsing Tab/Space delimited files.
 * <P>
 * This tokenizer assumes two different levels of delimiters.
 * <UL>
 * <LI>At the first level, tabs are used to separate columns.
 * <LI>At the second level, spaces are used to separate individual tokens.
 * </UL>
 * Here is an example file:
 * (see:  testData/references/unification_refs.txt):
 * <TABLE>
 * <TR>
 * <TH ALIGN=LEFT>UniProt</TH>
 * <TH ALIGN=LEFT>PIR</TH>
 * <TH ALIGN=LEFT>HUGE</TH>
 * </TR>
 * <TR>
 * <TD>UNIPROT_1234</TD>
 * <TD>PIR_1234 PIR_4321</TD>
 * <TD>HUGE_1234</TD>
 * </TR>
 * <TR>
 * <TD>UNIPROT_XYZ</TD>
 * <TD>PIR_XYZ</TD>
 * <TD>HUGE_XYZ</TD>
 * </TR>
 * <TR>
 * <TD>UNIPROT_1234</TD>
 * <TD></TD>
 * <TD>HUGE_4321</TD>
 * </TR>
 * </TABLE>
 * <P>
 * The tokenizer enables you to easily get all the tokens sequentially within
 * a line of text, but it also enables you to easily identify which column the
 * specific token came from.  Therefore, when reading the first line of the
 * file above, the tokens appear as: UNIPROT_1234, PIR_1234, PIR_4321 and
 * HUGE_1234.  However, you can also query each token to determine that token0
 * belongs to column 0 (UNIPROT), but that both tokens 1 and 2 belong to
 * column 1 (PIR).
 * <P>
 * If the input line contains no tabs, the tokenizer just assumes that
 * there is only one column, and will tokenize based on space characters.
 * <P>
 *
 * @author Ethan Cerami
 */
public class TabSpaceTokenizer implements Enumeration {
    private int currentTabIndex;
    private int currentSpaceIndex;
    private String tabs[];
    private String tab;
    private String tokens[];

    /**
     * Constructor.
     *
     * @param line Line of Data.
     */
    public TabSpaceTokenizer(String line) {
        currentTabIndex = 0;
        currentSpaceIndex = 0;
        tabs = line.split("\t");

        //  Jump to first non-empty tab
        tab = tabs[currentTabIndex];
        while (tab.trim().length() == 0 && currentTabIndex <= tabs.length) {
            tab = tabs[++currentTabIndex];
        }

        //  Split based on whitespace inside the column
        tokens = tab.split("\\s+");
    }

    /**
     * Gets Next Token.
     *
     * @return IndexedToken Object.
     */
    public Object nextElement() {
        if (currentSpaceIndex >= tokens.length) {
            //  Jump to next non-empty tab
            tab = tabs[++currentTabIndex];
            while (tab.trim().length() == 0 && currentTabIndex <= tabs.length) {
                tab = tabs[++currentTabIndex];
            }
            tokens = tab.split("\\s+");
            currentSpaceIndex = 0;
        }
        return new IndexedToken
                (tokens[currentSpaceIndex++], currentTabIndex);

    }

    /**
     * Determines if tokenizer has more elements.
     *
     * @return boolean value.
     */
    public boolean hasMoreElements() {
        if (currentSpaceIndex < tokens.length) {
            return true;
        } else {
            return (currentTabIndex + 1 < tabs.length);
        }
    }
}
