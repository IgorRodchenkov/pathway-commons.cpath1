// $Id: ConfigurableIndexCollector.java,v 1.5 2006-02-22 22:47:50 grossb Exp $
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
/*
 * Created on 16-Mar-2005
 * Created By idk37697
 */
package org.mskcc.pathdb.lucene;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.mskcc.dataservices.schemas.psi.Entry;

/**
 * Class uses JXPath libraries to tree walk an
 * org.mskcc.dataservices.schemas.psi.Entry object. An understanding of the
 * Entry object and children objects is needed to correctly create XPath
 * expressions. Please view the Javadoc for these before creating Xpath
 * expressions <br>
 * <br>
 * 
 * XPath expressions are used to define which tokens to collect. The xpath
 * expressions are routed at the Entry object so need to be below that. <br>
 * <br>
 * 
 * valid example xpath expressions are: <br>
 * <li>
 * interactionList/interaction/experimentList/experimentListItem/
 *            experimentDescription/id
 * </li>
 * <li>interactorList/proteinInteractor/names/fullName</li>
 * <br>
 * <br>
 * This class acts as a container class in a similar way to a jdbc resultSet. ie
 * the 'next' method is used to move the internal iterator on to the next
 * result. <br>
 * <br>
 * 
 * Configureation must be introduced and the context set before iterating can
 * commence. Properties may only be loaded once subsequent loads will not
 * collect propertes again.
 * 
 * @author idk37697
 */
public class ConfigurableIndexCollector {

    private static final String CONFIG_FILENAME = "indexConfig.properties";

    private static ArrayList configInfoList = null;

    private Iterator infoIterator;

    private ConfigInfo currentConfigInfo;
    
    private JXPathContext context;

    private static boolean alreadyLoaded = false;

    // constants
    private static final int MAX_PROPERTIES = 50;

    private static boolean debug = true;

    private static final String PROP_XPATH = "xpath";

    private static final String PROP_FIELD = "field";

 
    static {
        initialise();
    }
    
    /**
     * Static initialiser to setup the properties to be used by all instances
     */
    public static void initialise() {
        Properties properties = new Properties();
        configInfoList = new ArrayList();
        String cpathHome = System.getProperty("CPATH_HOME");
        String configLocation;

        if (cpathHome != null) {
            // hardcoded path
            configLocation = cpathHome + "/bin/" + CONFIG_FILENAME;
        } else {
            configLocation = CONFIG_FILENAME;
        }
        try {
            properties.load(new FileInputStream(configLocation));
            String debugString = properties.getProperty("debug");
            debug = (debugString != null && debugString
                    .equalsIgnoreCase("true"));

            // fetch all of the config
            String xpath;
            String field;
            for (int i = 0; i < MAX_PROPERTIES; i++) {
                xpath = properties.getProperty(PROP_XPATH + i);
                field = properties.getProperty(PROP_FIELD + i);
                if (xpath != null && field != null) {
                    configInfoList.add(new ConfigInfo(field, xpath));
                }
            }

        } catch (IOException e) {
            warn("Cannot collect indexing configuration file: "
                    + CONFIG_FILENAME + "[" + e.getMessage() + "]");
            configInfoList = null;
        }
    }

    /**
     * @return true if this object has been enabled with more than 0 commands
     */
    public boolean enabled() {
        return configInfoList != null && configInfoList.size() > 1;
    }

    /**
     * Configuration details must be loaded before use
     */
    public ConfigurableIndexCollector() {
    }

    /**
     * adds a config entry for interactors
     * 
     * @param xpath -
     *            an xpath entry relative to the entry element, for example
     *            interactorList/proteinInteractor/names/fullName
     * @param indexField -
     *            the index field to be used for the recoverd values
     */
    public void addConfigEntry(String xpath, String indexField) {
        configInfoList.add(new ConfigInfo(xpath, indexField));
    }

    /**
     * Initialises the class to search over the given entry object. This MUST be
     * done before any searching can commence
     * 
     * @param entry A PSI-MI Entry Object
     */
    public void setContext(Entry entry) {
        context = JXPathContext.newContext(entry);
        resetIterator();
    }

    /**
     * moves the internal iterator on to the next result. NOTE: this starts
     * before the first item, so looping with next() will work.
     * 
     * @return true if there are more elements to collect
     */
    public boolean next() {
        boolean returnBoolean;
        if (infoIterator != null && infoIterator.hasNext()) {
            currentConfigInfo = (ConfigInfo) infoIterator.next();
            returnBoolean = true;
        } else {
            returnBoolean = false;
        }
        return returnBoolean;
    }

    /**
     * return the internal iterator to the start position
     */
    public void resetIterator() {
        infoIterator = configInfoList.iterator();
    }

    /**
     * get the list of tokens collected from the entry
     * 
     * @return ArrayList of Strings
     */
    public ArrayList getIndexTokens() {
        ArrayList returnTokens = new ArrayList();

        try {
            if (currentConfigInfo != null) {
                Iterator fNameIterator = context
                        .iterate(currentConfigInfo.getXpath());
                debug("Path: " + currentConfigInfo.getXpath());
                while (fNameIterator.hasNext()) {
                    // force entry to be turned into a string, to convert
                    // Integers and Strings alike
                    String element = "" + fNameIterator.next();

                    if (element != null && !element.trim().equals("")) {
                        debug("\t[" + element + "]");
                        returnTokens.add(element.trim());
                    }
                }
            }
        } catch (JXPathException e) {
            warn(e.getMessage());
        }
        return returnTokens;
    }

    /**
     * @return get the name of the field to use in the index
     */
    public String getIndexField() {
        return currentConfigInfo.getFieldName();
    }

    /**
     * local debug msg.
     * 
     * @param msg
     */
    private static void debug(String msg) {
        if (debug) {
            System.out.println("[DEBUG] " + msg);
        }
    }

    /**
     * local warning mesage
     * 
     * @param msg
     */
    private static void warn(String msg) {
        System.out.println("[WARN] " + msg);
    }

    /**
     * @return Returns the configInfoList.
     */
    public static ArrayList getConfigInfoList() {
        return configInfoList;
    }

    /**
     * @param configInfoList
     *            The configInfoList to set.
     */
    public static void setConfigInfoList(ArrayList configInfoList) {
        ConfigurableIndexCollector.configInfoList = configInfoList;
    }
}
