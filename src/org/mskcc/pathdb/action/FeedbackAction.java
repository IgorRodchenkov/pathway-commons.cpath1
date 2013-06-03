package org.mskcc.pathdb.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionForm;
import org.mskcc.pathdb.xdebug.XDebug;
import org.mskcc.pathdb.form.FeedbackForm;
import org.mskcc.pathdb.form.WebUIBean;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.mskcc.pathdb.util.email.SendMail;

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


        //  Send email address
        WebUIBean webBean = CPathUIConfig.getWebUIBean();
        String smtpHost = webBean.getSmtpHost();
        String toAddress = webBean.getFeedbackEmailTo();
        String subject = webBean.getApplicationName() + " Feedback:  " + feedbackForm.getSubject();
        if (smtpHost != null && toAddress != null) {
            xdebug.logMsg(this, "Sending email...");
            SendMail.sendMail(smtpHost, feedbackForm.getEmail(),
                toAddress, subject, feedbackForm.getMessage());
            setUserMessage(request, "Thanks for your feedback!  Your message has been sent.");
        } else {
            xdebug.logMsg(this, "Could not send email.  WebUIBean does " +
                "not have the required data.");
            setUserMessage(request, "Sorry.  Your message could not be sent.  "
                + "Please try again later");
        }
        return mapping.findForward(BaseAction.FORWARD_SUCCESS);
    }
}