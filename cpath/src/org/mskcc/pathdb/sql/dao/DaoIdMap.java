package org.mskcc.pathdb.sql.dao;

import org.mskcc.pathdb.model.IdMapRecord;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.sql.JdbcUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Data Access Object to the Id Map Table.
 *
 * @author Ethan Cerami
 */
public class DaoIdMap {

    /**
     * Adds New ID Map Record.
     * <P>
     * This method ensures that duplicate records are not stored to the
     * database.  Check the return value to determine if record was saved
     * successfully.  A true value indicates success.  A false value indicates
     * that the record already exists and was not saved, or an error occurred.
     *
     * @param record IdMapRecord Object.
     * @return true if saved successfully.
     * @throws DaoException Error Saving Data.
     */
    public boolean addRecord(IdMapRecord record) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //  Validate the Incoming Database Ids
        DaoExternalDb dao = new DaoExternalDb();
        ExternalDatabaseRecord dbRecord1 = dao.getRecordById(record.getDb1());
        if (dbRecord1 == null) {
            throw new IllegalArgumentException ("External Database, DB1:  "
                    + record.getDb1() + " does not exist in database.");
        }
        ExternalDatabaseRecord dbRecord2 = dao.getRecordById(record.getDb1());
        if (dbRecord2 == null) {
            throw new IllegalArgumentException ("External Database, DB2:  "
                    + record.getDb2() + " does not exist in database.");
        }

        //  Validate that the record does not already exist in the database
        IdMapRecord dbRecord = getRecord(record);
        if (dbRecord == null) {
            try {
                con = JdbcUtil.getCPathConnection();
                pstmt = con.prepareStatement
                        ("INSERT INTO id_map (`DB_1`, `ID_1`, "
                        + "`DB_2`, `ID_2`) VALUES (?,?,?,?)");
                pstmt.setInt(1, record.getDb1());
                pstmt.setString(2, record.getId1().trim());
                pstmt.setInt(3, record.getDb2());
                pstmt.setString(4, record.getId2().trim());
                int rows = pstmt.executeUpdate();
                return (rows > 0) ? true : false;
            } catch (ClassNotFoundException e) {
                throw new DaoException(e);
            } catch (SQLException e) {
                throw new DaoException(e);
            } finally {
                JdbcUtil.closeAll(con, pstmt, rs);
            }
        } else {
            return false;
        }
    }

    /**
     * Gets ID Map Record specified by Primary ID.
     *
     * @param idMapId ID Map Primary ID.
     * @return IdMap Record
     * @throws DaoException Error Retrieving Data.
     */
    public IdMapRecord getRecordById(int idMapId)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM id_map WHERE ID_MAP_ID = ?");
            pstmt.setInt(1, idMapId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return createBean(rs);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes Record specified by Primary ID.
     *
     * @param idMapId ID Map Primary ID.
     * @return true if deletion is successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(int idMapId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("DELETE FROM id_map WHERE ID_MAP_ID = ?");
            pstmt.setInt(1, idMapId);
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
     * Gets the Record Specified by idRecord.
     * <P>
     * All ID Mappings are undirected.  Therefore the following are considered
     * equivalent:
     * <P>Affymetrix:155_s_at -- SwissProt: Q7272
     * <BR>SwissProt: Q7272 -- Affymetrix:155_s_at
     * <P>
     * Given an IdMapRecord, this method will therefore check for both
     * equivalent possibilities.  Either match will return a hit, and the
     * Data Access Object ensures that both options will never exist
     * simultaneously in the database.
     *
     * @param idRecord IdMapRecord Object.
     * @return IdMapRecord, if the record exists;  otherwise, null.
     * @throws DaoException Error Retrieving Data.
     */
    public IdMapRecord getRecord (IdMapRecord idRecord) throws DaoException {
        IdMapRecord option1 = getRecord (idRecord.getDb1(), idRecord.getId1(),
                idRecord.getDb2(), idRecord.getId2());
        IdMapRecord option2 = getRecord (idRecord.getDb2(),
                idRecord.getId2(), idRecord.getDb1(), idRecord.getId1());
        if (option1 != null) {
            return option1;
        } else if (option2 != null) {
            return option2;
        } else {
            return null;
        }
    }

    /**
     * Deletes all IdMap Records.  Use with extreme caution!
     *
     * @return true indicates success.
     * @throws DaoException Error Deleting Records.
     */
    public boolean deleteAllRecords() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("TRUNCATE table id_map;");
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
     * Gets Record which matches the specified criteria exactly.
     *
     * @param db1 Foreign Key to External Database.
     * @param id1 ID String
     * @param db2 Foreign Key to External Database.
     * @param id2 ID String
     * @return IdMapRecord, if the record exists;  otherwise, null.
     * @throws DaoException Error Retrieving Data.
     */
    private IdMapRecord getRecord (int db1, String id1, int db2,
            String id2) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getCPathConnection();
            pstmt = con.prepareStatement
                    ("SELECT * FROM id_map WHERE DB_1 = ? AND ID_1 = ? "
                        + "AND DB_2 = ? AND ID_2 = ?");
            pstmt.setInt(1, db1);
            pstmt.setString(2, id1);
            pstmt.setInt(3, db2);
            pstmt.setString(4, id2);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return createBean(rs);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }

    }

    /**
     * Creates an IdMapRecord Bean.
     */
    private IdMapRecord createBean(ResultSet rs) throws SQLException {
        IdMapRecord record = new IdMapRecord();
        record.setPrimaryId(rs.getInt("ID_MAP_ID"));
        record.setDb1(rs.getInt("DB_1"));
        record.setId1(rs.getString("ID_1"));
        record.setDb2(rs.getInt("DB_2"));
        record.setId2(rs.getString("ID_2"));
        return record;
    }
}