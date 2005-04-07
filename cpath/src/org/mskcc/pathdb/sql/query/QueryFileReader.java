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
package org.mskcc.pathdb.sql.query;

import org.mskcc.pathdb.protocol.ProtocolRequest;
import org.mskcc.pathdb.util.security.XssFilter;

import javax.servlet.http.HttpUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Reads in a Text File of Relative URLs and returns a List of
 * Protocol Request objects.
 * <p/>
 * <P>
 * Sample file format:
 * </P>
 * <PRE>
 * # Retrieve all Human Data
 * webservice?version=1.0&cmd=get_by_interactor_tax_id&q=9606&q=&maxHits= \
 * unbounded&format=psi
 * </PRE>
 *
 * @author Ethan Cerami
 */
public class QueryFileReader {

    /**
     * Read in Text File, and extract Protocol Request Objects.
     *
     * @param fileName File Name.
     * @return ArrayList of ProtocolRequest objects.
     * @throws IOException Error Reading in File.
     */
    public ArrayList getProtocolRequests(String fileName)
            throws IOException {
        ArrayList list = new ArrayList();
        FileReader reader = new FileReader(fileName);
        BufferedReader buf = new BufferedReader(reader);
        String line = buf.readLine();
        while (line != null) {
            if (!line.startsWith("#") && line.length() > 0) {
                Hashtable params1 = HttpUtils.parseQueryString(line);
                HashMap params2 = XssFilter.filterAllParameters(params1);
                ProtocolRequest request = new ProtocolRequest(params2);
                list.add(request);
            }
            line = buf.readLine();
        }
        return list;
    }
}