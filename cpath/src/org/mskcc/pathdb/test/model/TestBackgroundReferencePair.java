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
package org.mskcc.pathdb.test.model;

import junit.framework.TestCase;
import org.mskcc.pathdb.model.BackgroundReferencePair;
import org.mskcc.pathdb.model.ReferenceType;

/**
 * Tests the Background Reference Pair Object.
 *
 * @author Ethan Cerami
 */
public class TestBackgroundReferencePair extends TestCase {

    /**
     * Tests the HashCode Generator for the Background Reference Pair Object.
     */
    public void testHashCode() {
        //  These two records are functionally equivalent
        BackgroundReferencePair record1 = new BackgroundReferencePair
                (1, "ABC", 2, "XYZ", ReferenceType.PROTEIN_UNIFICATION);
        BackgroundReferencePair record2 = new BackgroundReferencePair
                (2, "XYZ", 1, "ABC", ReferenceType.PROTEIN_UNIFICATION);

        //  They should therefore result in identical hash codes
        assertTrue(record1.hashCode() == record2.hashCode());
    }


    /**
     * Gets Name of Test.
     * @return Name of Test.
     */
    public String getName() {
        return "Test the Background Reference Pair Data Model Object";
    }
}
