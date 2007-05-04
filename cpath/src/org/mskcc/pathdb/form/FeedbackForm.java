package org.mskcc.pathdb.form;

import org.apache.struts.action.*;
import org.mskcc.pathdb.servlet.CPathUIConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * Feedback Form.
 *
 * @author Ethan Cerami
 */
public class FeedbackForm extends ActionForm {
    private String email;
    private String subject;
    private String message;

    /**
     * Gets email address.
     * @return email email address.
     */
    public String getEmail () {
        return email;
    }

    /**
     * Sets email address.
     * @param email email address
     */
    public void setEmail (String email) {
        this.email = email;
    }

    /**
     * Get the subject.
     * @return subject.
     */
    public String getSubject () {
        return subject;
    }

    /**
     * Sets the subject.
     * @param subject subject.
     */
    public void setSubject (String subject) {
        this.subject = subject;
    }

    /**
     * Gets the message.
     * @return message.
     */
    public String getMessage () {
        return message;
    }

    /**
     * Sets the message.
     * @param message message.
     */
    public void setMessage (String message) {
        this.message = message;
    }

    /**
     * Validate form data.
     * @param actionMapping         ActionMapping Object.
     * @param httpServletRequest    Servlet Request.
     * @return ActionErrors List.
     */
    public ActionErrors validate (ActionMapping actionMapping,
            HttpServletRequest httpServletRequest) {
        WebUIBean uiBean = CPathUIConfig.getWebUIBean();
        ActionErrors errors = new ActionErrors();

        //  Check for required fields, and valid email address.
        if (email == null || email.length() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError ("error.feedback_form.required",
                    "Email address"));
        }
        if (subject == null || subject.length() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError ("error.feedback_form.required",
                    "Subject"));
        }
        if (message == null || message.length() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError ("error.feedback_form.required",
                    "Message"));
        }
        if (email != null && email.length() > 0
            && !email.matches(".+@.+\\.[a-z]+")) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.feedback_form.invaid_email"));
        }
        if (message != null) {
            //  Find out how many URLs the message has.
            //  If there is more than one URL that does not point to baseURL,
            //  flag it as invalid.
            int numNonBaseUrls = 0;
            String words[] = message.split(" ");
            for (int i=0; i<words.length; i++) {
                String word = words[i];
                if (word.startsWith("http://")) {
                    if (uiBean != null && uiBean.getBaseURL() != null) {
                        if (word.indexOf(uiBean.getBaseURL()) == -1) {
                            numNonBaseUrls++;
                        }
                    }
                }
            }
            if (numNonBaseUrls > 1) {
                errors.add(ActionErrors.GLOBAL_ERROR,
                        new ActionError("error.feedback_form.invaid_message"));
            }
        }
        return errors;
    }
}