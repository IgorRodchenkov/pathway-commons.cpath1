package org.mskcc.pathdb.sql.transfer;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.service.RegisterCPathServices;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.sql.dao.DaoInteractor;
import org.mskcc.pathdb.util.BatchTool;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Transfers External Links.
 *
 * @author Ethan Cerami
 */
public class TransferExternalLinks extends BatchTool {
    private int counter = 0;

    /**
     * Constructor.
     * @param runningFromCommandLine are we running from the command line.
     * @param xdebug XDebug Object.
     */
    public TransferExternalLinks(boolean runningFromCommandLine,
            XDebug xdebug) {
        super(runningFromCommandLine, xdebug);
    }

    /**
     * Transfers Data.
     * @throws DaoException Error Retrieving Data.
     * @throws java.io.IOException Input Output Exception.
     */
    public void transferData() throws DaoException, IOException {
        DaoInteractor daoInteractor = new DaoInteractor();
        ArrayList interactors = daoInteractor.getAllInteractors();
        for (int i = 0; i < interactors.size(); i++) {
            Interactor interactor = (Interactor) interactors.get(i);
            saveNewLinks(interactor);
        }
        this.outputMsg("Total Number of New Links created:  " + counter);
    }

    private void saveNewLinks(Interactor interactor) throws DaoException,
            IOException {
        DaoExternalDb dbTable = new DaoExternalDb();
        DaoExternalLink linkTable = new DaoExternalLink();

        ExternalReference refs[] = interactor.getExternalRefs();
        for (int i = 0; i < refs.length; i++) {
            String term = refs[i].getDatabase();
            String linkedToId = refs[i].getId();
            ExternalDatabaseRecord externalDb = dbTable.getRecordByTerm(term);
            if (externalDb == null) {
                outputMsg("Error:  Could not locate database for Interactor:  "
                        + interactor.getName() + " -->"
                        + term + " [" + linkedToId + "]");
            } else {
                ExternalLinkRecord link = new ExternalLinkRecord();
                String localId = (String) interactor.getAttribute
                        (InteractorVocab.LOCAL_ID);
                link.setCpathId(Integer.parseInt(localId));
                link.setExternalDatabase(externalDb);
                link.setLinkedToId(linkedToId);
                if (!linkTable.recordExists(link)) {
                    linkTable.addRecord(link);
                    counter++;
                }
            }
        }
    }

    /**
     * Main method.
     * @param args Command Line Argument.
     * @throws java.lang.Exception All Exceptions.
     */
    public static void main(String[] args) throws Exception {
        try {
            if (args.length > 0) {
                PropertyManager manager = PropertyManager.getInstance();
                manager.setProperty(PropertyManager.DB_LOCATION, args[0]);
            } else {
                System.out.println("Command line usage:  TransferExternalLinks "
                        + "host_name [datafile]");
            }
            RegisterCPathServices.registerServices();
            TransferExternalLinks transfer =
                    new TransferExternalLinks(true, null);
            transfer.transferData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}