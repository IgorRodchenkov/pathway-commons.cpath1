package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.task.ImportReferencesTask;

import java.io.FileReader;
import java.io.IOException;

/**
 * Loads External References from text file.
 *
 * @author Ethan Cerami
 */
public class LoadExternalReferences {

    /**
     * Loads External References from Specified File.
     *
     * @param fileName File Name.
     * @throws IOException  File Input Error.
     * @throws DaoException Data Access Error.
     */
    public void load(String fileName) throws DaoException, IOException {
        FileReader reader = new FileReader(fileName);
        ImportReferencesTask task =
                new ImportReferencesTask(true, reader);
        task.importReferences();
    }
}