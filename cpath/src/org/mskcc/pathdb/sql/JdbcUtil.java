package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.util.PropertyManager;

import java.sql.*;

/**
 * Connection Utility for JDBC.
 *
 * @author Ethan Cerami
 */
public class JdbcUtil {
    private static int counter = 0;
    private static Connection gridCon;
    private static Connection cpathCon;

    /**
     * Gets Connection to the CPath Database.
     * @return Live Connection to Database.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     */
    public static Connection getCPathConnection()
            throws SQLException, ClassNotFoundException {
        if (cpathCon == null || cpathCon.isClosed()) {
            cpathCon = JdbcUtil.connect("cpath");
        }
        return cpathCon;
    }

    /**
     * Gets Connection to the GRID Database.
     * @return Live Connection to Database.
     * @throws SQLException Error Connecting to Database.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     */
    public static Connection getGridConnection()
            throws SQLException, ClassNotFoundException {
        if (gridCon == null || gridCon.isClosed()) {
            gridCon = JdbcUtil.connect("grid");
        }
        return gridCon;
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
        counter++;
        PropertyManager manager = PropertyManager.getInstance();
        String host = manager.getProperty(PropertyManager.DB_LOCATION);
        String userName = manager.getProperty(PropertyManager.DB_USER);
        String password = manager.getProperty(PropertyManager.DB_PASSWORD);
        String url =
                new String("jdbc:mysql://" + host + "/" + db
                + "?user=" + userName + "&password=" + password);
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url);
        return con;
    }

    /**
     * Frees Database Connection.
     * @param con Connection Object.
     */
    public static void freeConnection(Connection con) {
        counter--;
//        if (con != null) {
//            try {
//                con.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        }
    }

    /**
     * Frees Database Connection.
     * @param con Connection Object.
     * @param ps Prepared Statement Object.
     * @param rs ResultSet Object.
     */
    public static void freeConnection(Connection con, PreparedStatement ps,
            ResultSet rs) {
        freeConnection(con);
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
}
