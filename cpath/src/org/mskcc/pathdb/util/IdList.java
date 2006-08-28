// $Id: IdList.java,v 1.1 2006-08-28 18:11:36 grossb Exp $
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
package org.mskcc.pathdb.util;

// imports
import java.util.Iterator;

/**
 * Stores a list of ids in a memory efficient manner.
 * (a bit position is set if that id is in our list)
 *
 * Should not be used with sparse lists.
 *
 * @author Benjamin Gross
 */
public class IdList {

	// the number of ids we store
	private int numIds;

	// the byte array to store ids
	private byte[] byteArray;

	/**
	 * Constructor.
	 *
	 * @param numIds int
	 */
	public IdList(int numIds) {

		// check args
		if (numIds <= 0) {
			throw new IllegalArgumentException("RecordIdListCommand Not Recognized");
		}

		// set members
		this.numIds = numIds;

		// calc num bytes required to store numIds
		int numBytesRequired = numIds / 8;
		if (numIds % 8 > 0) ++numBytesRequired;
		this.byteArray = new byte[numBytesRequired];
	}

	/**
	 * Gets the number of ids stored in lis.
	 *
	 * @return int
	 */
	public int getNumIds() {
		return numIds;
	}

	/**
	 * Adds the given id to the list.
	 *
	 * @param id long
	 */
	public void addId(long id) {

		// check args
		if (id <= 0 || id > numIds) {
			throw new IllegalArgumentException("IdList.addId(): invalid id");
		}

		// determine byte index, bit position
		int byteIndex = byteIndex(id);
		byte bitPosition = bitPosition (id, byteIndex);

		// set bit on
		byteArray[byteIndex] |= (1 << bitPosition);
	}

	/**
	 * Returns true if id exists in list.
	 * 
	 * @param id long
	 * @return boolean
	 */
	public boolean idIsStored(long id) {

		// check args
		if (id <= 0 || id > numIds) {
			throw new IllegalArgumentException("IdList.idIsStored(): invalid id");
		}

		// determine byte index, bit position
		int byteIndex = byteIndex(id);
		byte bitPosition = bitPosition (id, byteIndex);
			
		// outta here
		return ((byteArray[byteIndex] & (1 << bitPosition)) > 0);
	}

	/**
	 * Returns an iterator for the list.
	 *
	 * @return Iterator
	 */
	public Iterator iterator() {
		return new IdListIterator();
	}

	/**
	 * Given a value, returns the byte index to store/retrieve value.
	 *
	 * @param id long
	 * @return int
	 */
	private int byteIndex (long id) {

		int byteNum = 0;

		if (id > 8) {
			byteNum = (int)(id / 8);
			if (id % 8 == 0) --byteNum;
		}

		// outta here
		return byteNum;
	}

	/**
	 * Given a value, returns the bit index to store/retrieve value.
	 *
	 * @param id long
	 * @param byteIndex int
	 * @return byte
	 */
	private byte bitPosition (long id, int byteIndex) {

		return (id > 8) ? (byte)(id - (8 * byteIndex) - 1) : (byte)--id;
	}

	/**
	 * Private class which iterates through the id list.
	 */
	private class IdListIterator implements Iterator {

		// current position in list
		private int currentPos;

		/**
		 * Constructor.
		 */
		private IdListIterator() {

			// init member vars
			this.currentPos = 0;
		}

		/**
		 * Our implementation of hasNext().
		 */
		public boolean hasNext() {
			return (currentPos < numIds);
		}

		/**
		 * Our implementation of next().
		 * This will return id's in sorted (ascending) order.
		 *
		 * @return Object (Integer)
		 */
		public Object next() {

			while ((++currentPos <= numIds) && !idIsStored(currentPos)){}           
			return (currentPos <= numIds) ? new Integer(currentPos) : null;
		}

		/**
		 * Our implementation of remove().
		 * (unsupported)
		 */
		public void remove() {
			throw new UnsupportedOperationException("IdList.remove() is not supported");
		}
	}
}