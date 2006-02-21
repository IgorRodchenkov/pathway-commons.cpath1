// $Id: DaoCPath.java,v 1.25 2006-02-21 22:51:10 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006  Memorial Sloan-Kettering Cancer Center.
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

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalLinkRecord;
import org.mskcc.pathdb.model.XmlRecordType;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object to the cPath Table.
 *
 * @author Ethan Cerami.
 */
public class DaoCPath extends ManagedDAO {

    private static DaoCPath daoCPath = null;

    //  Get Num Entities SQL
    private static final String GET_NUM_ENTITIES_KEY = "GET_NUM_ENTITIES_KEY";
    private static final String GET_NUM_ENTITIES =
            "select count(CPATH_ID) from cpath where type = ?";

    //  Insert SQL
    private static final String INSERT_KEY = "INSERT_KEY";
    private static final String INSERT =
            "INSERT INTO cpath (`NAME`,`DESC`,"
            + "`TYPE`, `SPECIFIC_TYPE`, `NCBI_TAX_ID`, `XML_TYPE`, "
            + "`XML_CONTENT` ,"
            + " `CREATE_TIME`) VALUES (?, ?,?,?,?,?,?,?)";

    private static final String GET_MAX_ID_KEY = "GET_MAX_ID_KEY";
    private static final String GET_MAX_ID =
            "SELECT MAX(CPATH_ID) from cpath";

    //  Get All SQL
    private static final String GET_ALL_KEY = "GET_ALL_KEY";
    private static final String GET_ALL =
            "select * from cpath order by CPATH_ID";

    //  Get All by Record Type SQL
    private static final String GET_ALL_BY_TYPE_KEY = "GET_ALL_BY_TYPE_KEY";
    private static final String GET_ALL_BY_TYPE =
            "select * from cpath WHERE TYPE = ? order by CPATH_ID";

    //  Get All by Taxonomy ID SQL
    private static final String GET_ALL_BY_TAX_ID_KEY = "GET_ALL_BY_TAX_ID_KEY";
    private static final String GET_ALL_BY_TAX_ID =
            "SELECT * FROM cpath WHERE TYPE = ? AND " + "NCBI_TAX_ID = ?";

    //  Get Taxonomy IDs SQL
    private static final String GET_TAX_IDS_KEY = "GET_TAX_IDS";
    private static final String GET_TAX_IDS =
            "SELECT DISTINCT NCBI_TAX_ID FROM cpath";

    //  Get By ID SQL
    private static final String GET_BY_ID_KEY = "GET_BY_ID_KEY";
    private static final String GET_BY_ID =
            "SELECT * FROM cpath WHERE CPATH_ID = ?";

    //  Get By Name SQL
    private static final String GET_BY_NAME_KEY = "GET_BY_NAME";
    private static final String GET_BY_NAME =
            "SELECT * FROM cpath WHERE NAME = ?";

    //  Delete by ID SQL
    private static final String DELETE_BY_ID_KEY = "DELETE_BY_ID_KEY";
    private static final String DELETE_BY_ID =
            "DELETE FROM cpath WHERE CPATH_ID = ?";

    //  Update XML SQL
    private static final String UPDATE_XML_KEY = "UPDATE_XML_KEY";
    private static final String UPDATE_XML =
            "UPDATE cpath SET `XML_CONTENT` = ?, `UPDATE_TIME` = ? "
            + "WHERE `CPATH_ID` = ?";

    // get the max cpath id
    private static final String SELECT_MAX_CPATH_ID_KEY = 
        "SELECT_MAX_CPATH_ID_KEY";
    private static final String SELECT_MAX_CPATH_ID = 
            "SELECT MAX(CPATH_ID) FROM cpath";
    
    /**
     * Private Constructor (Singleton pattern).
     */
    private DaoCPath() {
    }

    /**
     * Gets Instance of Dao Object. (Singleton pattern).
     *
     * @return DaoCPath Object.
     * @throws DaoException Dao Initialization Error.
     */
    public static DaoCPath getInstance() throws DaoException {
        if (daoCPath == null) {
            daoCPath = new DaoCPath();
            daoCPath.init();
        }
        return daoCPath;
    }

    /**
     * Initialize DAO Prepared Statement Objects.
     *
     * @throws DaoException Dao Initialization Error.
     */
    protected void init() throws DaoException {
        super.init();
        addPreparedStatement(GET_NUM_ENTITIES_KEY, GET_NUM_ENTITIES);
        addPreparedStatement(INSERT_KEY, INSERT);
        addPreparedStatement(GET_MAX_ID_KEY, GET_MAX_ID);
        addPreparedStatement(GET_ALL_KEY, GET_ALL);
        addPreparedStatement(GET_ALL_BY_TYPE_KEY, GET_ALL_BY_TYPE);
        addPreparedStatement(GET_ALL_BY_TAX_ID_KEY, GET_ALL_BY_TAX_ID);
        addPreparedStatement(GET_TAX_IDS_KEY, GET_TAX_IDS);
        addPreparedStatement(GET_BY_ID_KEY, GET_BY_ID);
        addPreparedStatement(GET_BY_NAME_KEY, GET_BY_NAME);
        addPreparedStatement(DELETE_BY_ID_KEY, DELETE_BY_ID);
        addPreparedStatement(UPDATE_XML_KEY, UPDATE_XML);
        addPreparedStatement(SELECT_MAX_CPATH_ID_KEY, SELECT_MAX_CPATH_ID);
    }

    /**
     * Gets Total Number of Entities which match the specified Record Type.
     *
     * @param recordType RecordType Object.
     * @return number of entities.
     * @throws DaoException Indicates Error in Data access.
     */
    public int getNumEntities(CPathRecordType recordType)
            throws DaoException {
        int num = -1;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, GET_NUM_ENTITIES_KEY);
            pstmt.setString(1, recordType.toString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                num = rs.getInt(1);
            }
            return num;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * Adds Specified Record to the cPath Table.
     *
     * @param name           Enity Name
     * @param description    Entity Description
     * @param ncbiTaxonomyId NCBI Taxonomy ID.  If TaxonomyID is not available,
     *                       use the constant
     *                       CPathRecord.TAXONOMY_NOT_SPECIFIED.
     * @param type           CPathRecordType Object.
     * @param specificType   Specific Type of Record.  The value of this
     *                       field should be chosen from BioPaxConstants.java.
     * @param xmlType        XmlRecordType Object.
     * @param xml            XML Content
     * @return cPath Id for newly saved record
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized long addRecord(String name, String description,
            int ncbiTaxonomyId, CPathRecordType type, String specificType,
            XmlRecordType xmlType, String xml)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = this.getStatement(con, INSERT_KEY);
            pstmt.setString(1, name);
            pstmt.setString(2, description);
            pstmt.setString(3, type.toString());
            pstmt.setString(4, specificType);
            pstmt.setInt(5, ncbiTaxonomyId);
            pstmt.setString(6, xmlType.toString());
            pstmt.setString(7, xml);
            java.util.Date now = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(now.getTime());
            pstmt.setDate(8, sqlDate);
            pstmt.executeUpdate();

            //  Get New CPath ID
            pstmt = getStatement(con, GET_MAX_ID_KEY);
            rs = pstmt.executeQuery();
            rs.next();
            long cpathId = rs.getLong(1);
            return cpathId;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            this.localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * Adds Specified Record to the cPath Table.
     *
     * @param name           Enity Name
     * @param description    Enty Description
     * @param ncbiTaxonomyId NCBI Taxonomy ID.
     * @param type           CPathRecordType Object.
     * @param specificType   Specific Type of Record.  The value of this
     *                       field should be chosen from BioPaxConstants.java.
     * @param xmlType        XMLRecordType Object.
     * @param xml            XML Content
     * @param refs           Array of External References
     * @return cPath Id for newly saved record
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized long addRecord(String name, String description,
            int ncbiTaxonomyId, CPathRecordType type, String specificType,
            XmlRecordType xmlType, String xml, ExternalReference refs[])
            throws DaoException {
        long cpathId = this.addRecord(name, description, ncbiTaxonomyId,
                type, specificType, xmlType, xml);
        DaoExternalLink linker = DaoExternalLink.getInstance();
        linker.addMulipleRecords(cpathId, refs, false);
        return cpathId;
    }

    /**
     * Gets all cPath Records.
     *
     * @return ArrayList of CPath Records.
     * @throws DaoException        Error Retrieving Data.
     * @throws java.io.IOException Error Performing I/O.
     */
    public ArrayList getAllRecords() throws DaoException, IOException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList records = new ArrayList();
        try {
            con = getConnection();
            pstmt = this.getStatement(con, GET_ALL_KEY);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CPathRecord record = extractRecord(rs);
                records.add(record);
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
        return records;
    }

    /**
     * Gets all cPath Records of Specified Type.
     *
     * @param recordType CPathRecordType Object
     * @return ArrayList of CPath Records.
     * @throws DaoException        Error Retrieving Data.
     * @throws java.io.IOException Error Performing I/O.
     */
    public ArrayList getAllRecords(CPathRecordType recordType)
            throws DaoException, IOException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ArrayList records = new ArrayList();
        try {
            con = getConnection();
            pstmt = getStatement(con, GET_ALL_BY_TYPE_KEY);
            pstmt.setString(1, recordType.toString());
            rs = pstmt.executeQuery();
            while (rs.next()) {
                CPathRecord record = extractRecord(rs);
                records.add(record);
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
        return records;
    }

    /**
     * Gets Records by Taxonomy ID.
     *
     * @param recordType CPathRecordType.
     * @param taxonomyId NCBI Taxonomy ID.
     * @return ArrayList of CPath Record Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getRecordByTaxonomyID(CPathRecordType recordType,
            int taxonomyId) throws DaoException {
        ArrayList records = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = this.getConnection();
            pstmt = getStatement(con, GET_ALL_BY_TAX_ID_KEY);
            pstmt.setString(1, recordType.toString());
            pstmt.setInt(2, taxonomyId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                records.add(extractRecord(rs));
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
        return records;
    }

    /**
     * Gets a complete list of all TaxonomyIds stored in the database.
     * This effectively returns a list of all indexed organisms.
     *
     * @return ArrayList of Integer NCBI Taxonomy Ids.
     * @throws DaoException Database Access Error.
     */
    public ArrayList getAllTaxonomyIds() throws DaoException {
        ArrayList taxonomyList = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, GET_TAX_IDS_KEY);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                int taxId = rs.getInt("NCBI_TAX_ID");
                taxonomyList.add(new Integer(taxId));
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
        return taxonomyList;
    }

    /**
     * Gets Record by specified CPath ID.
     *
     * @param cpathId cPath ID.
     * @return cPath Record.
     * @throws DaoException Error Retrieving Data.
     */
    public synchronized CPathRecord getRecordById(long cpathId)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, GET_BY_ID_KEY);
            pstmt.setLong(1, cpathId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return this.extractRecord(rs);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Record by specified Name.
     *
     * @param name Name.
     * @return cPath Record.
     * @throws DaoException Error Retrieving Data.
     */
    public CPathRecord getRecordByName(String name) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, GET_BY_NAME_KEY);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return this.extractRecord(rs);
            } else {
                return null;
            }
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            this.localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * get the highest cpath ID
     * 
     * @return the highest cpath ID
     * @throws DaoException is there are problems accessing the database
     */
    public long getMaxCpathID() throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        long maxId = -1;
        try {
            
            con = getConnection();
            pstmt = getStatement(con, SELECT_MAX_CPATH_ID_KEY);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                maxId = rs.getLong(1);
            }

        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
        return maxId;
    }
    
    /**
     * Deletes cPath Record with the specified CPATH_ID.
     * This will also delete all external links associated with this record.
     *
     * @param cpathId cPath ID of record to delete.
     * @return returns true if deletion was successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(long cpathId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            //  Step 1:  Delete the Primary Record
            con = getConnection();
            pstmt = getStatement(con, DELETE_BY_ID_KEY);
            pstmt.setLong(1, cpathId);
            int rows = pstmt.executeUpdate();

            //  Step 2:  Delete all Associted External Links
            DaoExternalLink linker = DaoExternalLink.getInstance();
            ArrayList links = linker.getRecordsByCPathId(cpathId);
            for (int i = 0; i < links.size(); i++) {
                ExternalLinkRecord link = (ExternalLinkRecord) links.get(i);
                linker.deleteRecordById(link.getId());
            }

            //  Step 3: Delete all Associated Internal Links
            DaoInternalLink internalLinker = new DaoInternalLink();
            internalLinker.deleteRecordsByCPathId(cpathId);

            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            this.localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * Updates XML Content for CPath Record.
     *
     * @param cpathId cPath Id.
     * @param newXml  New XML Content.
     * @return true indicates success.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean updateXml(long cpathId, String newXml) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, UPDATE_XML_KEY);
            pstmt.setString(1, newXml);
            java.util.Date now = new java.util.Date();
            java.sql.Date sqlDate = new java.sql.Date(now.getTime());
            pstmt.setDate(2, sqlDate);
            pstmt.setLong(3, cpathId);
            int rows = pstmt.executeUpdate();
            return (rows > 0) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * Extracts cPath Record from Result Set.
     *
     * @param rs ResultSet Object.
     * @return cPath Record Object.
     * @throws SQLException Error Connecting to database.
     */
    public CPathRecord extractRecord(ResultSet rs) throws SQLException {
        CPathRecord record = new CPathRecord();
        record.setId(rs.getLong("CPATH_ID"));
        record.setName(rs.getString("NAME"));
        record.setDescription(rs.getString("DESC"));
        record.setType(CPathRecordType.getType(rs.getString("TYPE")));
        record.setSpecType(rs.getString("SPECIFIC_TYPE"));
        record.setNcbiTaxonomyId(rs.getInt("NCBI_TAX_ID"));
        record.setXmlType(XmlRecordType.getType(rs.getString("XML_TYPE")));
        record.setXmlContent(rs.getString("XML_CONTENT"));
        record.setCreateTime(rs.getDate("CREATE_TIME"));
        record.setUpdateTime(rs.getDate("UPDATE_TIME"));
        return record;
    }
}
