/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
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

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.*;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoExternalLink;
import org.mskcc.pathdb.xdebug.XDebug;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Diagnostic Tool for Testing Peformance of Various cPath Components.
 *
 * @author Ethan Cerami
 */
public class PerfTest {

    /**
     * Main Method.
     *
     * @param argv Command Line Arguments.
     * @throws Exception All Exceptions.
     */
    public static void main(String[] argv) throws Exception {
        //testXmlBinding();
        testMySqlIndexes();
    }

    private static void testMySqlIndexes() throws DaoException {
        XDebug xdebug = new XDebug();
        xdebug.startTimer();

        testExternalLinks();
        testExternalLinks();

        xdebug.stopTimer();
        System.out.println("\nTime Elapsed:  " + xdebug.getTimeElapsed());
    }

    private static void testExternalLinks() throws DaoException {
        for (int i = 0; i < 20; i++) {
            DaoExternalLink dao = new DaoExternalLink();
            ExternalReference externalRef = new ExternalReference("SWP",
                    "id" + i);
            ArrayList list = dao.lookUpByExternalRef(externalRef);
            if (i % 10 == 0) {
                System.out.print(".");
            }
        }
        System.out.println();
    }

    private static void testXmlBinding() throws IOException, MarshalException,
            ValidationException, DataServiceException {
        ContentReader reader = new ContentReader();
        String xml = reader.retrieveContent("testData/dip_psi.xml");
        //String xml = reader.retrieveContent("testData/dip20040203.mif");

        Date start = new Date();
        StringReader strReader = new StringReader(xml);
        EntrySet entrySet = EntrySet.unmarshalEntrySet(strReader);
        Date stop = new Date();

        long unmarshalTime = stop.getTime() - start.getTime();

        start = new Date();

        Entry entry = entrySet.getEntry(0);

        InteractorList interactorList = entry.getInteractorList();
        for (int i = 0; i < interactorList.getProteinInteractorCount(); i++) {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = new Marshaller(writer);
            marshaller.setValidation(false);
            ProteinInteractorType protein =
                    interactorList.getProteinInteractor(i);
            marshaller.marshal(protein);
            if (i % 100 == 0) {
                System.out.print(".");
            }
        }

        InteractionList interactionList = entry.getInteractionList();
        for (int i = 0; i < interactionList.getInteractionCount(); i++) {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = new Marshaller(writer);
            marshaller.setValidation(false);
            InteractionElementType interaction =
                    interactionList.getInteraction(i);
            marshaller.marshal(interaction);
            if (i % 100 == 0) {
                System.out.print(".");
            }
        }
        stop = new Date();

        long marshalTime = stop.getTime() - start.getTime();

        System.out.println("Unmarshal Time:  " + unmarshalTime);
        System.out.println("Marshal Time:  " + marshalTime);
    }
}
