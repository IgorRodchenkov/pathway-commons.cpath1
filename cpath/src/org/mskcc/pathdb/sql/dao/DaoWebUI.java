//
// $Id: DaoWebUI.java,v 1.1 2005-11-08 17:55:59 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2005 Memorial Sloan-Kettering Cancer Center.
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
package org.mskcc.pathdb.sql.dao;

// imports
import org.mskcc.pathdb.form.WebUIForm;
import org.mskcc.pathdb.sql.JdbcUtil;
import java.io.IOException;		
import java.sql.*;
import org.apache.log4j.Logger;

/**
 * Data Access Object to the WEB_UI Table.
 *
 * @author Benjamin Gross
 */
public class DaoWebUI {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	/**
	 * Column name string.
	 */
    private static final String LOGO = "LOGO";

	/**
	 * Column name string.
	 */
    private static final String HOME_PAGE_TITLE = "HOME_PAGE_TITLE";

	/**
	 * Column name string.
	 */
    private static final String HOME_PAGE_TAG_LINE = "HOME_PAGE_TAG_LINE";

	/**
	 * Column name string.
	 */
    private static final String HOME_PAGE_RIGHT_COLUMN_CONTENT = "HOME_PAGE_RIGHT_COLUMN_CONTENT";

	/**
	 * Column name string.
	 */
    private static final String DISPLAY_BROWSE_BY_PATHWAY_TAB = "DISPLAY_BROWSE_BY_PATHWAY_TAB";

	/**
	 * Column name string.
	 */
    private static final String DISPLAY_BROWSE_BY_ORGANISM_TAB = "DISPLAY_BROWSE_BY_ORGANISM_TAB";

	/**
	 * Column name string.
	 */
    private static final String FAQ_PAGE_CONTENT = "FAQ_PAGE_CONTENT";

	/**
	 * Column name string.
	 */
    private static final String ABOUT_PAGE_CONTENT = "FAQ_PAGE_CONTENT";

	/**
	 * Column name string.
	 */
    private static final String HOME_PAGE_MAINTENANCE_TAG_LINE = "HOME_PAGE_MAINTENANCE_TAG_LINE";

    /**
     * Gets the one and only record.
     *
     * @return WebUIForm.
     * @throws DaoException Error Retrieving Data.
     */
    public WebUIForm getRecord() throws DaoException {

		// init some local vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WebUIForm webUIForm = null;
		
        try {
			// perform the query
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
				("select * from web_ui where 1");
            rs = pstmt.executeQuery();
			// set the form object
			if (rs.next()){
				webUIForm = new WebUIForm();
				webUIForm.setLogo(rs.getString(LOGO));
				webUIForm.setHomePageTitle(rs.getString(HOME_PAGE_TITLE));
			}
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

		// outta here
		return webUIForm;
    }

    /**
     * Updates Record Status.
     *
     * @param form WebUIForm ref.
     * @return Number of Rows Affected.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean updateRecord(WebUIForm form)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

		logger.info("INFO in updateRecord");
		logger.info("logo: " + form.getLogo());
		logger.info("homePageTile: " + form.getHomePageTitle());

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("UPDATE web_ui set " +
					 "`LOGO` = ?, " +
					 "`HOME_PAGE_TITLE` = ?");
            pstmt.setString(1, form.getLogo());
            pstmt.setString(2, form.getHomePageTitle());
            int rows = pstmt.executeUpdate();
			if (rows > 0){
				logger.info("INFO in updateRecord - SUCCESS!");
			}
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}
