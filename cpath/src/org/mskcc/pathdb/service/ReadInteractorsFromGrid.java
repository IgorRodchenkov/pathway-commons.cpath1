/** Copyright (c) 2003 Institute for Systems Biology, University of
 ** California at San Diego, and Memorial Sloan-Kettering Cancer Center.
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
 ** documentation provided hereunder is on an "as is" basis, and the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** Institute for Systems Biology, the University of California at San Diego
 ** and/or Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** Institute for Systems Biology, the University of California at San
 ** Diego and/or Memorial Sloan-Kettering Cancer Center
 ** have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 **
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.mskcc.pathdb.service;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.contrib.input.ResultSetBuilder;
import org.jdom.output.XMLOutputter;
import org.mskcc.dataservices.bio.Interactor;
import org.mskcc.dataservices.bio.vocab.InteractorVocab;
import org.mskcc.dataservices.core.DataServiceException;
import org.mskcc.dataservices.core.EmptySetException;
import org.mskcc.dataservices.live.DataServiceBase;
import org.mskcc.dataservices.protocol.GridProtocol;
import org.mskcc.dataservices.services.ReadInteractors;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Reads Interactors from a local GRID Database.
 *
 * @author cerami
 */
public class ReadInteractorsFromGrid extends DataServiceBase
        implements ReadInteractors {

    /**
     * HashMap of ORF Names to Local IDs.
     */
    private static HashMap localIdToNameMap = new HashMap();

    /**
     * Gets Interactor object for specified ORF.
     * @param orfName ORF Name.
     * @return Interactor object.
     * @throws DataServiceException Indicates Error connecting to data service.
     */
    public Interactor getInteractor(String orfName)
            throws DataServiceException {
        try {
            Interactor interactor = getLiveInteractor
                    (orfName, GridProtocol.KEY_ORF);
            return interactor;
        } catch (EmptySetException e) {
            throw  e;
        } catch (Exception e) {
            throw new DataServiceException(e);
        }
    }

    /**
     * Gets Interactor object for specified Local ID.
     * @param localId Unique Local ID.
     * @return Interactor object.
     * @throws DataServiceException Indicates Error connecting to data service.
     */
    public Interactor getInteractorByLocalId(String localId)
            throws DataServiceException {
        Interactor interactor;
        try {
            String orfName = (String) localIdToNameMap.get(localId);
            if (orfName != null) {
                interactor = this.getInteractor(orfName);
            } else {
                interactor = getLiveInteractor
                        (localId, GridProtocol.KEY_LOCAL_ID);
            }
            return interactor;
        } catch (Exception e) {
            throw new DataServiceException(e);
        }
    }

    /**
     * Gets Live Interactor from GRID_LOCAL, and places in Local Cache.
     * @param uid Unique ID.
     * @param lookUpKey Database LookUp Key.
     * @return Interactor object.
     * @throws java.sql.SQLException Database error.
     * @throws java.lang.ClassNotFoundException Could not find JDBC Driver.
     */
    protected Interactor getLiveInteractor(String uid, String lookUpKey)
            throws SQLException, ClassNotFoundException, JDOMException,
            IOException, EmptySetException {
        Document doc = this.connect(uid, lookUpKey);
        GridInteractorUtil util = new GridInteractorUtil();
        Interactor interactor = util.parseResults(doc);

        //  Store Complete XML Document as an Attribute.
        XMLOutputter outputter = new XMLOutputter();
        outputter.setIndent(true);
        StringWriter writer = new StringWriter();
        outputter.output(doc, writer);
        interactor.addAttribute(InteractorVocab.XML_RESULT_SET,
                writer.toString());

        return interactor;
    }

    /**
     * Gets Live ORF Data from GRID_LOCAL.
     * @param uid Unique ID.
     * @param lookUpKey Database LookUp Key.
     * @return Database Result Set.
     * @throws java.sql.SQLException Database error.
     * @throws java.lang.ClassNotFoundException Could not find JDBC Driver.
     */
    private Document connect(String uid, String lookUpKey)
            throws SQLException, ClassNotFoundException, JDOMException,
            EmptySetException {
        Connection con = GridProtocol.getConnection(this.getLocation());
        PreparedStatement pstmt = con.prepareStatement
                ("select * from orf_info where " + lookUpKey + "=?");
        pstmt.setString(1, uid);
        ResultSet rs = pstmt.executeQuery();

        if (!rs.next()) {
            throw new EmptySetException();
        } else {
            rs = pstmt.executeQuery();
            ResultSetBuilder builder = new ResultSetBuilder(rs);
            Document document = builder.build();
            rs.close();
            return document;
        }
    }
}
