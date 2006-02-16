/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 **/
/*
 * Created on 16-Mar-2005
 * Created By idk37697
 */
package org.mskcc.pathdb.test.lucene;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;

import junit.framework.TestCase;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.schemas.psi.Entry;
import org.mskcc.dataservices.schemas.psi.EntrySet;
import org.mskcc.pathdb.lucene.ConfigInfo;
import org.mskcc.pathdb.lucene.ConfigurableIndexCollector;

/**
 * Tests the Configurable Index Collector.
 *
 * @author idk37697
 */
public class TestConfigurableIndexCollector extends TestCase {

    private static String cpathRoot;

    /**
     * setup the root
     * @see junit.framework.TestCase#setUp()
     */
    public void setUp() {
        cpathRoot = System.getProperty("CPATH_HOME");
        if (cpathRoot == null) {
            cpathRoot = ".";
        }
    }

    /**
     *  test the configurable index collector
     */
    public void testConfigurableIndexCollector() {
        String testFile = cpathRoot + "/testData/psi_mi/dip_sample.xml";
        //String testFile = "testData/1000.xml";

        ConfigurableIndexCollector configurableIndexCollector = 
            new ConfigurableIndexCollector();

        ArrayList configInfoList = new ArrayList();

        try {
            //
            // collect the castor object
            //
            Mapping mapping = new Mapping();
            // Create a Reader to the file to unmarshal from
            FileReader reader = new FileReader(testFile);
            //  Create a new Unmarshaller
            Unmarshaller unmarshaller = new Unmarshaller(EntrySet.class);

            // Unmarshal the person object
            EntrySet entrySet = (EntrySet) unmarshaller.unmarshal(reader);
            assertNotNull("null entry set created!", entrySet);

            //
            // configure the index collector
            //
            //configurableIndexCollector.loadConfiguration();
            configInfoList
                    .add(new ConfigInfo(
                            "experimentDescription",
                            "interactionList/interaction/experimentList/"
                            + "experimentListItem/experimentDescription/id"));
            configInfoList.add(new ConfigInfo("fullName",
                    "interactorList/proteinInteractor/names/fullName"));
            configInfoList.add(new ConfigInfo("noEntrys",
                    "interactorList/proteinInteractor/noSuchPath"));

            configInfoList
                    .add(new ConfigInfo(
                            "experiment_type",
                            "interactionList/interaction/experimentList/"
                            + "experimentListItem/experimentDescription/names"
                            + "/shortLabel"));
            configurableIndexCollector.setConfigInfoList(configInfoList);

            //Index All Interactors and Interactions.
            StringBuffer interactorTokens;

            for (int i = 0; i < entrySet.getEntryCount(); i++) {
                ArrayList indexTokenList;
                String fieldName;
                String[] expectedFieldNames = {
                        "experimentDescription",
                        "fullName", "noEntrys", "experiment_type" };
                int[] expectedFieldCounts = { 
                        5, 2, 0, 0 };
                int expectedIterator = 0;
                int tokenCount;

                Entry entry = entrySet.getEntry(i);
                configurableIndexCollector.setContext(entry);
                while (configurableIndexCollector.next()) {
                    indexTokenList = configurableIndexCollector
                            .getIndexTokens();
                    fieldName = configurableIndexCollector.getIndexField();

                    tokenCount = indexTokenList.size();
                    assertEquals("incorrectfield name", fieldName,
                            expectedFieldNames[expectedIterator]);
                    assertEquals("incorrect number of tokens found",
                            tokenCount, expectedFieldCounts[expectedIterator]);
                    expectedIterator++;
                }
            }

        } catch (MarshalException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            assertTrue("Exception thrown [" + e + "]", false);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            assertTrue("Exception thrown [" + e + "]", false);
        } catch (ValidationException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            assertTrue("Exception thrown [" + e + "]", false);
        }

    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the Configurable Index Collector";
    }
}