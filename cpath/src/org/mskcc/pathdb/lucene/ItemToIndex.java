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
     * @return total number of fields to index.
     */
    public int getNumFields();

    /**
     * Gets Field at specified index.
     * @param index Index value.
     * @return Lucene Field Object.
     */
    public Field getField(int index);
}