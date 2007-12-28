// $Id: webstart.js,v 1.12 2007-12-28 14:11:07 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2007 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander, Benjamin Gross
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
 * Javascript library to communication with java webstart application
 */

// globals
var SCRIPT_ELEMENT_ID = "cytoscape";
var timoutVar; // used to set/unset timeout handlers
var requestedID; // used to store record id link pressed
var requestedCommand; // used to store web services command of link pressed
var requestedNetworkViewTitle; // used to store web services title of link pressed - encoded from pc
var requestedDataSources; // used to store web services data source filter - encoded from pc
//var toolTip = "'<DIV CLASS=popup><DIV CLASS=popup_caption>Cytoscape</DIV><DIV CLASS=popup_text>To view this record in Cytoscape, click on this link.  If Cytoscape is already running, the network will be loaded into Cytoscape straightaway.  If Cytoscape is not running, Pathway Commons will attempt to load Cytoscape via Java Webstart.  Click on the 'help' link for more information.</DIV>";

/*
 * Function to determine webstart version - taken from sun site
 */
function webstartVersionCheck(versionString) {
    // Mozilla may not recognize new plugins without this refresh
    navigator.plugins.refresh(true);
    // First, determine if Web Start is available
    if (navigator.mimeTypes['application/x-java-jnlp-file']) {
        // Next, check for appropriate version family
        for (var i = 0; i < navigator.mimeTypes.length; ++i) {
		    var pluginType = navigator.mimeTypes[i].type;
			if (pluginType == "application/x-java-applet;version=" + versionString) {
                return true;
			}
		}
	}
	return true;
 }

/*
 * Called when we haven't heard back webstart application
 */
function timeoutHandler() {

	var answer = confirm ("Press the 'OK' button to launch Cytoscape via Web Start. Depending on your network connection, this may take a minute or two.");

    if (answer) {
        // construct webstart url
        var hostname = window.location.hostname;
        var port = window.location.port;
        if (port) {
            hostname += (":" + port);
        }
        var webstart_url = "cytoscape.jnlp?id=" + requestedID + "&command=" + requestedCommand;
        if (requestedNetworkViewTitle != "empty_title") {
          webstart_url += "&network_view_title=" + requestedNetworkViewTitle;
        }
        webstart_url += "&data_source=" + requestedDataSources;

        // determine if webstart is available - code taken from sun site
        var userAgent = navigator.userAgent.toLowerCase();
        // user is running windows
        if (userAgent.indexOf("msie") != -1 && userAgent.indexOf("win") != -1){
            document.write("<OBJECT " +
                           "codeBase=http://java.sun.com/update/1.5.0/jinstall-1_5_0_05-windows-i586.cab " +
                           "classid=clsid:5852F5ED-8BF4-11D4-A245-0080C6F74284 height=0 width=0>");
            document.write("<PARAM name=app VALUE=" + webstart_url + ">");
            document.write("<PARAM NAME=back VALUE=true>");
            // alternate html for browsers which cannot instantiate the object
            document.write("<A href=\"http://java.sun.com/j2se/1.5.0/download.html\">Download Java WebStart</A>");
            document.write("</OBJECT>");
        }
        // user is not running windows
        else if (webstartVersionCheck("1.5")) {
            window.location = webstart_url;
        }
        // user does not have jre installed or lacks appropriate version - direct them to sun download site
        else {
            window.open("http://jdl.sun.com/webapps/getjava/BrowserRedirect?locale=en&host=java.com",
                        "needdownload");
        }
    }

    // outta here
    return false;
}

/*
 * Called when webstart program has returned with a response.
 */
function callBack() {
    clearTimeout(timeoutVar);
}

/*
 * Called to disable a link to the webstart.
 */
function disableLink(linkID) {

    var link = document.getElementById(linkID);
    if (link) {
        link.onclick = function() { return false; };
        link.style.cursor = "default";
		link.style.color = "#000000";
    }
}

/**
 * Called to make a webstart app request
 */
function appRequest(url, linkID, command, networkViewTitle, dataSources) {

    // be good and remove the previous cytoscape script element
    // although, based on debugging, i'm not sure this really does anything
    var oldScript = document.getElementById(SCRIPT_ELEMENT_ID);
    if (oldScript) {
        oldScript.parentNode.removeChild(oldScript);
    }

    // create new script
    var newScript = document.createElement("script");
    newScript.id = SCRIPT_ELEMENT_ID;
    newScript.setAttribute("type", "text/javascript");
    newScript.setAttribute("src", url);

    // add new script to document (head section)
    var head = document.getElementsByTagName("head")[0];
    head.appendChild(newScript);

    // disable link
    // we do this because some browsers
    // will not fetch data if the url has been fetched in the past
    disableLink(linkID);

    // save record id in case we have to pass to webstart
    requestedID = linkID;

	// save command in case we have to pass to webstart
	requestedCommand = command;

	// save title in case we have to pass to webstart
	requestedNetworkViewTitle = networkViewTitle;

	// save requested data sources
	requestedDataSources = dataSources;

    // set timeout - handler for when cytoscape is not running
    timeoutVar = setTimeout("timeoutHandler()", 1000);
}
