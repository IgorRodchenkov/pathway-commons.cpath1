// $Id: ItemToIndex.java,v 1.7 2009-07-20 13:57:47 cerami Exp $
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
package org.mskcc.pathdb.lucene;

import org.apache.lucene.document.Field;

/**
 * Interface for encapsulating a data item scheduled for indexing
 * in the Lucene search engine. Each item to be indexed can have 0 or more
 * indexable fields.
 *
 * @author Ethan Cerami
 */
public interface ItemToIndex {

    /**
     * Gets Total Number of Fields to Index.
     *
     * @return total number of fields to index.
     */
    int getNumFields();

    /**
     * Gets Field at specified index.
     *
     * @param index Index value.
     * @return Lucene Field Object.
     */
    Field getField(int index);

    /**
     * Gets the Document Boost Factor;  Default is 1.0.
     *
     * @return Document Boost Factor.
     */
    float getBoost();
}
