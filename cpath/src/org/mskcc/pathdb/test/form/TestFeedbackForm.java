package org.mskcc.pathdb.test.form;

import junit.framework.TestCase;
import org.mskcc.pathdb.form.FeedbackForm;
import org.mskcc.pathdb.form.WebUIBean;
import org.mskcc.pathdb.servlet.CPathUIConfig;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;

import java.util.Iterator;

/**
 * Tests the Feedback Form.
 *
 * @author Ethan Cerami
 */
public class TestFeedbackForm extends TestCase {

    /**
     * Tests form validation of feedback form.
     */
    public void testFormValidation() {
        WebUIBean uiBean = new WebUIBean();
        uiBean.setBaseURL("pathwaycommons.org");
        CPathUIConfig.setWebUIBean(uiBean);
        FeedbackForm form = new FeedbackForm();

        //  Validate against valid data
        form.setEmail("joe@yahoo.com");
        form.setSubject("Great site");
        form.setMessage("Love your site!");
        ActionErrors errorList = form.validate(null, null);
        assertEquals (0, errorList.size());

        //  Validate against invalid email address
        form.setEmail("Ethan cerami");
        errorList = form.validate(null, null);
        assertEquals (1, errorList.size());

        //  Validate against invalid email address
        form.setEmail("www.yahoo.com");
        errorList = form.validate(null, null);
        assertEquals (1, errorList.size());

        //  Validate against missing field
        form.setEmail("joe@yahoo.com");
        form.setSubject("");
        errorList = form.validate(null, null);
        assertEquals (1, errorList.size());

        //  Validate against valid message w/ valid URL use.
        form.setEmail("joe@yahoo.com");
        form.setSubject("Great");
        form.setMessage("I went to this URL:  "
                + "http://www.pathwaycommons.org/pc/dbSnapshot.do?snapshot_id=4"
                + " and then, I went here:  "
                + " http://www.pathwaycommons.org/pc/dbSnapshot.do?snapshot_id=5");
        errorList = form.validate(null, null);
        assertEquals (0, errorList.size());

        //  Validate against valid message w/ valid URL use.
        form.setEmail("joe@yahoo.com");
        form.setSubject("Great");
        form.setMessage("I went to this URL:  "
                + "http://www.pathwaycommons.org/pc/dbSnapshot.do?snapshot_id=4"
                + " and then, I went here:  "
                + " http://www.yahoo.com");
        errorList = form.validate(null, null);
        assertEquals (0, errorList.size());

        //  Validate against valid message w/ invalid URL use.
        form.setEmail("joe@yahoo.com");
        form.setSubject("Great");
        form.setMessage("I went to this URL:  "
                + "http://www.google.com"
                + " and then, I went here:  "
                + " http://www.yahoo.com");
        errorList = form.validate(null, null);
        assertEquals (1, errorList.size());
    }

    /**
     * Gets Name of Test.
     *
     * @return Name of Test.
     */
    public String getName() {
        return "Test the feedback form validation rules";
    }
}
