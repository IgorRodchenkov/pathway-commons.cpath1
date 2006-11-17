// $Id: JdbcUtil.java,v 1.27 2006-11-17 19:23:33 cerami Exp $
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
package org.mskcc.pathdb.sql;

import org.apache.commons.dbcp.*;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPool;
import org.mskcc.dataservices.util.PropertyManager;
import org.mskcc.pathdb.util.CPathConstants;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Connection Utility for JDBC.
 *
 * @author Ethan Cerami
 */
public class JdbcUtil {
    private static BasicDataSource ds;

    /**
     * Gets Connection to the CPath Database.
     *
     * @return Live Connection to Database.
     * @throws SQLException           Error Connecting to Database.
     */
    public static Connection getCPathConnection() throws SQLException {
        if (ds == null) {
            initDataSource();
        }
        Connection con = ds.getConnection();
        return con;
    }

    /**
     * Initializes Data Source.
     */
    private static void initDataSource() {
        PropertyManager manager = PropertyManager.getInstance();
        String host = manager.getProperty(PropertyManager.DB_LOCATION);
        String userName = manager.getProperty(PropertyManager.DB_USER);
        String password = manager.getProperty(PropertyManager.DB_PASSWORD);
        String database = manager
                .getProperty(CPathConstants.PROPERTY_MYSQL_DATABASE);

        if (database == null) {
            database = CPathConstants.DEFAULT_DB_NAME;
        }
        String url =
                new String("jdbc:mysql://" + host + "/" + database
                        + "?user=" + userName + "&password=" + password
                        + "&zeroDateTimeBehavior=convertToNull");

        //  Set up poolable data source
        ds = new BasicDataSource();
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUsername(userName);
        ds.setPassword(password);
        ds.setUrl(url);
        ds.setPoolPreparedStatements(false);
        ds.setMaxActive(10);
    }

    /**
     * Frees Database Connection.
     *
     * @param con Connection Object.
     */
    private static void closeConnection(Connection con) throws SQLException {
        if (con != null && ! con.isClosed()) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Frees Database Connection.
     *
     * @param con Connection Object.
     * @param ps  Prepared Statement Object.
     * @param rs  ResultSet Object.
     */
    public static void closeAll(Connection con, PreparedStatement ps,
            ResultSet rs) {
        try {
            closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the SQL string statement associated with a PreparedStatement.
     * <p/>
     * This method compensates for a bug in the DBCP Code.  DBCP wraps an
     * original PreparedStatement object, but when you call toString() on the
     * wrapper, it returns a generic String representation that does not include
     * the actual SQL code which gets executed.  To get around this bug, this
     * method checks to see if we have a DBCP wrapper.  If we do, we get the
     * original delegate, and properly call its toString() method.  This
     * results in the actual SQL statement sent to the database.
     *
     * @param pstmt PreparedStatement Object.
     * @return toString value.
     */
    public static String getSqlQuery(PreparedStatement pstmt) {
        if (pstmt instanceof DelegatingPreparedStatement) {
            DelegatingPreparedStatement dp =
                    (DelegatingPreparedStatement) pstmt;
            Statement delegate = dp.getDelegate();
            return delegate.toString();
        } else {
            return pstmt.toString();
        }
    }
}
