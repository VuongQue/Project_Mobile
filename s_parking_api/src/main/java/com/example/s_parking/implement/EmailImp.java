package com.example.s_parking.implement;

import com.example.s_parking.service.EmailService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailImp implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public EmailImp(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOTP(String toEmail, String otp, String purpose) {
        String subject;
        String message;

        switch (purpose) {
            case "FORGOT_PASSWORD":
                subject = "Khôi phục mật khẩu";
                message = "Bạn đã yêu cầu khôi phục mật khẩu. Mã OTP là: " + otp + "\nCó hiệu lực trong 5 phút.";
                break;
            case "ACTIVATE":
            default:
                subject = "Kích hoạt tài khoản";
                message = "Mã OTP để kích hoạt tài khoản của bạn là: " + otp + "\nCó hiệu lực trong 5 phút.";
                break;
        }

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(toEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom(from);
        mailSender.send(mailMessage);
    }

}
