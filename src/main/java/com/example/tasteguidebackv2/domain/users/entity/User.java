package com.example.tasteguidebackv2.domain.users.entity;

import com.example.tasteguidebackv2.common.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan(basePackages = "org.example.hansabal.domain.users.entity")
@Getter
@Entity
@Builder
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false, unique = true)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole userRole;

	public User(Long id, String email, String password, String name, String nickname, UserRole userRole) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.userRole = userRole;
	}

	public void updateUser(String nickname, String password) {
		if (nickname != null) {
			this.nickname = nickname;
		}
		if (password != null) {
			this.password = password;
		}
	}
}
