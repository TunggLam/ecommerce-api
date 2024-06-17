package com.example.ecommercewebsite.service.impl;

import com.example.ecommercewebsite.constant.Constant;
import com.example.ecommercewebsite.entity.UserProfile;
import com.example.ecommercewebsite.entity.UserProfileOTP;
import com.example.ecommercewebsite.enums.OTPTypeEnum;
import com.example.ecommercewebsite.exception.BusinessException;
import com.example.ecommercewebsite.model.request.SendOTPRequest;
import com.example.ecommercewebsite.repositories.UserProfileOTPRepository;
import com.example.ecommercewebsite.repositories.UserProfileRepository;
import com.example.ecommercewebsite.service.OTPService;
import com.example.ecommercewebsite.utils.RegexUtils;
import com.example.ecommercewebsite.utils.StringUtils;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OTPServiceImpl implements OTPService {
    private static final String CONTENT = "<h2>Mã xác thực đăng ký tại Ecommerce</h2><p>Xin chào: %s,</p><p>Mã OTP của bạn là: <strong>%s</strong></p><p>Vui lòng sử dụng mã OTP này để xác thực đăng ký tài khoản của bạn</p><p>Lưu ý không chia sẻ mã OTP này cho bất kỳ ai.</p><br><p>Trân trọng,</p><p>Ecommerce</p>";
    private static final String SUBJECT = "Mã xác thực đăng ký tài khoản tại Ecommerce";
    private static final int TOTAL_RETRY_PER_DAY = 3;
    private static final int TOTAL_FALSE_OTP = 5;

    @Value("${spring.mail.username}")
    private String usernameMail;

    private final UserProfileRepository userProfileRepository;
    private final UserProfileOTPRepository userProfileOTPRepository;
    private final JavaMailSender mailSender;

    @Override
    public void sendOTP(SendOTPRequest request) {
        /* Kiểm tra Email có đúng định dạng hay không, và username */
        validateRequest(request);

        String username = request.getUsername();
        validateUserExist(username);

        validateTotalOTPInDay(username);

        validateCountVerifyOTP(request);

        userProfileOTPRepository.inactiveAllStatus(username, OTPTypeEnum.REGISTER.name());

        String otp = generateOTP();

        sendEmail(request, username, otp);

        saveUserProfileOTP(request, otp);
    }

    private static void validateRequest(SendOTPRequest request){
        if (StringUtils.isNullOrEmpty(request.getEmail()) || RegexUtils.matches(request.getEmail(), RegexUtils.EMAIL)){
            throw new BusinessException("Email không hợp lệ");
        }

        if (StringUtils.isNullOrEmpty(request.getUsername())){
            throw new BusinessException(("Username không được để trống"));
        }
    }

    private void validateUserExist(String username){
        Optional<UserProfile> userProfileOptional = userProfileRepository.findByUsername(username);
        if(userProfileOptional.isPresent()) {
            throw new BusinessException(Constant.USERNAME_EXISTS);
        }
    }

    private void validateTotalOTPInDay(String username){
        int totalOTPToday = userProfileOTPRepository.getTotalOTPToday(username, OTPTypeEnum.REGISTER.name());
        if(totalOTPToday >= TOTAL_RETRY_PER_DAY) {
            throw new BusinessException(Constant.OTP_EXCEEDED);
        }
    }

    private void validateCountVerifyOTP(SendOTPRequest request){
        UserProfileOTP userProfileOTP = userProfileOTPRepository.getLatestOTP(request.getUsername(), OTPTypeEnum.REGISTER.name());
        if(Objects.nonNull(userProfileOTP)){
            int countVerifyFail = userProfileOTP.getCountVerifyFalse() == null ? 0 : userProfileOTP.getCountVerifyFalse();
            if (Boolean.FALSE.equals(userProfileOTP.getStatus()) && countVerifyFail >= TOTAL_FALSE_OTP){
                throw new BusinessException(String.format(Constant.VERIFY_OTP_BLOCKED_MESS, TOTAL_FALSE_OTP));
            }
        }
    }

    private static String generateOTP(){
        int otp = new SecureRandom().nextInt(999999);
        return String.format("%06d", otp);
    }

    private void sendEmail(SendOTPRequest request, String username, String otp){
        try{
            MimeMessage message = mailSender.createMimeMessage();
            message.setFrom(new InternetAddress(usernameMail));
            message.setRecipients(Message.RecipientType.TO, request.getEmail());
            message.setSubject(SUBJECT);
            message.setContent(String.format(CONTENT, request.getUsername(), otp), "text/html; charset=utf-8");
            mailSender.send(message);
        } catch (MessagingException e){
            throw new BusinessException(Constant.EXCEPTION_MESSAGE_DEFAULT);
        }
    }

    private void saveUserProfileOTP(SendOTPRequest request, String otp){
        UserProfileOTP userProfileOTP = new UserProfileOTP();
        userProfileOTP.setOtp(otp);
        userProfileOTP.setUsername(request.getUsername());
        userProfileOTP.setType(StringUtils.isNullOrEmpty(request.getType()) ? OTPTypeEnum.REGISTER.name() : request.getType());
        userProfileOTP.setStatus(true);
        userProfileOTP.setCountVerifyFalse(0);
        userProfileOTPRepository.save(userProfileOTP);
    }
}
