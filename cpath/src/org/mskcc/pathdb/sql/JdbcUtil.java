package org.mskcc.pathdb.sql;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.mskcc.dataservices.util.PropertyManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Connection Utility for JDBC.
 *
 * @author Ethan Cerami
 */
public class JdbcUtil {
    private static DataSource dataSource;
    private static GenericObjectPool connectionPool;
    private static final String DB_CPATH = "cpath";

    /**
     * Gets Connection to the CPath Database.
     * @return Live Connection to Database.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     */
    public static Connection getCPathConnection()
            throws SQLException, ClassNotFoundException {
        return JdbcUtil.connect(DB_CPATH);
    }

    /**
     * Gets Connection to the GRID Database.
     * @return Live Connection to Database.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     */
    public static Connection getGridConnection()
            throws SQLException, ClassNotFoundException {
        return null;
    }

    /**
     * Connects to Specified Database.
     * @param db Database Name.
     * @return Live Connection to Database.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     */
    private static Connection connect(String db)
            throws ClassNotFoundException, SQLException {
        if (dataSource == null) {
            initDataSource();
        }
        Connection con = dataSource.getConnection();
        return con;
    }

    /**
     * Initializes Data Source.
     */
    private static void initDataSource() throws ClassNotFoundException {
        PropertyManager manager = PropertyManager.getInstance();
        String host = manager.getProperty(PropertyManager.DB_LOCATION);
        String userName = manager.getProperty(PropertyManager.DB_USER);
        String password = manager.getProperty(PropertyManager.DB_PASSWORD);
        String url =
                new String("jdbc:mysql://" + host + "/" + DB_CPATH
                + "?user=" + userName + "&password=" + password);
        Class.forName("com.mysql.jdbc.Driver");
        dataSource = setupDataSource(url);
    }

    /**
     * Frees Database Connection.
     * @param con Connection Object.
     */
    private static void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Frees Database Connection.
     * @param con Connection Object.
     * @param ps Prepared Statement Object.
     * @param rs ResultSet Object.
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
}