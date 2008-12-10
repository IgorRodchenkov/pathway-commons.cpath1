package org.mskcc.pathdb.form;

import org.apache.struts.action.*;
import org.mskcc.pathdb.servlet.CPathUIConfig;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.PostMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Feedback Form.
 *
 * @author Ethan Cerami
 */
public class FeedbackForm extends ActionForm {

	private static final String PC_PRIVATE_RECAPTCHA_KEY = "6Ld2PgIAAAAAAINTdo1lTZQt0eZQAzPbHfAAzYmt";
	private static final String AWABI_PRIVATE_RECAPTCHA_KEY = "6LdtRAIAAAAAAHlDoc4RU8FJAV4bDcXJ5OyP0F6U";
	private static final String TORO_PRIVATE_RECAPTCHA_KEY = "6LdzRAIAAAAAALdtZ7jdAg4uhQC1JJzWewMAWkho";

    private String em;
    private String su;
    private String me;

    /**
     * Gets email address.
     * @return email email address.
     */
    public String getEmail () {
        return em;
    }
	public String getEm() { return getEmail(); }

    /**
     * Sets email address.
     * @param email email address
     */
    public void setEmail (String email) {
        this.em = email;
    }
	public void setEm(String email) { setEmail(email); }

    /**
     * Get the subject.
     * @return subject.
     */
    public String getSubject () {
        return su;
    }
	public String getSu() { return getSubject(); }

    /**
     * Sets the subject.
     * @param subject subject.
     */
    public void setSubject (String subject) {
        this.su = subject;
    }
	public void setSu(String subject) { setSubject(subject); }

    /**
     * Gets the message.
     * @return message.
     */
    public String getMessage () {
        return me;
    }
	public String getMe() { return getMessage(); }

    /**
     * Sets the message.
     * @param message message.
     */
    public void setMessage (String message) {
        this.me = message;
    }
	public void setMe(String message) { setMessage(message); }

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
        if (em == null || em.length() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError ("error.feedback_form.required",
                    "Email address"));
        }
        if (su == null || su.length() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError ("error.feedback_form.required",
                    "Subject"));
        }
        if (me == null || me.length() < 1) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError ("error.feedback_form.required",
                    "Message"));
        }
        if (em != null && em.length() > 0
            && !em.matches(".+@.+\\.[a-z]+")) {
            errors.add(ActionErrors.GLOBAL_ERROR,
                    new ActionError("error.feedback_form.invalid_email"));
        }
        if (me != null) {
            //  Find out how many URLs the message has.
            //  If there is more than one URL that does not point to baseURL,
            //  flag it as invalid.
            int numNonBaseUrls = 0;
            String words[] = me.split("\\s");
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
                        new ActionError("error.feedback_form.invalid_message"));
            }
        }

		// validate challenge
		if (!validChallengeResponse(httpServletRequest)) {
			errors.add(ActionErrors.GLOBAL_ERROR,
					   new ActionError("error.feedback_form.invalid_challenge_response", "Challenge"));
		}

        return errors;
    }

	/**
	 * Method to validate challenge response.
	 *
	 * @param httpServletRequest HttpServletRequest
	 */
//	private boolean validChallengeResponse(HttpServletRequest httpServletRequest) {

		// get the challenge field
        if (httpServletRequest != null) {
            String challenge = httpServletRequest.getParameter("recaptcha_challenge_field");
            if (challenge == null || challenge.length() == 0) return false;

            // get challenge response field
            String challengeResponse = httpServletRequest.getParameter("recaptcha_response_field");
            if (challengeResponse == null || challengeResponse.length() == 0) return false;

            // private key
            String serverName = httpServletRequest.getServerName();
            String privateKey = "";
            if (serverName.equals("www.pathwaycommons.org")) {
                privateKey = PC_PRIVATE_RECAPTCHA_KEY;
            }
            else if (serverName.equals("awabi.cbio.mskcc.org")) {
                privateKey = AWABI_PRIVATE_RECAPTCHA_KEY;
            }
            else if (serverName.equals("toro.cbio.mskcc.org")) {
                privateKey = TORO_PRIVATE_RECAPTCHA_KEY;
            }

            // validate with reCAPTCHA servers
            HttpClient client = new HttpClient();
            NameValuePair nvps[] = new NameValuePair[4];
            nvps[0] = new NameValuePair("privatekey", privateKey);
            nvps[1] = new NameValuePair("remoteip", httpServletRequest.getRemoteAddr());
            nvps[2] = new NameValuePair("challenge", challenge);
            nvps[3] = new NameValuePair("response", challengeResponse);

            // create method
            HttpMethodBase method = new PostMethod("http://api-verify.recaptcha.net/verify");
            ((PostMethod)(method)).addParameters(nvps);

            // outta here
            return executeMethod(client, method);
        } else {
            return true;
        }
    }

	/**
	 * Executes http request.
	 *
	 * @param client HttpClient
	 * @param method HttpMethodBase
	 * @return boolean
	 */
	private boolean executeMethod(HttpClient client, HttpMethodBase method) {

		try {
			// execute method
			int statusCode = client.executeMethod(method);
			if (statusCode != 200) return false;

			// read in content
			String[] content = readContent(method).split("\n");

			// outta here
			return (content[0].contains("true"));
        }
		catch (Exception e) {
            return false;
        }
	}

    /**
     * Reads content of http request.
	 * 
	 * @param method HttpMethodBase
	 * @throws java.io.IOException
     */
    private String readContent(HttpMethodBase method) throws java.io.IOException {

		// create input stream to read response
		java.io.InputStream instream = method.getResponseBodyAsStream();

		// create outputstream to write response into
		java.io.ByteArrayOutputStream outstream =  new java.io.ByteArrayOutputStream(1024);
		byte[] buffer = new byte[1024];
		int len;
		int totalBytes = 0;
		while ((len = instream.read(buffer)) > 0) {
			outstream.write(buffer, 0, len);
		}
		instream.close();

		// outta here
		return new String(outstream.toByteArray());
	}
}