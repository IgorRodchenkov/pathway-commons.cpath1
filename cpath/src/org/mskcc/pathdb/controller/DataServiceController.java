package org.mskcc.pathdb.controller;

import org.mskcc.pathdb.format.PsiFormatter;
import org.mskcc.pathdb.sql.GridInteractionService;
import org.mskcc.pathdb.test.TestConstants;
import org.mskcc.pathdb.xml.psi.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

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
    public DataServiceController(HttpServletRequest request,
            HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    /**
     * Executes the Controller.
     */
    public void execute() {
        try {
            ProtocolRequest protocolRequest =
                    new ProtocolRequest(request.getParameterMap());
            ProtocolValidator validator =
                    new ProtocolValidator(protocolRequest);
            validator.validate();
            retrieveInteractions(protocolRequest.getUid());
        } catch (ProtocolException e) {
            response.setContentType("text/xml");
            try {
                PrintWriter out = response.getWriter();
                out.println(e.toXml());
            } catch (IOException e1) {
                System.out.println(e1);
            }
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
     * @param uid UID.
     * @throws Exception All Exceptions.
     */
    private void retrieveInteractions(String uid) throws Exception {
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