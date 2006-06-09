// $Id: TestValidator.java,v 1.2 2006-06-09 19:22:04 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.test.schemas.biopax;

import edu.ohsu.oci.isr.biopax.validator.error.errObj;
import edu.ohsu.oci.isr.biopax.validator.validate;

import java.io.File;
import java.util.ArrayList;

/**
 * Tests the OHSU BioPAX Validator.
 * [Currently Under Construction]
 *
 * @author Ethan Cerami.
 */
public class TestValidator {

    /**
     * Main Method.
     * @param args Command Line Arguments.
     */
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
            System.out.println("[" + eo.getErrorMessage() + "]");
            i++;
        }
    }
}
