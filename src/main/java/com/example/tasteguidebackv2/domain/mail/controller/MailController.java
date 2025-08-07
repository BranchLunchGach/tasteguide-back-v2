package com.example.tasteguidebackv2.domain.mail.controller;

import com.example.tasteguidebackv2.domain.mail.dto.request.MailAuthCodeRequest;
import com.example.tasteguidebackv2.domain.mail.dto.request.MailAuthCodeVerifyRequest;
import com.example.tasteguidebackv2.domain.mail.service.MailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class MailController {

    private final MailService mailService;

    /**
     * 회원가입용 인증 코드 메일 발송
     */
    @PostMapping("/send-code")
    // @RateLimiter(name = "sendCode", fallbackMethod = "sendCodeFallback")
    // 요청 제한 같은 IP에서 1분에 1번만 호출 가능, 같은 이메일 주소로 하루에 5번만 가능(라이브러리 설치 필요) -> 레디스로도 구현 가능
    public ResponseEntity<Void> sendCode(@RequestBody @Valid MailAuthCodeRequest request) {
        mailService.sendVerificationCode(request.email());
        return ResponseEntity.ok().build();
    }

    /**
     * 인증 코드 검증
     */
    @PostMapping("/verify-code")
    public ResponseEntity<Void> verifyCode(@RequestBody @Valid MailAuthCodeVerifyRequest request) {
        mailService.verifyCode(request.email(), request.code());
        return ResponseEntity.ok().build(); // 성공 시 200 OK (응답 바디 없음)
    }
}
