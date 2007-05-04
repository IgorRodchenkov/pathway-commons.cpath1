package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.form.FeedbackForm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Feedback Action.
 *
 * @author Ethan Cerami
 */
public class FeedbackAction extends BaseAction {

    /**
     * Executes Home Page.
     *
     * @param mapping  Struts ActionMapping Object.
     * @param form     Struts ActionForm Object.
     * @param request  Http Servlet Request.
     * @param response Http Servlet Response.
     * @param xdebug   XDebug Object.
     * @return Struts Action Forward Object.
     * @throws Exception All Exceptions.
     */
    public ActionForward subExecute(ActionMapping mapping,
            ActionForm form, HttpServletRequest request,
            HttpServletResponse response, XDebug xdebug) throws Exception {

        FeedbackForm feedbackForm = (FeedbackForm) form;
        xdebug.logMsg(this, "Email address:  " + feedbackForm.getEmail());
        xdebug.logMsg(this, "Subject:  " + feedbackForm.getSubject());

        setUserMessage(request, "Thanks for your feedback!  Your message has been sent.");
        //  TODO:  Send out email here...
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}