package com.example.tasteguidebackv2.domain.users.entity;

import com.example.tasteguidebackv2.common.base.BaseEntity;
import com.example.tasteguidebackv2.domain.menu.entity.Menu;
import com.example.tasteguidebackv2.domain.restaurant.entity.Restaurant;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
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

	private String address;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String phone;

	private String taste;

	@Column(name = "birth_date")
	private LocalDateTime birthDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole userRole;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Restaurant> restaurants = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Menu> menus = new ArrayList<>();

	public User(
		String email, String password, String name, String nickname,
		String address, Gender gender, String phone,
		String taste, LocalDateTime birthDate, UserRole userRole
	) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.nickname = nickname;
		this.address = address;
		this.gender = gender;
		this.phone = phone;
		this.taste = taste;
		this.birthDate = birthDate;
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

