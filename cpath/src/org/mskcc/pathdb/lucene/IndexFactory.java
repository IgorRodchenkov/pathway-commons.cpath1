package org.mskcc.pathdb.lucene;

import org.mskcc.pathdb.sql.assembly.XmlAssembly;

import java.io.IOException;

/**
 * Factory class for creating ItemToIndex classes.
 *
 * @author Ethan Cerami
 */
public class IndexFactory {

    /**
     * Creates a new ItemToIndex Object based on XML Document.
     *
     * @param cpathId     cPath ID.
     * @param xmlAssembly XML Document Assembly.
     * @return ItemToIndex Object.
     * @throws IOException Input Output Exception.
     */
    public static ItemToIndex createItemToIndex(long cpathId,
            XmlAssembly xmlAssembly) throws IOException {
        ItemToIndex item = new PsiInteractionToIndex(cpathId, xmlAssembly);
        return item;
    }
}
