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
    public void sendOTP(String toEmail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Mã xác thực OTP");
        message.setText("Mã OTP của bạn là: " + otp + "\nCó hiệu lực trong 5 phút.");
        message.setFrom(from);
        mailSender.send(message);
    }
}
