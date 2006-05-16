package org.mskcc.pathdb.test.schemas.biopax;/**
 * This class illustrates how to use the validator
 * to validate a BioPAX OWL file.
 *
 * To invoke this:
 *    java -DCLASSPATH=<path_to_biopaxvalidator.jar> validatorDemo  <path_to_biopax_file>
 *
 */
import java.io.File;
import java.util.ArrayList;
import edu.ohsu.oci.isr.biopax.validator.*;
import edu.ohsu.oci.isr.biopax.validator.error.*;

public class TestValidator {
    public static void main(String args[]) {

        int i = 0;
        File ff = new File(args[0]);
        System.out.println("Validating " + args[0]);

        // instantiating the validator
        validate v = new validate();

        // setting  properties

        // set log file location
//        v.setLogFile("mylog");
//        // set log to less verbose
//        v.setLogVerbosity(false);
//        // set to check for presence of onotology
//        v.setOntologyCheck(true);
//        // set location of ontology
//        v.setOntologyLocation("http://www.biopax.org/release/biopax-level1.owl");
//        // set to check tags
//        v.setCheckTags(true);

        // validate the given file and return errors
        ArrayList errs = v.analyze(ff);

        System.out.println("Total # of Errors:  " + errs.size());
        while (i < errs.size()) {
            // fetch error objects
            errObj eo = (errObj) errs.get(i);
            // print out error message
            System.out.println("["+eo.getErrorMessage()+"]");
            i++;
        }
    }
}
