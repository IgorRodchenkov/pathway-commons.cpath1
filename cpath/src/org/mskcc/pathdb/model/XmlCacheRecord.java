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
}