package org.mskcc.pathdb.util.rdf;

/**
 * Misc RDF Utilities.
 *
 * @author Ethan Cerami
 */
public class RdfUtil {

    /**
     * Strips out leading hash mark #, if necessary.
     */
    public static String removeHashMark(String referenceId) {
        if (referenceId.startsWith("#")) {
            referenceId = referenceId.substring(1);
        }
        return referenceId;
    }

}
