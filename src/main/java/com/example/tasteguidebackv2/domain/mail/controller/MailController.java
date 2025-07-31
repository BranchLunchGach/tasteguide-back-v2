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
     * 비밀번호 재설정용 인증 코드 메일 발송
     */
    @PostMapping("/send-code")
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
