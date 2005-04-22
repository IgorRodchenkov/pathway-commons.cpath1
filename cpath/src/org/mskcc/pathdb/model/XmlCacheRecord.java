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
package org.mskcc.pathdb.model;

import java.util.Date;

/**
 * JavaBean to Encapsulate an XML Cache Record.
 *
 * @author Ethan Cerami
 */
public class XmlCacheRecord {
    private int cacheId;
    private String url;
    private String md5;
    private int numHits;
    private Date lastUsed;
    private XmlRecordType xmlType;

    /**
     * Gets Cache ID.
     *
     * @return Cache ID.
     */
    public int getCacheId() {
        return cacheId;
    }

    /**
     * Sets Cache ID.
     *
     * @param cacheId Cache ID.
     */
    public void setCacheId(int cacheId) {
        this.cacheId = cacheId;
    }

    /**
     * Gets URL that generates Cache Record.
     *
     * @return URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets URL that generates Cache Record.
     *
     * @param url URL.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets MD5 Hash of URL Request.
     *
     * @return MD5 Hash of URL Request.
     */
    public String getMd5() {
        return md5;
    }

    /**
     * Sets MD5 Hash of URL Request.
     *
     * @param md5 MD5 Hash of URL Request.
     */
    public void setMd5(String md5) {
        this.md5 = md5;
    }

    /**
     * Gets Number of Hits.
     *
     * @return Number of Hits.
     */
    public int getNumHits() {
        return numHits;
    }

    /**
     * Sets Number of Hits.
     *
     * @param numHits Number of Hits.
     */
    public void setNumHits(int numHits) {
        this.numHits = numHits;
    }

    /**
     * Gets Date/Time When Last Used.
     *
     * @return Date/Time Last Used.
     */
    public Date getLastUsed() {
        return lastUsed;
    }

    /**
     * Sets Date/Time When Last Used.
     *
     * @param lastUsed Date/Time Last Used.
     */
    public void setLastUsed(Date lastUsed) {
        this.lastUsed = lastUsed;
    }

    /**
     * Gets XML Record Type.
     *
     * @return XmlRecordType Object.
     */
    public XmlRecordType getXmlType() {
        return xmlType;
    }

    /**
     * Sets XML Record Type.
     *
     * @param xmlType XmlRecordType Object.
     */
    public void setXmlType(XmlRecordType xmlType) {
        this.xmlType = xmlType;
    }
}