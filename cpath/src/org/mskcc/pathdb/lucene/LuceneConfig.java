package org.mskcc.pathdb.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.mskcc.dataservices.util.PropertyManager;


public class LuceneConfig {

    /**
     * Default Lucene Field for Storing All Terms.
     */
    public static final String FIELD_ALL = "all";

    /**
     * Lucene Field for Storing Intractor CPath ID.
     */
    public static final String FIELD_INTERACTOR_ID = "interactor_id";

    /**
     * Lucene Field for Storing Interaction CPath ID.
     */
    public static final String FIELD_INTERACTION_ID = "interaction_id";

    /**
     * Text Index Directory.
     */
    public static final String INDEX_DIR_PREFIX = "textIndex";

    /**
     * Lucene Directory System Property
     */
    public static final String PROPERTY_LUCENE_DIR = "lucene.dir";

    /**
     * Gets Directory for Full Text Indexer.
     *
     * @return Directory Location.
     */
    public static String getLuceneDirectory() {
        PropertyManager manager = PropertyManager.getInstance();
        String dir = manager.getProperty(PROPERTY_LUCENE_DIR);
        //  dir should only be null when run from the command line.
        if (dir == null) {
            String cPathHome = System.getProperty("CPATH_HOME");
            dir = cPathHome + "/build/WEB-INF/" + INDEX_DIR_PREFIX;
        }
        return dir;
    }

    /**
     * Gets Analyzer.
     * Index and Query must use the same Analyzer.
     *
     * @return Analyzer Object.
     */
    public static Analyzer getLuceneAnalyzer() {
        return new StandardAnalyzer();
    }
}
