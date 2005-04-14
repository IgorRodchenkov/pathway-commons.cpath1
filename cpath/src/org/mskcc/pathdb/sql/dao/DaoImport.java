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
package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.ImportRecord;
import org.mskcc.pathdb.model.XmlRecordType;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.file.ZipUtil;
import org.mskcc.pathdb.util.security.Md5Util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferOverflowException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object to the Import Table.
 *
 * @author Ethan Cerami.
 */
public class DaoImport {
    private static final String IMPORT_ID = "IMPORT_ID";
    private static final String DESCRIPTION = "DESC";
    private static final String XML_TYPE = "XML_TYPE";
    private static final String STATUS = "STATUS";
    private static final String CREATE_TIME = "CREATE_TIME";
    private static final String UPDATE_TIME = "UPDATE_TIME";
    private static final String DOC_BLOB = "DOC_BLOB";
    private static final String DOC_MD5 = "DOC_MD5";

    /**
     * Gets all Import Records.
     * Note:  this method does not retrieve the associated XML Blob.
     *
     * @return ArrayList of ImportRecord Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getAllRecords() throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select `IMPORT_ID`, `DESC`, `XML_TYPE`, `DOC_MD5`, "
                    + "`STATUS`, `CREATE_TIME`, `UPDATE_TIME`"
                    + " from import order by IMPORT_ID");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ImportRecord record = extractRecord(rs, false);
                records.add(record);
            }
            return records;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } catch (IOException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Individual Import Record.
     *
     * @param importId ImportID of Record to Retrieve.
     * @return ImportRecord Object.
     * @throws DaoException Error Retrieving Data.
     */
    public ImportRecord getRecordById(long importId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from import where IMPORT_ID=? order "
                    + "by IMPORT_ID");
            pstmt.setLong(1, importId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractRecord(rs, true);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } catch (IOException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Extracts Blob Data to Array of Bytes.
     */
    private byte[] extractBlobData(Blob blob) throws SQLException,
            IOException {
        InputStream in = blob.getBinaryStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        while (true) {
            int bytesRead = in.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            out.write(buffer, 0, bytesRead);
        }
        return out.toByteArray();
    }

    /**
     * Adds New Database Import Record.
     *
     * @param description Import Record Description.
     * @param xmlType     XmlRecordType Object.
     * @param data        String data.
     * @return indicate success (true) or failure (false).
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized long addRecord(String description, XmlRecordType
            xmlType, String data) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            data = implementJdkWorkAround(data);
            con = JdbcUtil.getCPathConnection();
            String hash = Md5Util.createMd5Hash(data);
            byte zippedData[] = ZipUtil.zip(data);
            pstmt = con.prepareStatement
                    ("INSERT INTO import (`DESC`, `XML_TYPE`, `DOC_BLOB`, "
                    + "`DOC_MD5`, `STATUS`, `CREATE_TIME`)"
                    + " VALUES (?,?,?,?,?,?)");
            pstmt.setString(1, description);
            pstmt.setString(2, xmlType.toString());
            pstmt.setBytes(3, zippedData);
            pstmt.setString(4, hash);
            pstmt.setString(5, ImportRecord.STATUS_NEW);
            java.util.Date date = new java.util.Date();
            Timestamp timeStamp = new Timestamp(date.getTime());
            pstmt.setTimestamp(6, timeStamp);
            int rows = pstmt.executeUpdate();

            //  Get New ID
            pstmt = con.prepareStatement("SELECT MAX(IMPORT_ID) from import");
            rs = pstmt.executeQuery();
            rs.next();
            long importId = rs.getLong(1);
            return importId;
        } catch (BufferOverflowException e) {
            IllegalArgumentException ie = new IllegalArgumentException
                    ("BufferOverflowException occurred while trying to load a "
                    + "large data file.  This is an existing bug in JDK 1.4,"
                    + " and the work-around fixed has apparently failed.  "
                    + "Please report this bug to the cPath Team and include a "
                    + "copy of the data file while failed.  Alternatively, "
                    + "refer to DaoImport.implementJdkWorkAround() for "
                    + "complete details.");
            throw new DaoException(ie);
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } catch (IOException e) {
            throw new DaoException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Implements a Workaround to an existing Java 1.4 Bug.
     * First, some background:  when trying to import a very large file from
     * HPRD, DaoImport was throwing a BufferOverflowException during the
     * creation of the MD5 and the creation of the Zip File.
     * <p/>
     * Turns out that this is a documented bug in JDK 1.4:
     * http://bugs.sun.com/bugdatabase/view_bug.do;:YfiG?bug_id=4949631
     * As of this writing (October 21, 2004), the bug remains open.
     * <p/>
     * The code below is a word-around suggested by the bug reporter.
     * The work-around is a bit of a hack, but it does work.
     *
     * @param data Original Data.
     * @return Massaged Data so that it gets around the JDK 1.4 bug.
     */
    private String implementJdkWorkAround(String data) {
        if (data.length() > 16777217 && data.length() % 4 == 1) {
            StringBuffer dataBuffer = new StringBuffer(data);
            //  Append two meaningless new line characters.
            //  Appending only one new line character does not fix the problem.
            dataBuffer.append("\n\n");
            return dataBuffer.toString();
        } else {
            return data;
        }
    }

    /**
     * Deletes Import Record with the specified IMPORT_ID.
     *
     * @param importID Import ID of record to delete.
     * @return returns true if deletion was successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(int importID) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM import WHERE IMPORT_ID = ?");
            pstmt.setInt(1, importID);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Updates Record Status.
     *
     * @param importID Import ID.
     * @param status  Set to ImportRecord.STATUS_TRANSFERRED, or
     *                ImportRecord.STATUS_INVALID.
     * @return Number of Rows Affected.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean updateRecordStatus(long importID, String status)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("UPDATE import set STATUS=? WHERE IMPORT_ID=?");
            pstmt.setString(1, status);
            pstmt.setLong(2, importID);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Extracts Database Record into Java class.
     */
    private ImportRecord extractRecord(ResultSet rs, boolean extractBlob)
            throws SQLException, IOException {
        ImportRecord record = new ImportRecord();
        record.setImportId(rs.getInt(IMPORT_ID));
        record.setDescription(rs.getString(DESCRIPTION));
        record.setXmlType(XmlRecordType.getType(rs.getString(XML_TYPE)));
        record.setStatus(rs.getString(STATUS));
        record.setCreateTime(rs.getTimestamp(CREATE_TIME));
        record.setUpdateTime(rs.getTimestamp(UPDATE_TIME));
        record.setMd5Hash(rs.getString(DOC_MD5));

        //  Unzip Blob
        if (extractBlob) {
            Blob blob = rs.getBlob(DOC_BLOB);
            byte blobData[] = extractBlobData(blob);
            String data = ZipUtil.unzip(blobData);
            record.setData(data);
        }
        return record;
    }
}