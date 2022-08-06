package com.footballbettingcore.mail;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.util.Properties;

import static com.footballbettingcore.mail.GetToken.*;

public class SendEmail {
    public final static String ADMIN_EMAIL = System.getenv("ADMIN_EMAIL");
    private final static String SENDING_EMAIL_ACC = System.getenv("SENDING_EMAIL_ACC");

    private static final Logger logger = LogManager.getLogger(SendEmail.class);

    public static void sendOutEmail(String subject, String body, String to) {
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            Gmail service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            // Encode as MIME message
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(SENDING_EMAIL_ACC));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(to));
            email.setSubject(subject);
            email.setText(body);

            // Encode and wrap the MIME message into a gmail message
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
            Message message = new Message();
            message.setRaw(encodedEmail);

            // Create send message
            message = service.users().messages().send("me", message).execute();
            System.out.println("Message id: " + message.getId());
            System.out.println(message.toPrettyString());
        } catch (Exception e) {
            logger.error("Exception sending mail:", e);
        }
    }

    public static void main(String[] args) {
        sendOutEmail("Hey, just testing", "Hi", SendEmail.ADMIN_EMAIL);
    }
}
