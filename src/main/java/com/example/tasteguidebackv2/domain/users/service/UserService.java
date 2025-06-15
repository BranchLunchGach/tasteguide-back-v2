package com.example.tasteguidebackv2.domain.users.service;

import com.example.tasteguidebackv2.common.exception.BizException;
import com.example.tasteguidebackv2.common.jwt.UserAuth;
import com.example.tasteguidebackv2.domain.users.dto.request.UserCreateRequest;
import com.example.tasteguidebackv2.domain.users.dto.request.UserUpdateRequest;
import com.example.tasteguidebackv2.domain.users.dto.response.UserResponse;
import com.example.tasteguidebackv2.domain.users.entity.User;
import com.example.tasteguidebackv2.domain.users.exception.UserErrorCode;
import com.example.tasteguidebackv2.domain.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public void createUser(@RequestBody UserCreateRequest request) {

		if (userRepository.existsByEmail(request.email())) {
			throw new BizException(UserErrorCode.DUPLICATE_USER_EMAIL);
		}

		String encodedPassword = passwordEncoder.encode(request.password());

		User user = User.builder()
			.email(request.email())
			.password(encodedPassword)
			.name(request.name())
			.nickname(request.nickname())
			.userRole(request.userRole())
			.build();

		userRepository.save(user);
	}

	public UserResponse findById(UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		return UserResponse.from(findUser);
	}

	@Transactional
	public void updateUser(UserUpdateRequest request, UserAuth userAuth) {
		User findUser = userRepository.findByIdOrElseThrow(userAuth.getId());
		checkPassword(request.oldPassword(), findUser.getPassword());

		if (request.nickname() == null && request.newPassword() == null) {
			throw new BizException(UserErrorCode.NO_UPDATE_TARGET);
		}
		if (request.nickname() != null && findUser.getNickname().equals(request.nickname())) {
			throw new BizException(UserErrorCode.NICKNAME_NOT_CHANGED);
		}
		if (request.newPassword() != null && passwordEncoder.matches(request.newPassword(), findUser.getPassword())) {
			throw new BizException(UserErrorCode.PASSWORD_NOT_CHANGED);
		}

		String encodedPassword = null;
		if (request.newPassword() != null) {
			encodedPassword = passwordEncoder.encode(request.newPassword());
		}

		findUser.updateUser(request.nickname(), encodedPassword);
	}

	private void checkPassword(String rawPassword, String hashedPassword) {
		if (!passwordEncoder.matches(rawPassword, hashedPassword)) {
			throw new BizException(UserErrorCode.INVALID_PASSWORD);
		}
	}

	@Transactional
	public void deleteUser(UserAuth userAuth) {
		User user = userRepository.findByIdOrElseThrow(userAuth.getId());
		user.softDelete();
		// 추후 유저관련 내용 삭제 로직 추가
	}
}
