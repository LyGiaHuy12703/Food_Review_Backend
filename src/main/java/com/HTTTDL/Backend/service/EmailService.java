package com.HTTTDL.Backend.service;

import com.HTTTDL.Backend.dto.SendEmailDto;
import com.HTTTDL.Backend.exception.AppException;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String systemEmail;

    public void sendEmail(SendEmailDto emailPayload) {
        var message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(emailPayload.getTo());
            helper.setSubject(emailPayload.getSubject());
            helper.setText(emailPayload.getText(), true);
            helper.setFrom(systemEmail);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.out.print(e);
            throw new AppException(HttpStatus.BAD_REQUEST, "Send mail fail");
        }
    }

    public void sendEmailToVerifyRegister(String toEmail, String verificationCode) {
        String verifyUrl = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/auth/register/verify/{verificationCode}")
                .buildAndExpand(verificationCode)
                .toUriString();
        String emailText = String.format(
                "<p>Chào bạn,</p>"
                        + "<p>Cảm ơn bạn đã đăng ký tài khoản! Để hoàn tất, vui lòng xác thực tài khoản bằng cách nhấn vào liên kết bên dưới:</p>"
                        + "<p><a href='%s'>Xác thực tài khoản</a></p>"
                        + "<p>Trân trọng,<br><b>FOOD DELIVERY</b></p>",
                verifyUrl
        );
        SendEmailDto emailPayload = SendEmailDto.builder()
                .to(toEmail)
                .subject("Verity email to register")
                .text(emailText)
                .isHtml(true)
                .build();
        sendEmail(emailPayload);
    }

    public void sendEmailToWelcome(String toEmail) {
        String emailText = """
        <div style="font-family: Arial, sans-serif; line-height: 1.6;">
            <h2>Chào mừng bạn đến với FOOD DELIVERY!</h2>
            <p>Cảm ơn bạn đã tham gia cùng chúng tôi. Chúng tôi rất vui khi có bạn là một phần của cộng đồng <b>FOOD DELIVERY</b>.</p>
            <p>Chúc bạn có những trải nghiệm tuyệt vời khi sử dụng dịch vụ của chúng tôi!</p>
            <p>Trân trọng,<br><b>FOOD DELIVERY</b></p>
        </div>
    """;
        SendEmailDto emailPayload = SendEmailDto.builder()
                .to(toEmail)
                .subject("Chào mừng đến với FOOD DELIVERY!")
                .text(emailText)
                .isHtml(true)  // Gửi email dưới dạng HTML
                .build();

        sendEmail(emailPayload);
    }
}
