// $Id: TestIdList.java,v 1.1 2006-08-28 18:13:35 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
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
package org.mskcc.pathdb.test.util;

// imports
import java.util.Iterator;
import org.mskcc.pathdb.util.IdList;


import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.framework.TestResult;

/**
 * This tests that the IdList class works properly.
 *
 * @author Benjamin Gross
 */
public class TestIdList extends TestCase {

	/**
	 * Dynamically adds all methods as tests that begin with 'test'
	 */
    public static Test suite() {
        return new TestSuite(TestIdList.class);
    }

	/**
	 * The big deal main - if we want to run from command line.
	 */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }

	/**
	 * Tests that the IdList class works properly.
	 */
    public void testJIdList() {

		// create an instance of id list
		IdList idList = new IdList(27);
		
		// grab a list iterator
		int lc;
		byte [] values = {3, 15, 16, 27};

		// store some id's
		for (lc = 0; lc < values.length; lc++) {
			idList.addId(values[lc]);
		}

		// retrieve the ids
		lc = -1;
		for (Iterator i = idList.iterator(); i.hasNext();) {
			Assert.assertEquals(values[++lc], (byte)(((Integer)i.next()).intValue()));
		}
		Assert.assertEquals(lc, 3);
	}
}