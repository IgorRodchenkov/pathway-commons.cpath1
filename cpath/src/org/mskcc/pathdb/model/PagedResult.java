package org.mskcc.pathdb.model;

/**
 * Interface for Paged Results.
 *
 * @author Ethan Cerami
 */
public interface PagedResult {

    /**
     * Sets the Start Index.
     * @param startIndex Start Index.
     */
    void setStartIndex (int startIndex);

    /**
     * Gets the Start Index.
     * @return startIndex int value.
     */
    int getStartIndex ();

    /**
     * Gets the URL for this request.
     * @return URL String.
     */
    String getUri();
}
