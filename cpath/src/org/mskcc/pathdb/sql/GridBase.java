package org.mskcc.pathdb.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * GRID Base Class.
 * Initiates opening of database connection to GRID database.
 *
 * @author Ethan Cerami
 */
public abstract class GridBase {
    /**
     * Host Name of GRID Database.
     */
    private String host;

    /**
     * User Name for connecting to Database.
     */
    private String user;

    /**
     * Password for connecting to Database.
     */
    private String password;

    /**
     * Name of GRID MySQL Database.
     */
    static final String DATABASE = "grid";

    /**
     * Common Term Delimiter.
     */
    static final String DELIMITER = ";";

    /**
     * GO Term/ID Prefix.
     */
    static final String GO_PREFIX = "GO:";

    /**
     * Database Lookup Key:  Local ID.
     */
    static final String KEY_LOCAL_ID = "id";

    /**
     * Database Lookup Key:  ORF_NAME.
     */
    static final String KEY_ORF = "orf_name";

    /**
     * Constructor.
     * @param host Database Host Name.
     * @param user Database User Name.
     * @param password Password.
     */
    public GridBase(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    /**
     * Gets Live Database Connection to GRID.
     * @return Database Connection object.
     * @throws SQLException Database Error.
     * @throws ClassNotFoundException Unable to locate JDBC Driver.
     */
    protected Connection getConnection()
            throws SQLException, ClassNotFoundException {
        String url =
                new String("jdbc:mysql://" + host + "/"
                + GridBase.DATABASE + "?user=" + user
                + "&password=" + password);
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = DriverManager.getConnection(url);
        return con;
    }

    /**
     * Splits String into multiple String tokens.
     * @param str String to split.
     * @return Array of String objects.
     */
    protected String[] splitString(String str) {
        if (str.startsWith(GridBase.DELIMITER)) {
            str = str.substring(1);
        }
        String string[] = str.split(GridBase.DELIMITER);
        return string;
    }

    /**
     * Gets Database Host Name.
     * @return Database host name.
     */
    protected String getHost() {
        return host;
    }

    /**
     * Gets User Name for connecting to Database.
     * @return User Name.
     */
    protected String getUser() {
        return user;
    }

    /**
     * Gets Password for connecting to Database.
     * @return Password.
     */
    protected String getPassword() {
        return password;
    }
}