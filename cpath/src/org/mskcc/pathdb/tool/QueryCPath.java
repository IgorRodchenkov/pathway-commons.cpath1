package org.mskcc.pathdb.tool;

import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.pathdb.sql.query.InteractionQuery;

import java.io.StringWriter;

/**
 * Command Line Tool for Querying CPath.
 *
 * @author Ethan Cerami.
 */
public class QueryCPath {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
    public static void main(String[] args) {
        if (args.length == 1) {
            queryCPath(args);
        } else {
            System.out.println("Command line usage:  queryCPath.sh"
                    + " interactor_name");
        }
    }

    /**
     * Executes Query against CPath.
     * @param args Command Line Arguments.
     */
    private static void queryCPath(String[] args) {
        try {
            InteractionQuery query = new InteractionQuery(args[0]);
            EntrySet entrySet = query.getEntrySet();
            StringWriter writer = new StringWriter();
            entrySet.marshal(writer);
            System.out.println(writer.toString());
        } catch (Exception e) {
            System.out.println("\n!!!!  An Error Has Occurred!");
            System.out.println("-->  " + e.getMessage());
        }
    }
}