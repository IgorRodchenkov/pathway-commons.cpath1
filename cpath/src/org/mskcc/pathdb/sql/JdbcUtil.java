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
package org.mskcc.pathdb.sql;

import org.apache.commons.dbcp.*;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.mskcc.dataservices.util.PropertyManager;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Connection Utility for JDBC.
 *
 * @author Ethan Cerami
 */
public class JdbcUtil {
    private static DataSource dataSource;
    private static GenericObjectPool connectionPool;
    private static final String DB_CPATH = "cpath";
    private static boolean commandLineFlag = false;
    private static Connection commandLineConnection;

    /**
     * Special Setting for Command Line Applications
     *
     * @param flag true or false.
     */
    public static void setCommandLineFlag(boolean flag) {
        commandLineFlag = flag;
    }

    /**
     * Gets the Command Line Flag.
     *
     * @return Command Line Flag.
     */
    public static boolean getCommandLineFlag() {
        return commandLineFlag;
    }

    /**
     * Gets Connection to the CPath Database.
     * If this is a command line connection, reuse the static
     * commandLienConnection object.  Otherwise, get a connection from the
     * database pool.
     *
     * @return Live Connection to Database.
     * @throws SQLException           Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     */
    public static Connection getCPathConnection()
            throws SQLException, ClassNotFoundException {
        if (dataSource == null) {
            initDataSource();
        }
        if (commandLineFlag) {
            if (commandLineConnection == null
                    || commandLineConnection.isClosed()) {
                commandLineConnection = dataSource.getConnection();
            }
            return commandLineConnection;
        } else {
            Connection con = dataSource.getConnection();
            return con;
        }
    }

    /**
     * Initializes Data Source.
     */
    private static void initDataSource() throws ClassNotFoundException {
        PropertyManager manager = PropertyManager.getInstance();
        String host = manager.getProperty(PropertyManager.DB_LOCATION);
        String userName = manager.getProperty(PropertyManager.DB_USER);
        String password = manager.getProperty(PropertyManager.DB_PASSWORD);

        //  Note that the zeroDateTimeBehavor value is set to "convertToNull"
        //  Here is an excerpted exlanation from the MySQL JDBC driver
        //  documentation:

        //  Datetimes with all-zero components ('0000-00-00 ...') -
        //  These values can not be represented reliably in Java.
        //  Connector/J 3.0.x always converted them to NULL when being read
        //  from a ResultSet.

        //  Connector/J 3.1 throws an exception by default when these values
        //  are encountered as this is the most correct behavior according to
        //  the JDBC and SQL standards. This behavior can be modified using
        //  the 'zeroDateTimeBehavior' configuration property. The allowable
        //  values are: 'exception' (the default), which throws a SQLException
        //  with a SQLState of 'S1009', 'convertToNull', which returns NULL
        //  instead of the date, and 'round', which rounds the date to the
        // nearest closest value which is '0001-01-01'.

        String url =
                new String("jdbc:mysql://" + host + "/" + DB_CPATH
                + "?user=" + userName + "&password=" + password
                + "&zeroDateTimeBehavior=convertToNull");

        Class.forName("com.mysql.jdbc.Driver");
        dataSource = setupDataSource(url);
    }

    /**
     * Frees Database Connection.
     * If this is a command line application, we keep the connection open
     * (this improves overall performance).
     *
     * @param con Connection Object.
     */
    private static void closeConnection(Connection con) {
        if (!commandLineFlag) {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
        closeConnection(con);
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
     * Initializes Database Connection Pool.
     *
     * @param connectURI Connection URI.
     * @return DataSource Object.
     */
    public static DataSource setupDataSource(String connectURI) {
        //
        // First, we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        connectionPool = new GenericObjectPool(null);
        connectionPool.setMaxActive(10);

        //
        // Next, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory =
                new DriverManagerConnectionFactory(connectURI, null);

        //
        // Now we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        PoolableConnectionFactory poolableConnectionFactory =
                new PoolableConnectionFactory
                        (connectionFactory, connectionPool, null,
                                null, false, true);

        //
        // Finally, we create the PoolingDriver itself,
        // passing in the object pool we created.
        //
        PoolingDataSource dataSource = new PoolingDataSource(connectionPool);

        return dataSource;
    }

    /**
     * Gets the SQL string statement associated with a PreparedStatement.
     * <P>
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