package com.footballbettingcore.mail;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import java.util.Properties;

public class SendEmail {
    private final static String SENDING_EMAIL_ACC = System.getenv("SENDING_EMAIL_ACC");
    private final static String SENDING_EMAIL_PASS = System.getenv("SENDING_EMAIL_PASS");
    public final static String ADMIN_EMAIL = System.getenv("ADMIN_EMAIL");

    private static Session session;

    static {
        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDING_EMAIL_ACC, SENDING_EMAIL_PASS);
                    }
                });
    }


    public static boolean sendOutEmail(String subject, String body, String to) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDING_EMAIL_ACC));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            return true;

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        sendOutEmail("Hey, just testing", "Hi", SendEmail.ADMIN_EMAIL);
    }
}
