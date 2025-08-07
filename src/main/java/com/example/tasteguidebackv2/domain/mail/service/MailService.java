package com.example.tasteguidebackv2.domain.mail.service;

import com.example.tasteguidebackv2.common.exception.BizException;
import com.example.tasteguidebackv2.domain.mail.exception.MailErrorCode;
import com.example.tasteguidebackv2.domain.users.repository.RedisRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

import java.time.Duration;

/*
이메일의 제목, 본문, 수신자, 첨부파일 등 각종 속성을 간단한 메소드로 편리하게 설정
HTML 본문이나 여러 수신자, 첨부파일, 인라인 이미지와 같은 복잡한 이메일 작성 가능
문자 인코딩(UTF-8 등) 설정 간편
*/

@Slf4j
@Service("mailService")
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisRepository redisRepository;

    /**
     * 인증번호 메일 전송
     */
    public void sendVerificationCode(String email) {
        // 6자리 난수 생성
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);

        // Redis에 3분간 유효한 인증 코드 저장
        redisRepository.saveMailAuthCode(email, code, Duration.ofMinutes(3));

        // HTML 본문 문자열 직접 작성
        String html = String.format("""
                <html>
                  <body style="font-family: Arial, sans-serif;">
                    <h2>회원가입 이메일 인증 코드</h2>
                    <p>아래 인증 코드를 입력해주세요:</p>
                    <p style="font-size: 24px; font-weight: bold;">%s</p>
                    <p>3분 이내에 입력하지 않으면 코드가 만료됩니다.</p>
                  </body>
                </html>
                """, code);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("회원가입 이메일 인증 코드");
            helper.setText(html, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            log.error("이메일 발송 실패. 대상: {}, 원인: {}", email, e.getMessage(), e);
            throw new BizException(MailErrorCode.SEND_FAILED);
        }
    }

    /**
     * 인증 코드 검증
     */
    public void verifyCode(String email, String code) {
        String storedCode = redisRepository.getMailAuthCode(email);

        if (!storedCode.equals(code)) {
            throw new BizException(MailErrorCode.CODE_NOT_MATCHED);
        }

        // 인증 성공 → 인증 상태 30분간 유지
        redisRepository.save("EMAIL_VERIFIED:" + email, "true", Duration.ofMinutes(30));

        // 인증 코드 삭제
        redisRepository.deleteMailAuthCode(email);
    }
}
