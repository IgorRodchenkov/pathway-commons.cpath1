// $Id: Evidence.java,v 1.1 2007-11-02 13:06:44 grossben Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami, Benjamin Gross
 ** Authors: Ethan Cerami, Gary Bader, Benjamin Gross, Chris Sander
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
package org.mskcc.pathdb.model;

// imports
import org.mskcc.pathdb.model.ExternalLinkRecord;

import java.util.List;

/**
 * Class to encapsulate Evidence/Experiment information.
 *
 * @author Benjamin Gross
 */
public class Evidence {

	/**
	 * Inner class to represent the confidence member of evidence class.
	 */
	public static class Confidence {
		// members
		private List<String> values;
		private List<String> comments;
		private List<ExternalLinkRecord> externalLinks;
		// methods
		public List<String> getValues() { return values; }
		public void setValues(List<String> values) { this.values = values; }
		public List<String> getComments() { return comments; }
		public void setComments(List<String> comments) { this.comments = comments; }
		public List<ExternalLinkRecord> getExternalLinks() { return externalLinks; }
		public void setExternalLinks(List<ExternalLinkRecord> externalLinks) { this.externalLinks = externalLinks; }
    }

	/**
	 * Inner class to represent code member of evidence class.
	 */
	public static class Code {
		// members
		List<String> terms;
		List<String> comments;
		List<ExternalLinkRecord> externalLinks;
		// methods
		public List<String> getTerms() { return terms; }
		public void setTerms(List<String> terms) { this.terms = terms; }
		public List<String> getComments() { return comments; }
		public void setComments(List<String> comments) { this.comments = comments; }
		public List<ExternalLinkRecord> getExternalLinks() { return externalLinks; }
		public void setExternalLinks(List<ExternalLinkRecord> externalLinks) { this.externalLinks = externalLinks; }
	}

	// members
	private List<String> comments; 
	private List<Evidence.Code> codes;
    private List<Evidence.Confidence> confidences;
    private List<ExternalLinkRecord> externalLinks;

	// methods
	public List<String> getComments() { return comments; }
	public void setComments(List<String> comments) { this.comments = comments; }
	public List<Evidence.Code> getCodes() { return codes; }
	public void setCodes(List<Evidence.Code>  codes) { this.codes = codes; }
	public List<Evidence.Confidence> getConfidences() { return confidences; };
	public void setConfidences(List<Evidence.Confidence>  confidences) { this.confidences = confidences; }
	public List<ExternalLinkRecord> getExternalLinks() { return externalLinks; }
	public void setExternalLinks(List<ExternalLinkRecord>  externalLinks) { this.externalLinks = externalLinks; }
}
