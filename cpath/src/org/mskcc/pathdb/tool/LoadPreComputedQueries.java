package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.sql.query.QueryFileReader;
import org.mskcc.pathdb.sql.query.ExecuteQuery;
import org.mskcc.pathdb.sql.query.QueryException;
import org.mskcc.pathdb.sql.dao.DaoXmlCache;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.controller.ProtocolRequest;
import org.mskcc.pathdb.controller.ProtocolValidator;
import org.mskcc.pathdb.controller.ProtocolException;
import org.mskcc.pathdb.controller.NeedsHelpException;
import org.mskcc.pathdb.xdebug.XDebug;

import java.util.ArrayList;
import java.util.Date;
import java.io.IOException;

/**
 * Loads PreComputed Queries.
 *
 * @author Ethan Cerami
 */
public class LoadPreComputedQueries {
    XDebug xdebug = new XDebug();

    /**
     * Processes all Queries in Specified File.
     * @param file File Name.
     */
    public void process (String file) {
        QueryFileReader reader = new QueryFileReader();
        try {
            System.out.println("START:  " + new Date());
            System.out.println("Clearing Database Cache");
            DaoXmlCache dao = new DaoXmlCache();
            dao.deleteAllRecords();
            ArrayList list = reader.getProtocolRequests(file);
            for (int i=0; i<list.size(); i++) {
                ProtocolRequest request = (ProtocolRequest) list.get(i);
                ProtocolValidator validator = new ProtocolValidator(request);
                validator.validate();
                System.out.print("Running Query:  " + request.getUri());
                ExecuteQuery executeQuery = new ExecuteQuery (xdebug);
                executeQuery.executeAndStoreQuery(request);
                System.out.println(" -->  OK");
            }
            System.out.println("DONE:  " + new Date());
        } catch (IOException e) {
            System.out.println("\n!!!!  Script aborted due to error!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        } catch (QueryException e) {
            System.out.println("\n!!!!  Script aborted due to error!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        } catch (ProtocolException e) {
            System.out.println("\n!!!!  Script aborted due to error!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        } catch (NeedsHelpException e) {
            System.out.println("\n!!!!  Script aborted due to error!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        } catch (DaoException e) {
            System.out.println("\n!!!!  Script aborted due to error!");
            System.out.println("-->  " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length > 0) {
            LoadPreComputedQueries loader = new LoadPreComputedQueries();
            loader.process (args[0]);
        } else {
            displayUsage();
        }
    }

    /**
     * Displays Command Line Usage.
     */
    public static void displayUsage() {
        System.out.println("Command line usage:  loadQueries.sh filename");
    }
}