package org.mskcc.pathdb.controller;

import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.test.TestConstants;
import org.mskcc.pathdb.format.PsiFormatter;
import org.mskcc.pathdb.xml.psi.Entry;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * Data Service Controller.
 *
 * @author cerami
 */
public class DataServiceController {
    /**
     * Servlet Request.
     */
    private HttpServletRequest request;

    /**
     * Servlet Response.
     */
    private HttpServletResponse response;

    /**
     * UID Specified by Client.
     */
    private String uid;

    /**
     * Constructor.
     * @param request Servlet Request.
     * @param response Servlet Response.
     */
    public DataServiceController (HttpServletRequest request,
            HttpServletResponse response) {
        this.request = request;
        this.response = response;
        uid = request.getParameter("uid");
    }

    /**
     * Executes the Controller.
     */
    public void execute () {
        try {
            retrieveInteractions();
        } catch (Exception e) {
            try {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.println("Error:  " + e.getMessage());
                out.println("<PRE>");
                e.printStackTrace(out);
                out.println("</PRE>");
            } catch (IOException e1) {
                System.out.println(e1);
            }
        }
    }

    /**
     * Retrieves Interactions.
     * @throws Exception All Exceptions.
     */
    private void retrieveInteractions() throws Exception {
        GridInteractionService service = new GridInteractionService
                (TestConstants.DB_HOST, TestConstants.USER,
                        TestConstants.PASSWORD);
        ArrayList interactions =
                service.getInteractions(uid);
        PsiFormatter formatter = new PsiFormatter(interactions);
        Entry entry = formatter.getPsiXml();
        StringWriter writer = new StringWriter();
        entry.marshal(writer);
        PrintWriter out = response.getWriter();
        response.setContentType("text/xml");
        out.println(writer.toString());
    }
}