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
package org.mskcc.pathdb.sql.assembly;

/**
 * Encapsulates a complete XML Document Assembly.
 * <P>
 * In cPath, we currently store XML document fragments.  For example, a
 * cPath interaction record contains an XML fragment describing the interaction,
 * and includes references to interactors.  However, each interactor is
 * specified with just a cPath ID, and you cannot determine the name,
 * description or organism of each interactor.
 * <p/>
 * Given an XML document fragment (or multiple fragments) you can, however
 * "assemble" all its consituent parts together, and create a single XML
 * document.  This complete XML document contains all interaction and
 * interactor data in one XML document, and is called a document "assembly."
 * <p/>
 * Having a document assembly is important for several reasons.  First, the
 * cPath Web Services API returns assembly documents containing all data
 * which matched the user's search critiera.  Second, assembly XML documents
 * are the atomic unit of data which we index in the Lucene Full Text Search
 * engine.
 * <p/>
 * As cPath evolves, we plan to support additional XML formats beyond just
 * PSI-MI.  The XmlAssembly interface, and its the associated XmlAssemblyFactory
 * therefore use the factory design pattern, and provide us with flexibility
 * to accomodate new XML formats in the future.
 *
 * @author Ethan Cerami
 */
public interface XmlAssembly {

    /**
     * Indicates that the Assembly is Empty (contains no data).
     *
     * @return true or false.
     */
    boolean isEmpty();

    /**
     * Gets Comlete Xml Assembly (in String form).
     *
     * @return XML Document String.
     */
    String getXmlString();

    /**
     * Gets Complete XML Assembly (in object form).
     *
     * @return Java Object encapsulating XML Document.
     */
    Object getXmlObject();

    /**
     * Gets Total Number of Hits.
     * This Assembly may be a subset of a larger set.
     * This method returns the total number of records in the larger,
     * complete set.
     *
     * @return int number of records.
     */
    int getNumHits();

    /**
     * Sets Total Number of Hits.
     * This Assembly may be a subset of a larger set.
     * This method returns the total number of records in the larger,
     * complete set.
     *
     * @param numRecords Total Number of Records.
     */
    void setNumHits(int numRecords);
}
