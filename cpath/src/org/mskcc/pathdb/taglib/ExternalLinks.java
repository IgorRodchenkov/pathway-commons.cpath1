/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
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
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.taglib;

/**
 * Creates Links to External Resources.
 *
 * @author Ethan Cerami
 */
public class ExternalLinks {

    /**
     * Gets PubMedLink.
     * @param pmid PMID.
     * @return URL to PubMed.
     */
    public String getPubMedLink(String pmid) {
        String url = "http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?"
                + "cmd=Retrieve&db=PubMed&list_uids=" + pmid + "&dopt=Abstract";
        return url;
    }

    /**
     * Gets External Link.
     * @param db Database Name.
     * @param id Unique identifier.
     * @return URL to External Resource.
     */
    public String getExternalLink(String db, String id) {
        if (db.equals("Entrez GI")) {
            return getNcbiLink(id);
        } else if (db.equals("RefSeq GI")) {
            return getNcbiLink(id);
        } else if (db.equals("SwissProt")) {
            return getSwissProtLink(id);
        } else if (db.equals("InterPro")) {
            return getInterProLink(id);
        } else {
            return null;
        }
    }

    /**
     * Gets NCBI Link.
     * @param id Unique ID.
     * @return URL to NCBI.
     */
    public String getNcbiLink(String id) {
        String url = "http://www.ncbi.nlm.nih.gov:80/entrez/"
                + "query.fcgi?cmd=Retrieve&db=protein&list_uids="
                + id + "&dopt=GenPept";
        return url;
    }

    /**
     * Gets SwissProt Link.
     * @param id Unique ID.
     * @return URL to SwissProt.
     */
    public String getSwissProtLink(String id) {
        String url = "http://us.expasy.org/cgi-bin/niceprot.pl?" + id;
        return url;
    }

    /**
     * Gets InterPro Link.
     * @param id Unique ID.
     * @return URL to InterPro.
     */
    public String getInterProLink(String id) {
        String url = "http://www.ebi.ac.uk/interpro/ISearch?query="
                + id + "&mode=ipr";
        return url;
    }

    /**
     * Gets Internal Link to "get interactions".
     * @param id Unique ID.
     * @return URL back to CPath.
     */
    public String getInteractionLink(String id) {
        String url = "/ds/dataservice?version=1.0&cmd=retrieve_interactions&"
                + "db=grid&format=html&uid=" + id;
        return url;
    }
}