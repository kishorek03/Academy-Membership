package com.Academy.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    // Method to send OTP email
    public void sendOtpEmail(String email, String otp) {
        // HTML content for OTP email
        String emailBody = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            line-height: 1.6;
                            background-color: #f9f9f9;
                            color: #333;
                            margin: 0;
                            padding: 20px;
                        }
                        .container {
                            background-color: #ffffff;
                            border-radius: 8px;
                            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
                            max-width: 500px;
                            margin: auto;
                            padding: 20px;
                        }
                        .header {
                            text-align: center;
                            color: #4CAF50;
                            font-size: 24px;
                            margin-bottom: 20px;
                        }
                        .footer {
                            margin-top: 20px;
                            font-size: 12px;
                            color: #777;
                            text-align: center;
                        }
                        .button {
                            display: inline-block;
                            background: linear-gradient(90deg, #87CEEB, #00BFFF); /* Sky blue gradient */
                            color: white;
                            font-weight: bold;
                            text-decoration: none;
                            padding: 10px;
                            border-radius: 5px;
                            text-align: center;
                            margin-bottom: 20px;
                            font-size: 24px;
                            cursor: pointer;
                            transition: background 0.3s ease; /* Smooth transition for hover */
                        }
                        .button:hover {
                            background: linear-gradient(90deg, #00BFFF, #87CEEB); /* Reversed gradient for hover effect */
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">Your OTP Code for your registration</div>
                        <p>Hello User,</p>
                        <p>Thank you for registering with us. Please find your One-Time Password (OTP) below:</p>
                        <div class="button">
                            %s
                        </div>
                        <p>This OTP will expire in 3 minutes. Please enter it on the registration page to complete the process.</p>
                        <div class="footer">
                            If you did not request this OTP, please ignore this email.
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(otp);
        // Sending the HTML email
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Your OTP Code for Registration");
            helper.setText(emailBody, true); // Enable HTML content
            logger.info("Sending OTP email to: {}", email);
            mailSender.send(message);
            logger.info("OTP email sent successfully to: {}", email);
        } catch (MessagingException e) {
            logger.error("Failed to send OTP email to {}: {}", email, e.getMessage(), e);
            throw new RuntimeException("Failed to send OTP email");
        }
    }
    // Method to send HTML emails
    public void sendEmailWithHtml(String to, String subject, String htmlBody) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // Enable HTML content
            logger.info("Sending HTML email to: {}", to);
            mailSender.send(message);
            logger.info("HTML email sent successfully to: {}", to);
        } catch (MessagingException e) {
            logger.error("Failed to send HTML email to {}: {}", to, e.getMessage(), e);
            throw new RuntimeException("Failed to send HTML email");
        }
    }
}