package org.mskcc.pathdb.util;

import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.ExternalDatabaseSnapshotRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.tool.Admin;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.*;

/**
 * External Database Image Util.
 */
public class ExternalDbImageUtil {
    private String cpathHome = System.getProperty(Admin.CPATH_HOME);
    private String separator = System.getProperty("file.separator");

    /**
     * Creates Database Image Icons, and stores them to build/jsp/images.
     * @throws DaoException Database Error.
     * @throws IOException  IO Error.
     */
    public void createDbImages() throws DaoException, IOException {
        DaoExternalDbSnapshot dao = new DaoExternalDbSnapshot();
        ArrayList list = dao.getAllDatabaseSnapshots();
        DaoExternalDb daoExternalDb = new DaoExternalDb();

        for (int i = 0; i < list.size(); i++) {
            ExternalDatabaseSnapshotRecord snapshotRecord =
                    (ExternalDatabaseSnapshotRecord) list.get(i);
            if (snapshotRecord.getExternalDatabase() != null) {
                    ExternalDatabaseRecord dbRecord = snapshotRecord.getExternalDatabase();
                    System.out.println ("Checking Database:  " + dbRecord.getName());
                    if (dbRecord.getIconFileExtension() != null) {
                        ImageIcon icon = daoExternalDb.getIcon(dbRecord.getId());
                        int width = icon.getIconWidth();
                        int height = icon.getIconHeight();
                        createImage(dbRecord.getId(), dbRecord.getIconFileExtension(),
                                width, height, icon);
                    }
            }
        }
    }

    /**
     * Creates the appropriate image icon.
     */
    private void createImage(long id, String fileExtension, int width, int height,
            ImageIcon icon) throws IOException {
        File outFile = new File (cpathHome + separator + "build" + separator
                + "jsp" + separator + "images" + separator + "database", "db_"
                + id + "." + fileExtension);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi.createGraphics();
        g2.drawImage(icon.getImage(), 0, 0, null);
        g2.dispose();
        System.out.println("Creating image:  " + outFile.getAbsolutePath());
        ImageIO.write(bi, fileExtension, new File(outFile.getAbsolutePath()));
    }
}