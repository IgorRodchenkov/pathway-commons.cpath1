package org.mskcc.pathdb.sql.dao;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

/**
 * Data Access Object to the cPath Table.
 *
 * @author Ethan Cerami.
 */
public class DaoCPath {

    /**
     * Adds Specified Record to the cPath Table.
     * @param name Enity Name
     * @param description Enty Description
     * @param ncbiTaxonomyId NCBI Taxonomy ID.  If TaxonomyID is not available,
     * use the constant CpathRecord.TAXONOMY_NOT_SPECIFIED.
     * @param type CPathRecordType Object.
     * @param xml XML Content
     * @return cPath Id for newly saved record
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized long addRecord(String name, String description,
            int ncbiTaxonomyId, CPathRecordType type, String xml)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("INSERT INTO CPATH (`NAME`,`DESC`,"
                    + "`TYPE`,`NCBI_TAX_ID`, `XML_CONTENT` ,"
                    + " `CREATE_TIME`) VALUES (?,?,?,?,?,?)");
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, type.toString());
            pstmt.setInt(4, ncbiTaxonomyId);
            pstmt.setString(5, xml);
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());
            pstmt.setTimestamp(6, timeStamp);
            pstmt.executeUpdate();

            //  Get New CPath ID
            pstmt = con.prepareStatement("SELECT MAX(CPATH_ID) from CPATH");
            rs = pstmt.executeQuery();
            rs.next();
            long cpathId = rs.getLong(1);
            return cpathId;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Adds Specified Record to the cPath Table.
     * @param name Enity Name
     * @param description Enty Description
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     * @param type CPathRecordType Object.
     * @param xml XML Content
     * @param refs Array of External References
     * @return cPath Id for newly saved record
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized long addRecord(String name, String description,
            int ncbiTaxonomyId, CPathRecordType type, String xml,
            ExternalReference refs[]) throws DaoException {
        long cpathId = this.addRecord(name, description, ncbiTaxonomyId,
                type, xml);
        DaoExternalLink linker = new DaoExternalLink();
        linker.addMulipleRecords(cpathId, refs);
        return cpathId;
    }

    /**
     * Gets all cPath Records.
     * @return ArrayList of CPath Records.
     * @throws DaoException Error Retrieving Data.
     * @throws java.io.IOException Error Performing I/O.
     */
    public ArrayList getAllRecords() throws DaoException, IOException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList records = new ArrayList();
        try {
            con = JdbcUtil.getCPathConnection();
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("select * from CPATH order by CPATH_ID");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CPathRecord record = extractRecord(rs);
                records.add(record);
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
        return records;
    }

    //  TODO Next Phase:  Get all Records by Species NCBI Taxonomy ID
    //  TODO Next Phase:  Get all Records by External DB Source

    /**
     * Gets Record by specified CPath ID.
     * @param cpathId cPath ID.
     * @return cPath Record.
     * @throws DaoException Error Retrieving Data.
     */
    public CPathRecord getRecordById(long cpathId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM CPATH WHERE CPATH_ID = ?");
            pstmt.setLong(1, cpathId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return this.extractRecord(rs);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Record by specified Name.
     * @param name Name.
     * @return cPath Record.
     * @throws DaoException Error Retrieving Data.
     */
    public CPathRecord getRecordByName(String name) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM CPATH WHERE NAME = ?");
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return this.extractRecord(rs);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes cPath Record with the specified CPATH_ID.
     * This will also delete all external links associated with this record.
     * @param cpathId cPath ID of record to delete.
     * @return returns true if deletion was successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(long cpathId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM CPATH WHERE CPATH_ID = ?");
            pstmt.setLong(1, cpathId);
            int rows = pstmt.executeUpdate();

            //  Delete all External Links too
            DaoExternalLink linker = new DaoExternalLink();
            ArrayList links = linker.getRecordsByCPathId(cpathId);
            for (int i = 0; i < links.size(); i++) {
                ExternalLinkRecord link = (ExternalLinkRecord) links.get(i);
                linker.deleteRecordById(link.getId());
            }

            //  Delete all Internal Links
            DaoInternalLink internalLinker = new DaoInternalLink();
            internalLinker.deleteRecordsByCPathId(cpathId);

            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Updates XML Content for CPath Record.
     * @param cpathId cPath Id.
     * @param newXml New XML Content.
     * @return true indicates success.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean updateXml(long cpathId, String newXml) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("UPDATE CPATH SET `XML_CONTENT` = ?, `UPDATE_TIME` = ? "
                    + "WHERE `CPATH_ID` = ?");
            pstmt.setString(1, newXml);
            java.util.Date now = new java.util.Date();
            Timestamp timeStamp = new Timestamp(now.getTime());
            pstmt.setTimestamp(2, timeStamp);
            pstmt.setLong(3, cpathId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Extracts cPath Record from Result Set.
     * @param rs ResultSet Object.
     * @return cPath Record Object.
     */
    private CPathRecord extractRecord(ResultSet rs) throws SQLException {
        CPathRecord record = new CPathRecord();
        record.setId(rs.getLong("CPATH_ID"));
        record.setName(rs.getString("NAME"));
        record.setDescription(rs.getString("DESC"));
        CPathRecordType type = CPathRecordType.getType(rs.getString("TYPE"));
        record.setType(type);
        record.setSpecType(rs.getString("SPEC_TYPE"));
        record.setNcbiTaxonomyId(rs.getInt("NCBI_TAX_ID"));
        record.setXmlContent(rs.getString("XML_CONTENT"));
        record.setCreateTime(rs.getTimestamp("CREATE_TIME"));
        record.setUpdateTime(rs.getTimestamp("UPDATE_TIME"));
        return record;
    }
}