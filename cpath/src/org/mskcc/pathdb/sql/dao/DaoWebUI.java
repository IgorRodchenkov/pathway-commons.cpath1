// $Id: DaoWebUI.java,v 1.10 2006-06-09 19:22:03 cerami Exp $
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
package org.mskcc.pathdb.sql.dao;

// imports

import org.mskcc.pathdb.form.WebUIBean;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object to the WEB_UI Table.
 *
 * @author Benjamin Gross
 */
public class DaoWebUI {

    /**
     * Gets the one and only record.
     *
     * @return WebUIBean.
     * @throws DaoException Error Retrieving Data.
     */
    public WebUIBean getRecord() throws DaoException {

        // init some local vars
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        WebUIBean webUIBean = null;

        try {
            // perform the query
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from web_ui where 1");
            rs = pstmt.executeQuery();
            // set the form object
            if (rs.next()) {
                webUIBean = new WebUIBean();
                webUIBean.setApplicationName(rs.getString("APPLICATION_NAME"));
                webUIBean.setHomePageHeader(rs.getString("HOME_PAGE_HEADER"));
                webUIBean.setHomePageTagLine(rs.getString("HOME_PAGE_TAG_LINE"));
                webUIBean.setHomePageRightColumnContent(
                        rs.getString("HOME_PAGE_RIGHT_COLUMN_CONTENT"));
                webUIBean.setDisplayBrowseByPathwayTab(
                        rs.getBoolean("DISPLAY_BROWSE_BY_PATHWAY_TAB"));
                webUIBean.setDisplayBrowseByOrganismTab(
                        rs.getBoolean("DISPLAY_BROWSE_BY_ORGANISM_TAB"));
                webUIBean.setFAQPageContent(rs.getString("FAQ_PAGE_CONTENT"));
                webUIBean.setAboutPageContent(rs.getString("ABOUT_PAGE_CONTENT"));
                webUIBean.setMaintenanceTagLine(rs.getString("MAINTENANCE_TAG_LINE"));
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

        // outta here
        return webUIBean;
    }

    /**
     * Updates Record Status.
     *
     * @param form WebUIBean ref.
     * @return Number of Rows Affected.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean updateRecord(WebUIBean form)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("UPDATE web_ui set "
                            + "`APPLICATION_NAME` = ?, "
                            + "`HOME_PAGE_HEADER` = ?, "
                            + "`HOME_PAGE_TAG_LINE` = ?, "
                            + "`HOME_PAGE_RIGHT_COLUMN_CONTENT` = ?, "
                            + "`DISPLAY_BROWSE_BY_PATHWAY_TAB` = ?, "
                            + "`DISPLAY_BROWSE_BY_ORGANISM_TAB` = ?, "
                            + "`FAQ_PAGE_CONTENT` = ?, "
                            + "`ABOUT_PAGE_CONTENT` = ?, "
                            + "`MAINTENANCE_TAG_LINE` = ?");
            pstmt.setString(1, form.getApplicationName());
            pstmt.setString(2, form.getHomePageHeader());
            pstmt.setString(3, form.getHomePageTagLine());
            pstmt.setString(4, form.getHomePageRightColumnContent());
            pstmt.setBoolean(5, form.getDisplayBrowseByPathwayTab());
            pstmt.setBoolean(6, form.getDisplayBrowseByOrganismTab());
            pstmt.setString(7, form.getFAQPageContent());
            pstmt.setString(8, form.getAboutPageContent());
            pstmt.setString(9, form.getMaintenanceTagLine());
            int rows = pstmt.executeUpdate();
            return (rows > 0);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}
