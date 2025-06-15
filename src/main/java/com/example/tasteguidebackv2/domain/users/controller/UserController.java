package com.example.tasteguidebackv2.domain.users.controller;

import com.example.tasteguidebackv2.common.jwt.UserAuth;
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
	public ResponseEntity<Void> createUser(@Valid @RequestBody UserCreateRequest request) {
		userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponse> findById(@AuthenticationPrincipal UserAuth userAuth) {
		return ResponseEntity.status(HttpStatus.OK).body(userService.findById(userAuth));
	}

	@PatchMapping
	public ResponseEntity<Void> updateUser(@Valid @RequestBody UserUpdateRequest request, @AuthenticationPrincipal UserAuth userAuth) {
		userService.updateUser(request, userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@DeleteMapping
	public ResponseEntity<Void> deleteUser(@AuthenticationPrincipal UserAuth userAuth) {
		userService.deleteUser(userAuth);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
