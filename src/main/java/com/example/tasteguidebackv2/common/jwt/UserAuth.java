package com.example.tasteguidebackv2.common.jwt;

import com.example.tasteguidebackv2.domain.users.entity.UserRole;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserAuth {
	private final Long id;
	private final UserRole userRole;
}