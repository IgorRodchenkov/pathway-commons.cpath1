// $Id: NeighborhoodMap.java,v 1.1 2008-12-10 16:49:04 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2008 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
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
package org.mskcc.pathdb.model;

/**
 * Class to encapsulate neighborhood map table record.
 *
 * @author Benjamin Gross
 */
public class NeighborhoodMap {

    private long cpathID;
    private int mapSize;

    /**
     * Gets the cpath record id.
     *
     * @return long
     */
    public long getCpathID() {
        return cpathID;
    }

    /**
     * Sets the cptah record id.
     *
     * @param cpathID long
     */
    public void setCpathID(long cpathID) {
        this.cpathID = cpathID;
    }

    /**
     * Gets the map size.
     *
     * @return int
     */
    public int getMapSize() {
        return mapSize;
    }

    /**
     * Sets the map size.
     *
     * @param mapSize int
     */
    public void setMapSize(int mapSize) {
        this.mapSize = mapSize;
    }
}
