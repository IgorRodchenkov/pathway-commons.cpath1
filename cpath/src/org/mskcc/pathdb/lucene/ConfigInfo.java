// $Id: ConfigInfo.java,v 1.5 2006-06-09 19:22:03 cerami Exp $
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
 * Created 05-Jul-2005
 * @author Iain Keddie
 * @author <BR>$Author: cerami $ (last revision)
 * @version $Revision: 1.5 $
 */
package org.mskcc.pathdb.lucene;

/**
 * local container class
 *
 * @author Iain Keddie
 */
public class ConfigInfo {
    private String xpath;

    private String fieldName;

    /**
     * constructor
     *
     * @param fieldName index field name
     * @param xpath     path to item to index
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

