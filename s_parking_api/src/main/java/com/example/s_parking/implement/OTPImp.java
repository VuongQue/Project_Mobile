package com.example.s_parking.implement;

import com.example.s_parking.entity.OTP;
import com.example.s_parking.entity.User;
import com.example.s_parking.repository.OTPRepository;
import com.example.s_parking.repository.UserRepository;
import com.example.s_parking.service.EmailService;
import com.example.s_parking.service.OTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OTPImp implements OTPService {

    private final OTPRepository otpRepository;
    private final EmailService emailService;
    private final UserRepository userRepository; // <-- bổ sung dòng này

    @Override
    public void sendOTP(User user, String purpose) {
        String otp = String.valueOf((int) (Math.random() * 900000 + 100000));
        LocalDateTime now = LocalDateTime.now();

        OTP entity = new OTP();
        entity.setCode(otp);
        entity.setCreatedAt(now);
        entity.setExpiresAt(now.plusMinutes(5));
        entity.setPurpose(purpose);
        entity.setUsed(false);
        entity.setUser(user);
        otpRepository.save(entity);

        String studentEmail = user.getUsername() + "@student.hcmute.edu.vn";
        emailService.sendOTP(studentEmail, otp, purpose);
    }

    @Override
    public boolean verifyOTP(User user, String code, String purpose) {
        Optional<OTP> latest = otpRepository.findTopByUserAndPurposeAndIsUsedFalseOrderByCreatedAtDesc(user, purpose);
        if (latest.isEmpty()) return false;

        OTP otp = latest.get();
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        if (!otp.getCode().equals(code)) return false;

        otp.setUsed(true);
        otpRepository.save(otp);

        userRepository.activateUser(user.getUsername()); // gọi update

        return true;
    }
}
