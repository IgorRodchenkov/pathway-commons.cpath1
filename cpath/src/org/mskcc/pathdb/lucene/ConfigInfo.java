// $Id: ConfigInfo.java,v 1.2 2006-02-21 22:51:09 grossb Exp $
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
 **/
/*
 * Created 05-Jul-2005
 * @author Iain Keddie
 * @author <BR>$Author: grossb $ (last revision)
 * @version $Revision: 1.2 $
 */
package org.mskcc.pathdb.lucene;

/** 
 * local container class 
 * @author Iain Keddie
 * */
public class ConfigInfo {
    private String xpath;

    private String fieldName;

    /**
     * constructor
     * @param fieldName index field name
     * @param xpath path to item to index
     */
    public ConfigInfo(String fieldName, String xpath) {
        this.xpath = xpath;
        this.fieldName = fieldName;
    }
    /**
     * @return Returns the fieldName.
     */
    public String getFieldName() {
        return fieldName;
    }
    /**
     * @param fieldName The fieldName to set.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    /**
     * @return Returns the xpath.
     */
    public String getXpath() {
        return xpath;
    }
    /**
     * @param xpath The xpath to set.
     */
    public void setXpath(String xpath) {
        this.xpath = xpath;
    }
}

