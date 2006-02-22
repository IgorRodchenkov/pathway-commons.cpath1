// $Id: DaoExternalLink.java,v 1.27 2006-02-22 22:47:51 grossb Exp $
//------------------------------------------------------------------------------
/** Copyright (c) 2006 Memorial Sloan-Kettering Cancer Center.
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

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.schemas.psi.DbReferenceType;
import org.mskcc.dataservices.schemas.psi.ProteinInteractorType;
import org.mskcc.dataservices.schemas.psi.XrefType;
import org.mskcc.pathdb.model.CPathRecord;
import org.mskcc.pathdb.model.CPathRecordType;
import org.mskcc.pathdb.model.ExternalDatabaseRecord;
import org.mskcc.pathdb.model.ExternalLinkRecord;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Data Access Object to the External Link Table.
 * <P>
 * <B>Data Synchronization:</B>  As currently constructed, CPath maintains
 * external references for PHYSICAL_ENTITY records in two places:
 * the EXTERNAL_LINK table, and the embedded XML document in the CPATH table.
 * This XML is currently encoded in PSI-MI XML format, but in the future,
 * it will be encoded in BioPax XML format.  CPath records other than
 * PHYSICAL_ENTITY records do *not* have this data integrity requirement.
 * <P>
 * The DaoExternalLink class is currently responsible for enforcing the data
 * integrity requirement specified above.  However, there are two scenarios
 * for data synchronization:
 * <UL>
 * <LI>Scenario 1:  New interactor data is imported.  In this case, external
 * references already exist in the XML, and we only need to add external
 * references to the EXTERNAL_LINK table.  No synchronization is needed.
 * To follow this procedure, set the synchronizeXml parameter to false when
 * calling addRecord() or addMulipleRecords().
 * <LI>Scenario 2:  Updated interactor data is imported.  In this case, new
 * external references are added to both the EXTERNAL_LINK table and the
 * embedded XML.  This class assumes that all newly imported external links
 * are in fact new, and that checking for pre-existing or duplicate references
 * is done elsewhere.   To follow this procedure, set the synchronizeXml
 * parameter to false when calling addRecord() or addMulipleRecords().
 * </UL>
 *
 * @author Ethan Cerami
 */
public class DaoExternalLink extends ManagedDAO {
    private static DaoExternalLink daoExternalLink;

    private static final String INSERT_KEY = "INSERT_KEY";
    private static final String INSERT =
            "INSERT INTO external_link (`CPATH_ID`, "
            + "`EXTERNAL_DB_ID`, `LINKED_TO_ID`)"
            + " VALUES (?,?,?)";

    private static final String GET_BY_LINK_ID_KEY = "GET_BY_LINK_ID_KEY";
    private static final String GET_BY_LINK_ID =
            "SELECT * FROM external_link WHERE EXTERNAL_LINK_ID = ?";

    private static final String GET_BY_DB_ID_KEY = "GET_BY_DB_ID_KEY";
    private static final String GET_BY_DB_ID =
            "SELECT * FROM external_link WHERE "
            + "EXTERNAL_DB_ID = ? AND LINKED_TO_ID =?";

    private static final String GET_BY_DB_KEY = "GET_BY_DB_KEY";
    private static final String GET_BY_DB =
            "SELECT * FROM external_link WHERE"
            + " EXTERNAL_DB_ID = ?";

    private static final String GET_BY_CPATH_ID_KEY = "GET_BY_CPATH_ID_KEY";
    private static final String GET_BY_CPATH_ID =
            "SELECT * FROM external_link WHERE CPATH_ID = ? "
            + "ORDER BY EXTERNAL_LINK_ID";

    private static final String DELETE_BY_ID_KEY = "DELETE_BY_ID_KEY";
    private static final String DELETE_BY_ID =
            "DELETE FROM external_link WHERE EXTERNAL_LINK_ID = ?";

    private static final String RECORD_EXISTS_KEY = "RECORD_EXISTS_KEY";
    private static final String RECORD_EXISTS =
            "SELECT EXTERNAL_LINK_ID FROM external_link WHERE "
            + "CPATH_ID = ? AND EXTERNAL_DB_ID = ? AND "
            + "LINKED_TO_ID = ?";

    /**
     * Private Constructor (Singleton pattern).
     */
    private DaoExternalLink() {
    }

    /**
     * Gets Instance of Dao Object. (Singleton pattern).
     *
     * @return DaoCPath Object.
     * @throws DaoException Dao Initialization Error.
     */
    public static DaoExternalLink getInstance() throws DaoException {
        if (daoExternalLink == null) {
            daoExternalLink = new DaoExternalLink();
            daoExternalLink.init();
        }
        return daoExternalLink;
    }

    /**
     * Initialize DAO Prepared Statement Objects.
     *
     * @throws DaoException Dao Initialization Error.
     */
    protected void init() throws DaoException {
        super.init();
        addPreparedStatement(INSERT_KEY, INSERT);
        addPreparedStatement(GET_BY_LINK_ID_KEY, GET_BY_LINK_ID);
        addPreparedStatement(GET_BY_DB_ID_KEY, GET_BY_DB_ID);
        addPreparedStatement(GET_BY_DB_KEY, GET_BY_DB);
        addPreparedStatement(GET_BY_CPATH_ID_KEY, GET_BY_CPATH_ID);
        addPreparedStatement(DELETE_BY_ID_KEY, DELETE_BY_ID);
        addPreparedStatement(RECORD_EXISTS_KEY, RECORD_EXISTS);
    }

    /**
     * Adds New External Link Record.
     * See class comments for details regarding data/xml synchronization.
     *
     * @param link           ExternalLinkRecord Object.
     * @param synchronizeXml Synchronize XML in CPath Record (true or false).
     * @return true if saved successfully.
     * @throws DaoException Error Saving Data.
     */
    public boolean addRecord(ExternalLinkRecord link, boolean synchronizeXml)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        //  Validate Incoming Links are not Null or Empty, part of bug #0000508.
        //  JUnit Test occurs in TestDaoExternalLinks.testEmptyIds();
        String linkedToId = link.getLinkedToId();
        if (linkedToId == null || linkedToId.length() == 0) {
            throw new IllegalArgumentException("External Reference "
                    + "ID is null or empty.");
        }
        try {
            con = getConnection();
            pstmt = getStatement(con, INSERT_KEY);
            pstmt.setLong(1, link.getCpathId());
            pstmt.setInt(2, link.getExternalDbId());
            pstmt.setString(3, link.getLinkedToId());
            int rows = pstmt.executeUpdate();
            if (synchronizeXml) {
                synchronizeXml(link);
            }
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
     * Adds All External References to Database.
     * See class comments for details regarding data/xml synchronization.
     *
     * @param cpathId        cPath ID.
     * @param refs           Array of External Reference Objects.
     * @param synchronizeXml Synchronize XML in CPath Record (true or false).
     * @throws DaoException Error Retrieving Data.
     */
    public void addMulipleRecords(long cpathId, ExternalReference refs[],
            boolean synchronizeXml)
            throws DaoException {
        if (refs != null) {
            for (int i = 0; i < refs.length; i++) {
                String dbName = refs[i].getDatabase();
                String id = refs[i].getId();

                DaoExternalDb dao = new DaoExternalDb();
                ExternalDatabaseRecord dbRecord = dao.getRecordByTerm(dbName);

                if (dbRecord != null) {
                    ExternalLinkRecord link = new ExternalLinkRecord();
                    link.setExternalDatabase(dbRecord);
                    link.setCpathId(cpathId);
                    link.setLinkedToId(id);
                    addRecord(link, synchronizeXml);
                }
            }
        }
    }

    /**
     * Validates all External References.
     *
     * @param refs Array of External Reference objects.
     * @return true is all ref.getDatabase() items match.
     * @throws DaoException Error Retrieving Data.
     * @throws ExternalDatabaseNotFoundException
     *                      Database Not Found.
     */
    public boolean validateExternalReferences(ExternalReference refs[])
            throws DaoException, ExternalDatabaseNotFoundException {
        if (refs != null) {
            for (int i = 0; i < refs.length; i++) {
                String dbName = refs[i].getDatabase();
                String id = refs[i].getId();

                //  Checks for Null or Empty DB Name, part of bug:  0000508.
                if (dbName == null || dbName.length() == 0) {
                    throw new ExternalDatabaseNotFoundException
                            ("Database name is null or empty");
                }
                //  Checks for Null or Empty DB ID, part of bug:  0000508.
                if (id == null || id.length() == 0) {
                    throw new ExternalDatabaseNotFoundException
                            ("Database ID is null or empty");
                }

                DaoExternalDb dao = new DaoExternalDb();
                ExternalDatabaseRecord exDb =
                        dao.getRecordByTerm(refs[i].getDatabase());
                if (exDb == null) {
                    throw new ExternalDatabaseNotFoundException
                            ("No matching database "
                            + "found for:  " + dbName + " [" + id + "]");
                }
            }
        }
        return true;
    }

    /**
     * Looks Up the cPath Records that matches the specified External Reference.
     *
     * @param ref An External Reference.
     * @return ArrayList of Matching CPath Records.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList lookUpByExternalRef(ExternalReference ref)
            throws DaoException {
        ExternalReference refs[] = new ExternalReference[1];
        refs[0] = ref;
        return lookUpByExternalRefs(refs);
    }

    /**
     * Looks Up the cPath Record that matches any of the specified External
     * References.
     *
     * @param refs An Array of External References.  All these references
     *             refer to the same interactor, as defined in different
     *             databases.
     * @return ArrayList of Matching CPath Records.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList lookUpByExternalRefs(ExternalReference refs[])
            throws DaoException {
        ArrayList records = new ArrayList();
        //  Iterate through all External References.
        if (refs != null) {
            for (int i = 0; i < refs.length; i++) {
                String dbName = refs[i].getDatabase();
                String linkedToId = refs[i].getId();

                // Find matching Database (if available).
                DaoExternalDb dao = new DaoExternalDb();
                ExternalDatabaseRecord externalDb = dao.getRecordByTerm(dbName);
                if (externalDb != null) {
                    //  Find Record(s) that already uses this DbId
                    //  and linkedToId.
                    ArrayList links = this.getRecordByDbAndLinkedToId
                            (externalDb.getId(), linkedToId);
                    //  Retrieve the Associated CPath Records.
                    for (int j = 0; j < links.size(); j++) {
                        ExternalLinkRecord externalLink = (ExternalLinkRecord)
                                links.get(j);
                        long cpathId = externalLink.getCpathId();
                        DaoCPath cpathDao = DaoCPath.getInstance();
                        CPathRecord record = cpathDao.getRecordById(cpathId);
                        records.add(record);
                    }
                }
            }
        }
        return records;
    }

    /**
     * Gets Record by specified External Link ID.
     *
     * @param externalLinkId External Link ID.
     * @return External Link Object.
     * @throws DaoException Error Retrieving Data.
     */
    public ExternalLinkRecord getRecordById(long externalLinkId)
            throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, GET_BY_LINK_ID_KEY);
            pstmt.setLong(1, externalLinkId);
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
            localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * Gets Record that matches specified ExternalDbId and LinkedToId.
     *
     * @param externalDbId External Database ID.
     * @param linkedToId   Linked To ID String.
     * @return Array List of External Link Objects.
     * @throws DaoException Error Retrieving Data.
     */
    private ArrayList getRecordByDbAndLinkedToId(long externalDbId,
            String linkedToId) throws DaoException {
        ArrayList externalLinks = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            if (linkedToId != null) {
                pstmt = getStatement(con, GET_BY_DB_ID_KEY);
                pstmt.setLong(1, externalDbId);
                pstmt.setString(2, linkedToId);
            } else {
                pstmt = getStatement(con, GET_BY_DB_KEY);
                pstmt.setLong(1, externalDbId);
            }
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ExternalLinkRecord externalLink = this.createBean(rs);
                externalLinks.add(externalLink);
            }
            return externalLinks;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * Gets All External Link Records Associated with specified CPath ID.
     *
     * @param cpathId CPath ID.
     * @return ArrayList of External Link Objects.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getRecordsByCPathId(long cpathId) throws DaoException {
        ArrayList links = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, GET_BY_CPATH_ID_KEY);
            pstmt.setLong(1, cpathId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                ExternalLinkRecord link = createBean(rs);
                links.add(link);
            }
            return links;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
    }

    /**
     * Deletes Record specified by External Link ID.
     *
     * @param externalLinkId External Link ID.
     * @return true if deletion is successful.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean deleteRecordById(long externalLinkId) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, DELETE_BY_ID_KEY);
            pstmt.setLong(1, externalLinkId);
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
     * Determines if the specified record already exists.
     *
     * @param link ExternalLinkRecord Object.
     * @return true if record already exists in database.
     * @throws DaoException Error Retrieving Data.
     */
    public boolean recordExists(ExternalLinkRecord link) throws DaoException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            pstmt = getStatement(con, RECORD_EXISTS_KEY);
            pstmt.setLong(1, link.getCpathId());
            pstmt.setInt(2, link.getExternalDbId());
            pstmt.setString(3, link.getLinkedToId());
            rs = pstmt.executeQuery();
            return (rs.next()) ? true : false;
        } catch (ClassNotFoundException e) {
            throw new DaoException(e);
        } catch (SQLException e) {
            throw new DaoException(e);
        } finally {
            localCloseAll(con, pstmt, rs);
        }
    }

    private ExternalLinkRecord createBean(ResultSet rs) throws SQLException,
            DaoException {
        ExternalLinkRecord link = new ExternalLinkRecord();
        link.setId(rs.getInt("EXTERNAL_LINK_ID"));
        link.setCpathId(rs.getInt("CPATH_ID"));
        link.setExternalDbId(rs.getInt("EXTERNAL_DB_ID"));
        link.setLinkedToId(rs.getString("LINKED_TO_ID"));

        //  Get Associated External Database Record
        DaoExternalDb table = new DaoExternalDb();
        ExternalDatabaseRecord db = table.getRecordById(link.getExternalDbId());
        link.setExternalDatabase(db);
        return link;
    }

    /**
     * Synchonizes New External Link with Embedded XML.
     *
     * @param externalLink ExternalLink Object.
     * @throws DaoException Data Access Exception.
     */
    private void synchronizeXml(ExternalLinkRecord externalLink)
            throws DaoException {
        //  Get CPath Record with Current XML
        DaoCPath cpath = DaoCPath.getInstance();
        CPathRecord record = cpath.getRecordById(externalLink.getCpathId());

        CPathRecordType recordType = record.getType();
        if (recordType.equals(CPathRecordType.PHYSICAL_ENTITY)) {
            //  Transform XML to Castor Objects
            String xml = record.getXmlContent();
            StringReader reader = new StringReader(xml);
            try {
                ProteinInteractorType protein = ProteinInteractorType.
                        unmarshalProteinInteractorType(reader);
                //  Add new Ref to XML document
                XrefType xref = protein.getXref();
                DbReferenceType secondaryRef = new DbReferenceType();
                String cvTerm = externalLink.getExternalDatabase()
                        .getMasterTerm();
                secondaryRef.setDb(cvTerm);
                secondaryRef.setId(externalLink.getLinkedToId());
                xref.addSecondaryRef(secondaryRef);

                //  Then, update XML
                StringWriter writer = new StringWriter();
                protein.marshal(writer);
                cpath.updateXml(externalLink.getCpathId(), writer.toString());
            } catch (MarshalException e) {
                throw new DaoException(e);
            } catch (ValidationException e) {
                throw new DaoException(e);
            }
        }
    }
}
