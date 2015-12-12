package utilities.mail;

import java.util.*;
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.*;

public class MailManager {
    private static final String GMAIL_HOST = "smtp.gmail.com";
    private static final String GMAIL_USER = "websecondproject@gmail.com";
    private static final String GMAIL_PASSWORD = "webpassword1234";
    private static final String GMAIL_PORT = "465";
    private static final String GMAIL_SOCKET_FACTORY = "javax.net.ssl.SSLSocketFactory";
    
    private static final String UNITN_HOST = "mail.unitn.it";
    
    private static final String GMAIL_FROM = "websecondproject@gmail.com";
    private static final String UNITN_FROM = "websecondproject@unitn.it";
    

/*    private static final String STARTTLS = "true";
      private static final String AUTH = "true";
      private static final String TO = "pgphnpxy@sharklasers.com";
      private static final String SUBJECT = "Testing JavaMail API";
      private static final String TEXT = "This is a test message from my java application. Just ignore it";
*/
    private static final String DEBUG = "true";

    
    private static boolean insideUniversity;
    private static Session session;
    
    private MailManager() {}
    
    public static void initialize(boolean insideUniv) {
        insideUniversity = insideUniv;
        Properties props = new Properties();

        if (!insideUniversity) {
            // only for google secure connection
            props.put("mail.smtp.user", GMAIL_USER);
            props.put("mail.smtp.auth", true);
            props.put("mail.smtp.starttls.enable", true);
            props.put("mail.smtp.socketFactory.port", GMAIL_PORT);
            props.put("mail.smtp.socketFactory.class", GMAIL_SOCKET_FACTORY);
            props.put("mail.smtp.socketFactory.fallback", "false");
            
            props.put("mail.smtp.host", GMAIL_HOST);
            props.put("mail.smtp.port", GMAIL_PORT);
        }
        else {
            props.put("mail.smtp.host", UNITN_HOST);
        }
        
        props.put("mail.smtp.debug", DEBUG);
        
        session = Session.getInstance(props);
        
        if ("true".equals(DEBUG)) {
            session.setDebug(true);
        }
        
    }
    
    
    
    public static synchronized void sendMails(List<Mail> mails) {
        try {
            Transport transport = null;
            
            if (!insideUniversity) {
                transport = session.getTransport("smtp");
                transport.connect(GMAIL_HOST, GMAIL_USER, GMAIL_PASSWORD);
            }
            
            for (Mail m: mails) {
                //Construct the mail message
                MimeMessage message = new MimeMessage(session);
                message.setText(m.getText());
                message.setSubject(m.getSubject());
                message.setFrom(new InternetAddress(insideUniversity?UNITN_FROM:GMAIL_FROM));

                InternetAddress [] dest = new InternetAddress[m.getRecipients().size()];

                for (int i=0; i<m.getRecipients().size(); i++) {
                    dest[i] = new InternetAddress(m.getRecipients().get(i));
                }

                message.setRecipients(RecipientType.TO, dest);
                if (!insideUniversity) {
                    transport.sendMessage(message, dest);
                }
                else {
                    Transport.send(message);
                }
            }
            
            if (!insideUniversity) {
                transport.close();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
}
