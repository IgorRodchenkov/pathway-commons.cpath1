package org.mskcc.pathdb.test.sql;

import junit.framework.TestCase;
import org.mskcc.dataservices.bio.Interaction;
import org.mskcc.dataservices.bio.vocab.InteractionVocab;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.dao.DaoImport;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.transfer.TransferImportToCPath;
import org.mskcc.pathdb.sql.query.InteractionQuery;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Tests Data Import and Transfer with DIP Data.
 *
 * @author Ethan Cerami.
 */
public class TestTransfersWithDipData extends TestCase {

    /**
     * Tests Import and Transfer with DIP Data.
     * @throws Exception All Exceptions.
     */
    public void testDipData() throws Exception {
        RegisterCPathServices.registerServices();
        //  Add DIP data to import table
        addDipDataToImportTable();

        //  Transfer DIP data to GRID
        TransferImportToCPath transfer = new TransferImportToCPath
                (false, null);
        transfer.transferData();

        //  Verify Contents
        InteractionQuery query = new InteractionQuery("P06139");
        ArrayList interactions = query.getInteractions();

        assertEquals(5, interactions.size());

        validateInteraction(0, interactions, "Genetic", "11821039");
        validateInteraction(1, interactions, "x-ray crystallography",
                "9174345");
        validateInteraction(2, interactions, "x-ray crystallography",
                "9174345");
        validateInteraction(3, interactions, "x-ray crystallography",
                "10587438");
        validateInteraction(4, interactions, "x-ray crystallography",
                "10089390");
    }

    private void validateInteraction(int index, ArrayList interactions,
            String expectedSystem, String expectedPmid) {
        Interaction interaction = (Interaction) interactions.get(index);
        String system = (String) interaction.getAttribute
                (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
        String pmid = (String) interaction.getAttribute
                (InteractionVocab.PUB_MED_ID);
        assertEquals(expectedSystem, system);
        assertEquals(expectedPmid, pmid);
    }

    /**
     * Gets Sample PSI File from local directory.
     */
    private void addDipDataToImportTable() throws IOException, DaoException {
        File file = new File("testData/dip_sample.xml");
        ContentReader reader = new ContentReader();
        String data = reader.retrieveContentFromFile(file);
        DaoImport dbImport = new DaoImport();
        dbImport.addRecord(data);
    }
}