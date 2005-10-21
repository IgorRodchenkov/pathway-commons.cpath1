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
package org.mskcc.pathdb.util;

/**
 * CPath Constants.
 *
 * @author Ethan Cerami
 */
public class CPathConstants {

    /**
     * Current cPath Version Number.
     */
    public static final String VERSION = "0.5 (Beta)";

    /** db name property */
    public static final String PROPERTY_MYSQL_DATABASE = "mysql.database_name";

    /** default database name */
    public static final String DEFAULT_DB_NAME = "cpath";


    /**
     * Property Name for PSI_SCHEMA_LOCATION
     */
    public static final String PROPERTY_PSI_SCHEMA_LOCATION =
            "psi_schema_location";

    /**
     * CPath Home URI
     */
    public static final String CPATH_HOME_URI =
            "http://cbio.mskcc.org/cpath";

    /**
     * cPath Database Name.
     */
    public static final String CPATH_DB_NAME =
            "CPATH";
}
