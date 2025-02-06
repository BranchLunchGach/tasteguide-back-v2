package com.example.tasteguidebackv2.domain.users.controller;

import com.example.tasteguidebackv2.common.jwt.UserAuth;
import com.example.tasteguidebackv2.common.response.ApiResponse;
import com.example.tasteguidebackv2.domain.users.dto.request.UserCreateRequest;
import com.example.tasteguidebackv2.domain.users.dto.request.UserUpdateRequest;
import com.example.tasteguidebackv2.domain.users.dto.response.UserResponse;
import com.example.tasteguidebackv2.domain.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> createUser(@Valid @RequestBody UserCreateRequest request) {
		userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success("회원가입이 완료되었습니다."));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserResponse>> findById(@AuthenticationPrincipal UserAuth userAuth) {
		UserResponse response = userService.findById(userAuth);
		return ResponseEntity.ok(ApiResponse.success("회원 조회 성공", response));
	}

	@PatchMapping
	public ResponseEntity<ApiResponse<Void>> updateUser(@Valid @RequestBody UserUpdateRequest request,
														@AuthenticationPrincipal UserAuth userAuth) {
		userService.updateUser(request, userAuth);
		return ResponseEntity.ok(ApiResponse.success("회원 정보 수정 완료"));
	}

	@DeleteMapping
	public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal UserAuth userAuth) {
		userService.deleteUser(userAuth);
		return ResponseEntity.ok(ApiResponse.success("회원 탈퇴 완료"));
	}
}
