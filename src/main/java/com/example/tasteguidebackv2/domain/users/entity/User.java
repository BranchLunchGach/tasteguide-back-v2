package com.example.tasteguidebackv2.domain.users.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User {

	@Id
	@Column(name = "user_no")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int userNo;

	@Column(name = "user_id", unique = true)
	private String userId;
	private String password;
	private String name;
	private String address;

	@Enumerated(EnumType.STRING)
	private Gender gender; // Boolean 어떤지..?

	private String phone;
	private String taste; // 취향(,구분)

	@Column(name = "birth_date")
	private LocalDate birthDate;
	private String role;

	@Override
	public String toString() {
		return "User [userNo=" + userNo + ", userId=" + userId + ", password=" + password + ", name=" + name
				+ ", address=" + address + ", gender=" + gender + ", phone=" + phone + ", taste=" + taste
				+ ", birthDate=" + birthDate + ", role=" + role + "]";
	}
}