/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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

/**
 * Encapsulates a Token with its Column Number.
 * Used in conjunction with TabSpaceTokenizer.
 *
 * @author Ethan Cerami.
 */
public class IndexedToken {
    private String token;
    private int columnNumber;

    /**
     * Constructor.
     * @param token Token String.
     * @param columnNumber Column Number.
     */
    public IndexedToken(String token, int columnNumber) {
        this.token = token;
        this.columnNumber = columnNumber;
    }

    /**
     * Gets Token String.
     * @return token String.
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets Token String.
     * @param token token String.
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets Column Number.
     * @return column number.
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * Sets Column Number.
     * @param columnNumber column number.
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * Gets Text Representation of Token.
     * @return text representation.
     */
    public String toString() {
        return "Token:  " + token + ", column number:  " + columnNumber;
    }
}