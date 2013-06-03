package org.mskcc.pathdb.util;

import org.mskcc.pathdb.tool.Admin;
import org.mskcc.pathdb.sql.dao.DaoExternalDbSnapshot;
import org.mskcc.pathdb.sql.dao.DaoExternalDb;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.schemas.externalDb.ExternalDbXmlUtil;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.*;
import org.jdom.JDOMException;

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
        DaoExternalDb daoExternalDb = new DaoExternalDb();
        ArrayList list = daoExternalDb.getAllRecords();
        for (int i = 0; i < list.size(); i++) {
            ExternalDatabaseRecord dbRecord = (ExternalDatabaseRecord) list.get(i);
            System.out.print ("Checking Database:  " + dbRecord.getName());
            if (dbRecord.getIconFileExtension() != null) {
                ImageIcon icon = daoExternalDb.getIcon(dbRecord.getId());
                int width = icon.getIconWidth();
                int height = icon.getIconHeight();
                createImage(dbRecord.getId(), dbRecord.getIconFileExtension(),
                        width, height, icon);
            } else {
                System.out.println ("--> No image to create.");
            }
        }
    }

    /**
     * Updates Database Image Icons, and stores them to build/jsp/images.
     * @throws DaoException Database Error.
	 * @throws DOMException.
     * @throws IOException  IO Error.
     */
    public void updateDbImages(File file) throws DaoException, IOException, JDOMException {

        DaoExternalDb daoExternalDb = new DaoExternalDb();
        ExternalDbXmlUtil util = new ExternalDbXmlUtil(file);
        ArrayList list = util.getExternalDbList();
		System.out.println("Updating image blobs in database.");
        for (int i = 0; i < list.size(); i++) {
			ExternalDatabaseRecord dbRecord = (ExternalDatabaseRecord) list.get(i);
            ExternalDatabaseRecord existingRecord = daoExternalDb.getRecordByTerm(dbRecord.getMasterTerm().toUpperCase());
			if (existingRecord != null) {
				dbRecord.setId(existingRecord.getId());
				System.out.print ("Checking Database:  " + dbRecord.getName());
				File iconFile = getIconFile(file, dbRecord);
				if (iconFile != null) {
					System.out.println ("--> Updating image.");
					boolean res = daoExternalDb.addIcon(iconFile, dbRecord.getId());
					if (!res) {
						System.out.println ("--> Error adding image.");
					}
				}
				else {
					System.out.println ("--> Cannot find new image.");
				}
			}
			else {
				System.out.println("Cannot find existing Database Record for: " + dbRecord.getName());
			}
		}
		System.out.println("Creating new image files.");
		createDbImages();
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
        System.out.println("--> Creating image:  " + outFile.getAbsolutePath());
        ImageIO.write(bi, fileExtension, new File(outFile.getAbsolutePath()));
    }

    private File getIconFile(File file, ExternalDatabaseRecord dbRecord) {
        String path = dbRecord.getIconPath();
        File iconFile;

        //  Deal with absolute and relative paths
        if (path.startsWith("/") || path.startsWith("\"")) {
            iconFile = new File (path);
        } else {
            iconFile = new File (file.getParentFile(),
                dbRecord.getIconPath());
        }
        return iconFile;
    }
}