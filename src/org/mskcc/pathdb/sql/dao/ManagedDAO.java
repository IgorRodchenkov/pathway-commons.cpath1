// $Id: ManagedDAO.java,v 1.6 2006-11-17 16:32:03 cerami Exp $
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
package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Managed Data Access Object Base Class.
 *
 * @author idk37697
 */
public abstract class ManagedDAO {

    //  map of all sql statements
    protected HashMap sqlMap = null;

    protected void init() throws DaoException {
        this.sqlMap = new HashMap();
    }

    protected void addPreparedStatement(String key, String statement)
            throws DaoException {
        this.sqlMap.put(key, statement);
    }

    /**
     * collect the stored prepared statement
     *
     * @param con          needs a valid connection when when the application
     *                     is not run from the command line, otherwise ignored
     * @param statementKey key to find the correct prepared statement
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    protected PreparedStatement getStatement(Connection con,
            String statementKey) throws SQLException, ClassNotFoundException {
        PreparedStatement pstmt;
        String statement = (String) this.sqlMap.get(statementKey);
        pstmt = con.prepareStatement(statement);
        return pstmt;
    }

    /**
     * this method checks whether we are started from the command line, if we
     * did the method returns a local singelton connection, if not it returns
     * a new connection
     *
     * @return active database connection
     * @throws SQLException           Database Error.
     * @throws ClassNotFoundException Class Not Found Error.
     */
    public Connection getConnection() throws SQLException,
            ClassNotFoundException {
        return JdbcUtil.getCPathConnection();
    }

    protected String getStoredSql(String key) {
        return (String) sqlMap.get(key);
    }

    /**
     * if we're running from the command line just close the result set,
     * otherwise close all three
     *
     * @param con
     * @param pstmt
     * @param rs
     */
    protected void localCloseAll(Connection con, PreparedStatement pstmt,
            ResultSet rs) {
        JdbcUtil.closeAll(con, pstmt, rs);
    }
}
