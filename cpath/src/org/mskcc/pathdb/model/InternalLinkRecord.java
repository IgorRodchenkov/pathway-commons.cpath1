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
package org.mskcc.pathdb.model;

/**
 * JavaBean to Encapsulate an Import Record.
 *
 * @author Ethan Cerami
 */
public class InternalLinkRecord {
    private long id;
    private long cpathIdA;
    private long cpathIdB;

    /**
     * No-arg Constructor.
     */
    public InternalLinkRecord() {
    }

    /**
     * Constructor.
     *
     * @param cpathIdA CPath ID of Entity A.
     * @param cpathIdB CPath ID of Entity B.
     */
    public InternalLinkRecord(long cpathIdA, long cpathIdB) {
        this.cpathIdA = cpathIdA;
        this.cpathIdB = cpathIdB;
    }

    /**
     * Gets Primary Internal Link ID.
     *
     * @return Primary Internal Link ID.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets Primary Internal Link ID.
     *
     * @param id Primary Internal Link ID.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets CPath ID of Entity A.
     *
     * @return cpath ID.
     */
    public long getCpathIdA() {
        return cpathIdA;
    }

    /**
     * Sets CPath ID of Entity A.
     *
     * @param cpathIdA cpathID of Entity A.
     */
    public void setCpathIdA(long cpathIdA) {
        this.cpathIdA = cpathIdA;
    }

    /**
     * Gets CPath ID of Entity B.
     *
     * @return cpath ID.
     */
    public long getCpathIdB() {
        return cpathIdB;
    }

    /**
     * Sets CPath ID of Entity B.
     *
     * @param cpathIdB cpathID of Entity B.
     */
    public void setCpathIdB(long cpathIdB) {
        this.cpathIdB = cpathIdB;
    }
}