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