package org.mskcc.pathdb.sql.assembly;

/**
 * Encapsulates a complete XML Document Assembly.
 * <P>
 * In cPath, we currently store XML document fragments.  For example, a
 * cPath interaction record contains an XML fragment describing the interaction,
 * and includes references to interactors.  However, each interactor is
 * specified with just a cPath ID, and you cannot determine the name,
 * description or organism of each interactor.
 *
 * Given an XML document fragment (or multiple fragments) you can, however
 * "assemble" all its consituent parts together, and create a single XML
 * document.  This complete XML document contains all interaction and
 * interactor data in one XML document, and is called a document "assembly."
 *
 * Having a document assembly is important for several reasons.  First, the
 * cPath Web Services API returns assembly documents containing all data
 * which matched the user's search critiera.  Second, assembly XML documents
 * are the atomic unit of data which we index in the Lucene Full Text Search
 * engine.
 *
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
     * @return true or false.
     */
    public boolean isEmpty();

    /**
     * Gets Comlete Xml Assembly (in String form).
     * @return XML Document String.
     */
    public String getXmlString ();

    /**
     * Gets Complete XML Assembly (in object form).
     * @return Java Object encapsulating XML Document.
     */
    public Object getXmlObject ();

    /**
     * Gets Total Number of Hits.
     * This Assembly may be a subset of a larger set.
     * This method returns the total number of records in the larger,
     * complete set.
     * @return int number of records.
     */
    public int getNumHits();

    /**
     * Sets Total Number of Hits.
     * This Assembly may be a subset of a larger set.
     * This method returns the total number of records in the larger,
     * complete set.
     * @param numRecords Total Number of Records.
     */
    public void setNumHits(int numRecords);
}
