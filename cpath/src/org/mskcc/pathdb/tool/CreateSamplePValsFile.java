// $Id: CreateSamplePValsFile.java,v 1.2 2006-06-09 19:22:04 cerami Exp $
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
package org.mskcc.pathdb.tool;

import org.jdom.Element;
import org.mskcc.pathdb.schemas.biopax.BioPaxConstants;
import org.mskcc.pathdb.schemas.biopax.BioPaxUtil;
import org.mskcc.pathdb.task.ProgressMonitor;
import org.mskcc.pathdb.util.rdf.RdfQuery;

import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * This is a hacky, one-off script, used to generate the sample.pvals file
 * that is currently checked into:  web/jsp/cytoscape/expressionData.
 * <p/>
 * If you need to generate a new sample.pvals, here's how to use this class.
 * <UL>
 * <LI>Run CreateSamplePValsFile.  On the command line argument specify a BioPAX
 * file that you have obtained from cPath.
 * <LI>Program will output a list of Affy IDs with fake expression data to System.out.
 * <LI>Copy and paste this data into a text file.
 * <LI>Open text file in MS Excel.
 * <LI>Add headers.
 * </UL>
 *
 * @author Ethan Cerami
 */
public class CreateSamplePValsFile {
    private static DecimalFormat formatter = new DecimalFormat("###.####");

    /**
     * Hacky Script to Create a Sample Cytoscape Pvals File.
     *
     * @param args Specify a BioPAX File obtained from cPath.
     * @throws Exception All Errors.
     */
    public static void main(String[] args) throws Exception {

        // Init Random Number Generator
        Random generator = new Random();

        //  Get File name from command line (no error checking provided here...)
        File file = new File(args[0]);
        FileReader reader = new FileReader(file);

        //  Read into BioPAX Util
        BioPaxUtil bpUtil = new BioPaxUtil(reader, false, new ProgressMonitor());
        HashMap rdfMap = bpUtil.getRdfResourceMap();

        //  Create RDF Query Object
        RdfQuery query = new RdfQuery(rdfMap);
        ArrayList peList = bpUtil.getPhysicalEntityList();

        //  Process All Physical Entities
        processPhysicalEntities(peList, query, generator);
    }

    private static void processPhysicalEntities(ArrayList peList, RdfQuery query,
            Random generator) {
        for (int i = 0; i < peList.size(); i++) {
            Element pe = (Element) peList.get(i);

            //  Get Names and XREFs
            Element shortNameElement = query.getNode(pe, "SHORT-NAME");
            Element nameElement = query.getNode(pe, "NAME");
            ArrayList xrefList = query.getNodes(pe, "XREF/relationshipXref");

            //  Flag Ensures that we only extract one AFFY_ID
            boolean gotOne = false;
            for (int j = 0; j < xrefList.size(); j++) {
                Element xref = (Element) xrefList.get(j);

                //  Extract First Affy ID Only.
                String db = xref.getChildText("DB",
                        BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE);
                if (db != null && db.equalsIgnoreCase("AFFYMETRIX") && !gotOne) {
                    String id = xref.getChildText("ID",
                            BioPaxConstants.BIOPAX_LEVEL_2_NAMESPACE);
                    System.out.print(id);

                    //  Print out Physical Entity Name
                    if (shortNameElement != null) {
                        System.out.print("\t" + shortNameElement.getTextNormalize());
                    } else if (nameElement != null) {
                        System.out.print("\t" + nameElement.getTextNormalize());
                    } else {
                        System.out.print("NA");
                    }
                    //  Generate Random Expression data
                    generateRandomExpressionData(generator);
                    System.out.println();
                    gotOne = true;
                }
            }
        }
    }

    private static void generateRandomExpressionData(Random generator) {
        for (int k = 0; k < 4; k++) {
            double value = generator.nextDouble() * 6.0;
            System.out.print("\t" + formatter.format(value));
        }
    }
}
