package org.mskcc.pathdb.sql.dao;

import org.mskcc.dataservices.bio.ExternalReference;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.live.DataServiceFactory;
import org.mskcc.dataservices.protocol.GridProtocol;
import org.mskcc.dataservices.services.ReadInteractors;
import org.mskcc.pathdb.sql.JdbcUtil;
import org.mskcc.pathdb.util.CPathConstants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data Access Object for the GRID Interactor Table.
 *
 * @author Ethan Cerami
 */
public class DaoInteractor {

    /**
     * Looks up local Ids for all Interactors.  We need localIds,
     * because localIds are used to store interactions.
     * Local IDs are stored with the attribute key:  InteractorVocab.LOCAL_ID.
     * @param interactors ArrayList of Interactors.
     * @return HashMap of Names to LocalIds.
     * @throws DataServiceException Error Connecting to data service.
     * @throws EmptySetException No Results Found.
     */
    public HashMap getLocalInteractorIds(ArrayList interactors)
            throws DataServiceException, EmptySetException {
        HashMap map = new HashMap();
        DataServiceFactory factory = DataServiceFactory.getInstance();
        for (int i = 0; i < interactors.size(); i++) {
            Interactor interactor = (Interactor) interactors.get(i);
            String name = interactor.getName();
            ReadInteractors service = (ReadInteractors) factory.getService
                    (CPathConstants.READ_INTERACTORS_FROM_GRID);
            try {
                Interactor dbInteractor = service.getInteractor(name);
                String localId = (String) dbInteractor.getAttribute
                        (InteractorVocab.LOCAL_ID);
                map.put(name, localId);
            } catch (EmptySetException e) {
                throw new EmptySetException();
            }
        }
        return map;
    }

    /**
     * Gets All Interactors.
     * @return ArrayList of Interactors.
     * @throws DaoException Error Retrieving Data.
     */
    public ArrayList getAllInteractors() throws DaoException {
        ArrayList list = new ArrayList();
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = JdbcUtil.getGridConnection();
            pstmt = con.prepareStatement
                    ("select * from orf_info");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                Interactor interactor = new Interactor();
                interactor.setName(rs.getString("orf_name"));
                interactor.addAttribute(InteractorVocab.LOCAL_ID,
                        Integer.toString(rs.getInt("id")));
                String dbIds = rs.getString("external_ids");
                String dbNames = rs.getString("external_names");
                String idArray[] = GridProtocol.splitString(dbIds);
                String nameArray[] = GridProtocol.splitString(dbNames);
                ExternalReference refs[] = new ExternalReference
                        [idArray.length];
                for (int i = 0; i < idArray.length; i++) {
                    refs[i] = new ExternalReference(nameArray[i], idArray[i]);
                }
                interactor.setExternalRefs(refs);
                list.add(interactor);
            }
            return list;
        } catch (ClassNotFoundException e) {
            throw new DaoException("ClassNotFoundException:  "
                    + e.getMessage());
        } catch (SQLException e) {
            throw new DaoException("SQLException:  " + e.getMessage());
        } finally {
            JdbcUtil.closeAll(con, pstmt, rs);
        }
    }
}