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
package org.mskcc.pathdb.servlet;

import org.apache.xmlrpc.XmlRpcServer;
import org.mskcc.pathdb.xmlrpc.SubmitData;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Handles all incoming XML-RPC Requests.
 *
 * @author Ethan Cerami
 */
public class XmlRpcServlet extends HttpServlet {
    private XmlRpcServer xmlrpc;

    /**
     * Initializes Servlet.
     *
     * @param config Servlet Configuration Object.
     * @throws ServletException All Servlet Errors.
     */
    public void init(ServletConfig config) throws ServletException {
        xmlrpc = new XmlRpcServer();
        xmlrpc.addHandler("import", new SubmitData());
    }

    /**
     * Handles Client Request.
     *
     * @param req Http Servlet Request.
     * @param res Http Servlet Response.
     * @throws ServletException All Servlet Errors.
     * @throws IOException      All Input/Output Errors.
     */
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        byte[] result = xmlrpc.execute(req.getInputStream());
        res.setContentType("text/xml");
        res.setContentLength(result.length);
        OutputStream output = res.getOutputStream();
        output.write(result);
        output.flush();
    }
}