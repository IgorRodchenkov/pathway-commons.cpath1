package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mskcc.pathdb.xdebug.XDebug;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Action for Main cPath Home Page.
 *
 * @author Ethan Cerami
 */
public class HomeAction extends BaseAction {
    /**
     * Request Attribute:  NUM_INTERACTIONS.
     */
    public static final String NUM_INTERACTIONS = "num_interactions";

    /**
     * Request Attribute:  NUM_PHYSICAL_ENTITIES.
     */
    public static final String NUM_PHYSICAL_ENTITIES =
            "num_physical_entities";

    /**
     * Executes Home Page.
     * @param mapping Struts ActionMapping Object.
     * @param form Struts ActionForm Object.
     * @param request Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}
