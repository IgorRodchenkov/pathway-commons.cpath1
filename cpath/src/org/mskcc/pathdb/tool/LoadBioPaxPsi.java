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
package org.mskcc.pathdb.tool;

import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.util.ContentReader;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.dao.DaoException;
import org.mskcc.pathdb.sql.dao.DaoImport;

import java.io.File;

/**
 * Command Line Tool for Loading BioPAX and PSI-MI Data into the cPath import
 * table.
 *
 * @author Ethan Cerami
 */
public class LoadBioPaxPsi {

    /**
     * Imports BioPAX and PSI-MI Data File into the cPath Import Table.
     *
     * @param file File.
     * @param xmlType XmlRecordType Object.
     * @return Import Record Primary ID.
     * @throws DataServiceException File Input Error.
     * @throws DaoException         Data Access Error.
     */
    public static long importDataFile(File file, XmlRecordType xmlType)
            throws DataServiceException, DaoException {
        String description = file.getName();
        System.out.println("XML Type:  " + xmlType.toString());
        ContentReader contentReader = new ContentReader();
        String data = contentReader.retrieveContent(file.getAbsolutePath());
        DaoImport dbImport = new DaoImport();
        long importId = dbImport.addRecord(description, xmlType, data);
        System.out.println("Document Loaded.  Ready for Transfer.");
        return importId;
    }
}