package org.mskcc.pathdb.util.email;

import javax.mail.Transport;
import javax.mail.Session;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Email Utility.
 * <P>
 * Sends email messages.
 *
 * @author Ethan Cerami
 *
 */
public class SendMail {

    /**
     * Used for local testing purposes only.
     * @param args          Command line arguments (none expected)
     * @throws Exception    All Errors.
     */
    public static void main(String[] args) throws Exception{
        sendMail("cbio.mskcc.org", "cerami@cbio.mskcc.org",
                "cerami@cbio.mskcc.org", "Testing javamail plain",
                "This is a test");
    }

    /**
     * Send email.
     * @param smtpHost      SMTP Host (this method assumes login to SMTP server is not required).
     * @param from          From Address.
     * @param to            To Address.
     * @param subject       Subject.
     * @param message       Message.
     * @throws MessagingException   Email Error.
     */
    public static void sendMail (String smtpHost, String from, String to,
            String subject, String message) throws MessagingException {
        Properties props = new Properties();
        props.setProperty("mail.host", smtpHost);

        Session mailSession = Session.getDefaultInstance(props, null);

        MimeMessage mimeMessage = new MimeMessage(mailSession);
        mimeMessage.setSubject(subject);
        mimeMessage.setContent(message, "text/plain");
        mimeMessage.addRecipient(javax.mail.Message.RecipientType.TO,
             new InternetAddress(to));
        mimeMessage.setFrom(new InternetAddress(from));
		Transport.send(mimeMessage);
    }
}