package org.mskcc.pathdb.sql;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;

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
     * @throws ClassNotFoundException Could not located JDBC Driver.
     * @throws SQLException Error Connecting to Database
     */
    public synchronized long addRecord(String name, String description,
            int ncbiTaxonomyId, CPathRecordType type, String xml)
            throws ClassNotFoundException,
            SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        PreparedStatement pstmt = null;
        try {
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
            int rows = pstmt.executeUpdate();

            //  Get New CPath ID
            pstmt = con.prepareStatement("SELECT MAX(CPATH_ID) from CPATH");
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            long cpathId = rs.getLong(1);
            return cpathId;
        } finally {
            JdbcUtil.freeConnection(con, pstmt, null);
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
     * @throws ClassNotFoundException Could not located JDBC Driver.
     * @throws SQLException Error Connecting to Database
     * @throws ExternalDatabaseNotFoundException ExternalDatabase Not Found.
     */
    public synchronized long addRecord(String name, String description,
            int ncbiTaxonomyId, CPathRecordType type, String xml,
            ExternalReference refs[])
            throws ClassNotFoundException, SQLException,
            ExternalDatabaseNotFoundException {
        Connection con = JdbcUtil.getCPathConnection();
        con.setAutoCommit(false);
        PreparedStatement pstmt = null;
        try {
            long cpathId = this.addRecord(name, description, ncbiTaxonomyId,
                    type, xml);
            addExternalReferences(refs, cpathId);
            con.commit();
            return cpathId;
        } catch (ExternalDatabaseNotFoundException e) {
            // If ExternalDatabaseNotFoundException is thrown,
            // roll back everything, and rethrow exception.
            con.rollback();
            throw e;
        } finally {
            con.setAutoCommit(true);
            JdbcUtil.freeConnection(con, pstmt, null);
        }
    }

    /**
     * Gets all cPath Records.
     * @return ArrayList of CPath Records.
     * @throws ClassNotFoundException Error Locating Correct Database Driver.
     * @throws SQLException Error Connecting to Database.
     * @throws java.io.IOException Error Performing I/O.
     */
    public ArrayList getAllRecords()
            throws ClassNotFoundException, SQLException, IOException {
        ArrayList records = new ArrayList();
        Connection con = JdbcUtil.getCPathConnection();
        ResultSet rs = null;
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("select * from CPATH order by CPATH_ID");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CPathRecord record = extractRecord(rs);
                records.add(record);
            }
            return records;
        } finally {
            rs.close();
            JdbcUtil.freeConnection(con);
        }
    }

    //  TODO Next Phase:  Get all Records by Species NCBI Taxonomy ID
    //  TODO Next Phase:  Get all Records by External DB Source

    /**
     * Gets Record by specified CPath ID.
     * @param cpathId cPath ID.
     * @return cPath Record.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public CPathRecord getRecordById(long cpathId)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM CPATH WHERE CPATH_ID = ?");
            pstmt.setLong(1, cpathId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return this.extractRecord(rs);
            } else {
                return null;
            }
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }

    /**
     * Gets Record by specified Name.
     * @param name Name.
     * @return cPath Record.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public CPathRecord getRecordByName(String name)
            throws ClassNotFoundException, SQLException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
                    ("SELECT * FROM CPATH WHERE NAME = ?");
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return this.extractRecord(rs);
            } else {
                return null;
            }
        } finally {
            JdbcUtil.freeConnection(con);
        }
    }

    /**
     * Deletes cPath Record with the specified CPATH_ID.
     * This will also delete all external links associated with this record.
     * @param cpathId cPath ID of record to delete.
     * @return returns true if deletion was successful.
     * @throws SQLException Error connecting to database.
     * @throws ClassNotFoundException Error locating correct SQL driver.
     */
    public boolean deleteRecordById(long cpathId)
            throws SQLException, ClassNotFoundException {
        Connection con = JdbcUtil.getCPathConnection();
        try {
            PreparedStatement pstmt = con.prepareStatement
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

            //  TODO:  Delete all Internal Links too

            return (rows > 0) ? true : false;
        } finally {
            JdbcUtil.freeConnection(con);
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

    /**
     * Adds All External References to Database.
     * @param refs Array of External Reference Objects.
     */
    private void addExternalReferences(ExternalReference refs[], long cpathId)
            throws ClassNotFoundException, SQLException,
            ExternalDatabaseNotFoundException {
        for (int i = 0; i < refs.length; i++) {
            String dbName = refs[i].getDatabase();
            String id = refs[i].getId();
            DaoExternalDb dao = new DaoExternalDb();
            ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(dbName);
            if (dbRecord != null) {
                DaoExternalLink linker = new DaoExternalLink();
                ExternalLinkRecord link = new ExternalLinkRecord();
                link.setExternalDatabase(dbRecord);
                link.setCpathId(cpathId);
                link.setLinkedToId(id);
                linker.addRecord(link);
            } else {
                throw new ExternalDatabaseNotFoundException
                        ("No matching database "
                        + "found for:  " + dbName + "[" + id + "]");
            }
        }
    }
}