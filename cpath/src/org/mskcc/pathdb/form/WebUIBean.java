// $Id: WebUIBean.java,v 1.20 2008-12-10 04:56:30 grossben Exp $
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

import org.apache.struts.action.*;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.protocol.ProtocolConstantsVersion2;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;

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
     * Support Cytoscape (tab, webstart links)
     */
    private boolean wantCytoscape;

    /**
     * Display Filter Tab.
     */
    private boolean displayFilterTab;

    /**
     * A Default user message.
     */
    private String defaultUserMessage;

    /**
     * Base URL
     */
    private String baseURL;

    /**
     * SMTP Host, used to send email.
     */
    private String smtpHost;

    /**
     * To address, where feedback is submitted.
     */
    private String feedbackEmailTo;

    /**
     * Currently Supported Web API Version #.  Defaults to Version 2.0.
     */
    private String webApiVersion = ProtocolConstantsVersion2.VERSION_2;

    /**
     * Header Tag line.
     */
    private String tagLine = "";

	/**
	 * Flag indicating Mini-Maps are enabled.
	 */
    private boolean enableMiniMaps;

	/**
	 * Image Map Server URL
	 */
	private String imageMapServerURL = "";

	/**
	 * Max Neighborhood Map Size
	 */
	private Integer maxMiniMapSize;

	/**
	 * Filter Interactions
	 */
	private String filterInteractions = "";

    /**
     * List of Supported ID Types.
     */
    private ArrayList<String> supportedIdTypes = new ArrayList();

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
     * Sets the wantCytoscape boolean.
     *
     * @param wantCytoscape boolean.
     */
    public void setWantCytoscape(boolean wantCytoscape) {
        this.wantCytoscape = wantCytoscape;
    }

    /**
     * Gets the want Cytoscape boolean.
     *
     * @return wantCytoscape.
     */
    public boolean getWantCytoscape() {
        return wantCytoscape;
    }

    /**
     * Gets the Display Filer Tab.
     * @return displayFilterTab.
     */
    public boolean getDisplayFilterTab () {
        return displayFilterTab;
    }

    /**
     * Sets the Display Filter Tab.
     * @param displayFilterTab Display Filter Tab.
     */
    public void setDisplayFilterTab (boolean displayFilterTab) {
        this.displayFilterTab = displayFilterTab;
    }

    /**
     * Sets a default user message.
     * <P>If set, the default user message appears at the top of every cPath page.
     * Currently used to display a message that solicits feedback, but could also be
     * used to set routine maintenace messaages, etc.
     *
     * @return Default user message.
     */
    public String getDefaultUserMessage () {
        return defaultUserMessage;
    }

    /**
     * Gets a default user message.
     * <P>If set, the default user message appears at the top of every cPath page.
     * Currently used to display a message that solicits feedback, but could also be
     * used to set routine maintenace messaages, etc.
     * @param defaultUserMessage Default user message.
     */
    public void setDefaultUserMessage (String defaultUserMessage) {
        this.defaultUserMessage = defaultUserMessage;
    }

    /**
     * Gets the current base URL.
     * <P>The base URL is currently used to prevent SPAM-bots from spamming the feedback
     * form.  For pathway commons, the base URL is currently set to pathwaycommons.org.
     * If a user submits a feedback message with lots of URLs pointing to the base URL,
     * we consider that OK.  However, if a user submits a message with lots of URLs pointing
     * to *other* sites, it's probably a SPAM-bot, and we reject the form submission.
     *
     * @return base URL.
     */
    public String getBaseURL () {
        return baseURL;
    }

    /**
     * Sets the current base URL.
     * <P>The base URL is currently used to prevent SPAM-bots from spamming the feedback
     * form.  For pathway commons, the base URL is currently set to pathwaycommons.org.
     * If a user submits a feedback message with lots of URLs pointing to the base URL,
     * we consider that OK.  However, if a user submits a message with lots of URLs pointing
     * to *other* sites, it's probably a SPAM-bot, and we reject the form submission.
     * @param baseURL base URL.
     */
    public void setBaseURL (String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Gets SMTP Host.
     * <P>This property must be set, in order for cPath to send email messages.  Currently
     * required for the feedback form feature.
     *
     * @return  SMTP Host.
     */
    public String getSmtpHost () {
        return smtpHost;
    }

    /**
     * Sets SMTP Host.
     * <P>This property must be set, in order for cPath to send email messages.  Currently
     * required for the feedback form feature.
     * @param smtpHost
     */
    public void setSmtpHost (String smtpHost) {
        this.smtpHost = smtpHost;
    }

    /**
     * Gets Email Address where feedback will be sent.  Currently required for the feedback
     * form feature.
     * @return email address.
     */
    public String getFeedbackEmailTo () {
        return feedbackEmailTo;
    }

    /**
     * Sets Email Address where feedback will be set.  Currently required for the feedback
     * form feature.
     * @param feedbackEmailTo email address.
     */
    public void setFeedbackEmailTo (String feedbackEmailTo) {
        this.feedbackEmailTo = feedbackEmailTo;
    }

    /**
     * Gets the currently supported Web API version.
     * @return currently supported Web API version.
     */
    public String getWebApiVersion () {
        return webApiVersion;
    }

    /**
     * Sets the currently supported Web API version.
     * @param webApiVersion currently supported Web API version.
     */
    public void setWebApiVersion (String webApiVersion) {
        this.webApiVersion = webApiVersion;
    }

    /**
     * Add to list of supported ID types.
     * @param idType Must correspond to a Master CV Term.
     */
    public void addSupportedIdType(String idType) {
        supportedIdTypes.add(idType);
    }

    /**
     * A list of supported ID types.
     * @return ArrayLit of supported ID types.
     */
    public ArrayList <String> getSupportedIdTypes() {
        return supportedIdTypes;
    }

    /**
     * Gets Tag Line for Home Page.
     * @return tag line.
     */
    public String getTagLine() {
        return tagLine;
    }

    /**
     * Sets Tag Line for Home Page.
     * @param tagLine Tag Line.
     */
    public void setTagLine(String tagLine) {
        this.tagLine = tagLine;
    }

	/**
	 * Sets enable mini maps flag.
	 *
	 * @param enableMiniMaps boolean
	 */
	public void setEnableMiniMaps(boolean enableMiniMaps) {
		this.enableMiniMaps = enableMiniMaps;
	}

	/**
	 * Returns enable mini maps flag.
	 *
	 * @return boolean
	 */
	public boolean getEnableMiniMaps() {
		return enableMiniMaps;
	}

	/**
	 * Sets image map server url
	 *
	 * @param imageMapServerURL String
	 */
    public void setImageMapServerURL(String imageMapServerURL)
    {
        this.imageMapServerURL = imageMapServerURL;
    }

	/**
	 * Gets image map server url.
	 *
	 * @return String
	 */
    public String getImageMapServerURL()
    {
        return imageMapServerURL;
    }

	/**
	 * Sets max size (nodes) of mini map.
	 *
	 * @param maxMiniMapSize Integer
	 */
	public void setMaxMiniMapSize(Integer maxMiniMapSize) {
		this.maxMiniMapSize = maxMiniMapSize;
	}

	/**
	 * Gets max mini map size.
	 *
	 * @return Integer
	 */
	public Integer getMaxMiniMapSize() {
		return maxMiniMapSize;
	}

	/**
	 * Sets filter interactions string (comma delimited list of interactions).
	 *
	 * @param filterInteractions
	 */
	public void setFilterInteractions(String filterInteractions) {
		this.filterInteractions = filterInteractions;
	}

	/**
	 * Gets filter interactions string.
	 *
	 * @return String
	 */
	public String getFilterInteractions() {
		return filterInteractions;
	}

	/**
	 * Validates Mini-Map entries.
	 */
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();
		if (enableMiniMaps) {
			if (imageMapServerURL == null || imageMapServerURL.equals("")) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.mini_maps_config_form.required", "Image Map Server URL"));
			}
			else {
				try {
					java.net.URL url = new java.net.URL(imageMapServerURL);
				}
				catch (java.net.MalformedURLException e) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.mini_maps_config_form.invalid_imageMapServerURL"));
				}
			}
			if (maxMiniMapSize <= 0) {
				errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.mini_maps_config_form.invalid_maxMiniMapSize"));
			}
		}
		return errors;
	}
}