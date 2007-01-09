// $Id: WebUIBean.java,v 1.10 2007-01-09 17:25:57 cerami Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Benjamin Gross, Gary Bader, Chris Sander
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
package org.mskcc.pathdb.form;

// imports

import org.apache.struts.action.ActionForm;

/**
 * Struts ActionForm for updating/retrieving web ui elements.
 *
 * @author Benjamin Gross
 */
public class WebUIBean extends ActionForm {

    /**
     * Application Name.
     */
    private String applicationName;

    /**
     * Display Browse by Pathway Tab.
     */
    private boolean displayBrowseByPathwayTab;

    /**
     * Display Browse by Organism Tab.
     */
    private boolean displayBrowseByOrganismTab;

    /**
     * Display Web Service Tab.
     */
    private boolean displayWebServiceTab;

    /**
     * Display Cytoscape Tab.
     */
    private boolean displayCytoscapeTab;

    /**
     * Sets the Application Name.
     *
     * @param applicationName String.
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * Gets the Application Name.
     *
     * @return applicationName.
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Sets the Display Browse by Pathway Tab.
     *
     * @param displayBrowseByPathwayTab boolean.
     */
    public void setDisplayBrowseByPathwayTab(boolean displayBrowseByPathwayTab) {
        this.displayBrowseByPathwayTab = displayBrowseByPathwayTab;
    }

    /**
     * Gets the Display Browse by Pathway Tab.
     *
     * @return displayBrowseByPathwayTab.
     */
    public boolean getDisplayBrowseByPathwayTab() {
        return displayBrowseByPathwayTab;
    }

    /**
     * Sets the Display Browse by Organism Tab.
     *
     * @param displayBrowseByOrganismTab boolean.
     */
    public void setDisplayBrowseByOrganismTab(boolean displayBrowseByOrganismTab) {
        this.displayBrowseByOrganismTab = displayBrowseByOrganismTab;
    }

    /**
     * Gets the Display Browse by Organism Tab.
     *
     * @return displayBrowseByOrganismTab.
     */
    public boolean getDisplayBrowseByOrganismTab() {
        return displayBrowseByOrganismTab;
    }

    /**
     * Sets the Web Service Tab.
     *
     * @param displayWebServiceTab boolean.
     */
    public void setDisplayWebServiceTab(boolean displayWebServiceTab) {
        this.displayWebServiceTab = displayWebServiceTab;
    }

    /**
     * Gets the Display Web Service Tab.
     *
     * @return displayWebServiceTab.
     */
    public boolean getDisplayWebServiceTab() {
        return displayWebServiceTab;
    }

    /**
     * Sets the Cytoscape Tab.
     *
     * @param displayCytoscapeTab boolean.
     */
    public void setDisplayCytoscapeTab(boolean displayCytoscapeTab) {
        this.displayCytoscapeTab = displayCytoscapeTab;
    }

    /**
     * Gets the Display Cytoscape Tab.
     *
     * @return displayCytoscapeTab.
     */
    public boolean getDisplayCytoscapeTab() {
        return displayCytoscapeTab;
    }
}