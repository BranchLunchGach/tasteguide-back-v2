package com.example.tasteguidebackv2.domain.users.repository;

import com.example.tasteguidebackv2.common.exception.BizException;
import com.example.tasteguidebackv2.domain.users.entity.User;
import com.example.tasteguidebackv2.domain.users.exception.UserErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	default User findByEmailOrElseThrow(String email) {
		return findByEmail(email).orElseThrow(() -> new BizException(UserErrorCode.NOT_FOUND_USER));
	}

	default User findByIdOrElseThrow(Long id) {
		return findById(id)
				.filter(user -> !user.isDeleted())
				.orElseThrow(() -> new BizException(UserErrorCode.NOT_FOUND_USER));
	}

	boolean existsByNickname(String nickname);
}
