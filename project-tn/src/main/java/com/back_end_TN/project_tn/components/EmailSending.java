package com.back_end_TN.project_tn.components;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailSending {
    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String data) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("nguyenxuanmanh2992003@gmail.com"); // Địa chỉ gửi
            helper.setText(buildEmailContent(data), true); // Cho phép HTML

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }

    private String buildEmailContent(String data) {
        return "<div style='font-family: Arial, sans-serif; padding: 20px; border: 1px solid #ddd; border-radius: 10px; max-width: 500px; background: #f9f9f9;'>"
                + "<h2 style='color: #333;'>🔑 Xác nhận OTP của bạn</h2>"
                + "<p>Chào bạn,</p>"
                + "<p>Đây là mã OTP của bạn để xác nhận: <strong style='font-size: 18px; color: #d9534f;'>" + data + "</strong></p>"
                + "<p>Mã OTP này chỉ có hiệu lực trong <strong>2 phút</strong>. Vui lòng không chia sẻ mã này với bất kỳ ai.</p>"
                + "<hr style='border: none; border-top: 1px solid #ddd;'/>"
                + "<p style='font-size: 12px; color: #666;'>Nếu bạn không yêu cầu OTP này, vui lòng bỏ qua email này.</p>"
                + "<p style='font-size: 12px; color: #666;'>Cảm ơn bạn,<br/>Hệ thống Học Trực Tuyến</p>"
                + "</div>";
    }
}
