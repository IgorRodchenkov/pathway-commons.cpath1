package org.mskcc.pathdb.lucene;

import org.jdom.JDOMException;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.sql.assembly.XmlAssembly;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import java.io.IOException;

/**
 * Factory class for creating ItemToIndex classes.
 *
 * @author Ethan Cerami
 */
public class IndexFactory {

    /**
     * Creates a new ItemToIndex Object based on XML Document.
     * @param cpathId cPath ID.
     * @param xmlAssembly XML Document Assembly.
     * @throws IOException Input Output Exception.
     * @return ItemToIndex Object.
     */
    public static ItemToIndex createItemToIndex (long cpathId,
            XmlAssembly xmlAssembly) throws IOException {
        ItemToIndex item = new PsiInteractionToIndex (cpathId, xmlAssembly);
        return item;
    }
}
