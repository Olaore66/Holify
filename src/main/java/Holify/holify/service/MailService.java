//package Holify.holify.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class MailService {
//    private final JavaMailSender mailSender;
//
//    public void sendMail(String to, String subject, String body) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//        mailSender.send(message);
//    }
//}


package Holify.holify.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MailService {
    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    public void sendMail(String to, String subject, String body) throws IOException {
        Email from = new Email("abrahamiborida@gmail.com"); // Verified sender
        Email recipient = new Email(to);
        Content content = new Content("text/plain", body);
        Mail mail = new Mail(from, subject, recipient, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            if (response.getStatusCode() != 202) {
                throw new IOException("Email failed: " + response.getStatusCode() + " - " + response.getBody());
            }
        } catch (IOException ex) {
            throw new IOException("Failed to send email: " + ex.getMessage());
        }
    }

    // For OTP (from prior chat, Sept 29, 2025)
    public void sendOtpMail(String to, String otp) throws IOException {
        sendMail(to, "Your Holify OTP", "Your OTP is: " + otp);
    }
}