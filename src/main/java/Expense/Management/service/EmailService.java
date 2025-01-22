package Expense.Management.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendRegistrationEmail(String to, String username) {
        String subject = "Welcome to Expense Management System";
        String body = "<h1>Hello, " + username + "!</h1>"
                + "<p>Thank you for registering with the Expense Management System.</p>"
                + "<p>We are excited to have you on board. Start managing your expenses efficiently now!</p>"
                + "<br>"
                + "<p>Regards,<br>Expense Management Team</p>";

        sendEmail(to, subject, body);
    }

    @Value("${SERVER_PORT}")
    private String serverPort;

    public void sendEmailVerification(String to, String verificationToken) {
        String subject = "Email Verification - Expense Management System";
        String body = "<h1>Email Verification</h1>"
                + "<p>Thank you for registering. Please verify your email by clicking the link below:</p>"
                + "<p><a href=\"http://localhost:"+ serverPort +"/api/auth/verify-email?token=" + verificationToken + "\">Verify Email</a></p>"
                + "<br>"
                + "<p>Regards,<br>Expense Management Team</p>";

        sendEmail(to, subject, body);
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
