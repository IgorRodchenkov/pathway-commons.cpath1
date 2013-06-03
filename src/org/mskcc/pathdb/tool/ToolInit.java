package org.mskcc.pathdb.tool;

import org.mskcc.pathdb.util.cache.EhCache;
import org.mskcc.pathdb.util.CPathConstants;
import org.mskcc.dataservices.util.PropertyManager;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Utility Class Used to Initialize all Command Line Tools.
 *
 * @author Ethan Cerami
 */
public class ToolInit {

    /**
     * Initializes the build properties.
     * @throws java.io.IOException IO Error.
     */
    public static void initProps() throws IOException {
        EhCache.initCache();
        EhCache.resetAllCaches();

        //  Load build.properties
        String cpathHome = System.getProperty(Admin.CPATH_HOME);
        String separator = System.getProperty("file.separator");
        Properties buildProps = new Properties();
        buildProps.load(new FileInputStream(cpathHome
                + separator + "build.properties"));

        String dbUser = buildProps.getProperty("db.user");
        String dbPwd = buildProps.getProperty("db.password");
        String dbName = buildProps.getProperty("db.name");
        String dbHost = buildProps.getProperty("db.host");

        PropertyManager propertyManager = PropertyManager.getInstance();
        propertyManager.setProperty(PropertyManager.DB_USER, dbUser);
        propertyManager.setProperty(PropertyManager.DB_PASSWORD, dbPwd);
        propertyManager.setProperty(CPathConstants.PROPERTY_MYSQL_DATABASE, dbName);
        propertyManager.setProperty(PropertyManager.DB_LOCATION, dbHost);
    }
}
